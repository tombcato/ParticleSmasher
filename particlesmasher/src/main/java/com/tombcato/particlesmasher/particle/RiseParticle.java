package com.tombcato.particlesmasher.particle;

import android.graphics.Point;
import android.graphics.Rect;

import java.util.Random;

/**
 * 向上飘散粒子
 * 支持从左往右、从右往左、同时向上的飘散效果
 */
public class RiseParticle extends Particle {

    // 方向常量
    public static final int DIRECTION_ALL = 0;      // 同时向上（默认）
    public static final int DIRECTION_LEFT = 1;     // 从左往右逐列向上
    public static final int DIRECTION_RIGHT = 2;    // 从右往左逐列向上
    public static final int DIRECTION_TOP = 3;      // 从上到下逐行向上

    private int direction = DIRECTION_ALL;
    private float left;  // 粒子在 View 中的水平位置比例 (0~1)
    private float top;   // 粒子在 View 中的垂直位置比例 (0~1)

    /**
     * 生成向上飘散的粒子
     * @param direction          方向
     * @param point              粒子在图片中的位置
     * @param color              粒子颜色
     * @param radius             粒子的半径
     * @param rect               View区域的矩形
     * @param endValue           动画的结束值
     * @param random             随机数
     * @param horizontalMultiple 水平变化幅度
     * @param verticalMultiple   垂直变化幅度（向上移动的距离）
     * @param startRandomness    起跑随机延迟系数
     * @param endRandomness      结束随机提前系数
     * @param scaleMode          缩放模式
     */
    public RiseParticle(int direction, Point point, int color, int radius, Rect rect, float endValue, 
                        Random random, float horizontalMultiple, float verticalMultiple, 
                        float startRandomness, float endRandomness, ScaleMode scaleMode) {

        this.color = color;
        this.baseAlpha = android.graphics.Color.alpha(color);
        this.direction = direction;
        this.scaleMode = scaleMode;
        alpha = 1;

        float nextFloat = random.nextFloat();

        // Rise 粒子半径略有增大
        baseRadius = calculateBaseRadius(radius, random, nextFloat, 1.2f, 1.4f);
        this.radius = baseRadius;

        // 水平方向随机偏移（幅度较小）
        horizontalElement = calculateHorizontalElement(rect, random, nextFloat, horizontalMultiple) * 0.5f;
        // 垂直方向向上移动距离
        verticalElement = calculateVerticalElement(rect, random, nextFloat, verticalMultiple);

        baseCx = point.x;
        baseCy = point.y;
        cx = baseCx;
        cy = baseCy;

        // 计算粒子在 View 中的位置比例
        left = (baseCx - rect.left) / (float) rect.width();
        top = (baseCy - rect.top) / (float) rect.height();

        startOffset = endValue * startRandomness * random.nextFloat();
        endFadeOffset = endRandomness * random.nextFloat();
    }

    @Override
    public void advance(float factor, float endValue) {
        float normalization = factor / endValue;

        if (normalization < startOffset) {
            alpha = 1;
            return;
        }

        if (normalization > 1f - endFadeOffset) {
            alpha = 0;
            return;
        }
        alpha = 1;

        normalization = (normalization - startOffset) / (1f - startOffset - endFadeOffset);
        
        // Rise 使用不同的淡出阈值
        alpha = calculateFadeAlpha(normalization, RISE_FADE_START_THRESHOLD, RISE_FADE_DURATION_RATIO);

        float realValue = normalization * endValue;
        float progress = 0;

        // 根据方向决定粒子何时开始移动
        switch (direction) {
            case DIRECTION_LEFT:
                if (realValue > left) {
                    progress = realValue - left;
                    cy = baseCy - verticalElement * progress;
                    cx = baseCx + horizontalElement * progress;
                }
                break;
            case DIRECTION_RIGHT:
                if (realValue > (1 - left)) {
                    progress = realValue - (1 - left);
                    cy = baseCy - verticalElement * progress;
                    cx = baseCx + horizontalElement * progress;
                }
                break;
            case DIRECTION_TOP:
                if (realValue > top) {
                    progress = realValue - top;
                    cy = baseCy - verticalElement * progress;
                    cx = baseCx + horizontalElement * progress;
                }
                break;
            default:
                // 同时向上
                progress = realValue;
                cy = baseCy - verticalElement * realValue;
                cx = baseCx + horizontalElement * realValue;
                break;
        }

        // Rise 使用不同的缩放因子
        radius = calculateRadius(baseRadius, progress * 0.3f, scaleMode, 0.25f);
        if (radius < 0) radius = 0;
    }
}
