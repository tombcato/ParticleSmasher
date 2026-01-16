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
     */
    public RiseParticle(int direction, Point point, int color, int radius, Rect rect, float endValue, Random random, float horizontalMultiple, float verticalMultiple, float startRandomness, float endRandomness, int scaleMode) {

        this.color = color;
        this.baseAlpha = android.graphics.Color.alpha(color);
        this.direction = direction;
        this.scaleMode = scaleMode;
        alpha = 1;

        float nextFloat = random.nextFloat();

        baseRadius = getBaseRadius(radius, random, nextFloat);
        this.radius = baseRadius;

        // 水平方向随机偏移
        horizontalElement = getHorizontalElement(rect, random, nextFloat, horizontalMultiple);
        // 垂直方向向上移动距离
        verticalElement = getVerticalElement(rect, random, nextFloat, verticalMultiple);

        baseCx = point.x;
        baseCy = point.y;
        cx = baseCx;
        cy = baseCy;

        // 计算粒子在 View 中的位置比例
        left = (baseCx - rect.left) / (float) rect.width();
        top = (baseCy - rect.top) / (float) rect.height();

        // 随机延迟启动
        font = endValue * startRandomness * random.nextFloat();
        later = endRandomness * random.nextFloat();
    }

    private static float getBaseRadius(float radius, Random random, float nextFloat) {
        float r = radius + radius * (random.nextFloat() - 0.5f) * 0.5f;
        r = nextFloat < 0.6f ? r :
                nextFloat < 0.8f ? r * 1.2f : r * 1.4f;
        return r;
    }

    private static float getHorizontalElement(Rect rect, Random random, float nextFloat, float horizontalMultiple) {
        // 水平方向随机左右偏移
        float horizontal = rect.width() * (random.nextFloat() - 0.5f) * 0.5f;
        horizontal = nextFloat < 0.2f ? horizontal :
                nextFloat < 0.8f ? horizontal * 0.6f : horizontal * 0.3f;
        return horizontal * horizontalMultiple;
    }

    private static float getVerticalElement(Rect rect, Random random, float nextFloat, float verticalMultiple) {
        // 向上移动的距离（正值，在 advance 中会取负）
        float vertical = rect.height() * (random.nextFloat() * 0.5f + 0.5f);
        vertical = nextFloat < 0.2f ? vertical :
                nextFloat < 0.8f ? vertical * 1.2f : vertical * 1.4f;
        return vertical * verticalMultiple;
    }

    @Override
    public void advance(float factor, float endValue) {
        float normalization = factor / endValue;

        if (normalization < font) {
            alpha = 1;
            return;
        }

        if (normalization > 1f - later) {
            alpha = 0;
            return;
        }
        alpha = 1;

        normalization = (normalization - font) / (1f - font - later);
        
        // 超过 60% 开始变透明
        if (normalization >= 0.6f) {
            alpha = 1f - (normalization - 0.6f) / 0.4f;
        }

        float realValue = normalization * endValue;
        float progress = 0;

        // 根据方向决定粒子何时开始移动
        switch (direction) {
            case DIRECTION_LEFT:
                // 从左往右：左边的粒子先动
                if (realValue > left) {
                    progress = realValue - left;
                    cy = baseCy - verticalElement * progress;
                    cx = baseCx + horizontalElement * progress;
                }
                break;
            case DIRECTION_RIGHT:
                // 从右往左：右边的粒子先动
                if (realValue > (1 - left)) {
                    progress = realValue - (1 - left);
                    cy = baseCy - verticalElement * progress;
                    cx = baseCx + horizontalElement * progress;
                }
                break;
            case DIRECTION_TOP:
                // 从上到下：上面的粒子先动
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

        if (scaleMode == 2) { // 2 = SCALE_UP from SmashAnimator
             radius = baseRadius + baseRadius / 4 * progress;
        } else if (scaleMode == 1) { // 1 = SCALE_SAME
             radius = baseRadius;
        } else { // 0 = SCALE_DOWN
             radius = baseRadius * (1f - progress * 0.3f);
        }

        if (radius < 0) radius = 0;
    }
}
