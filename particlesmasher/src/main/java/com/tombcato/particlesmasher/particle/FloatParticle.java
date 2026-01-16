package com.tombcato.particlesmasher.particle;

import android.graphics.Point;
import android.graphics.Rect;

import java.util.Random;

/**
 * <pre>
 *     author : FaDai
 *     e-mail : i_fadai@163.com
 *     time   : 2017/12/20
 *     desc   : 飘落粒子
 *     version: 2.0
 * </pre>
 */

public class FloatParticle extends Particle {

    public static final int ORIENTATION_LEFT = 1;
    public static final int ORIENTATION_RIGHT = 2;
    public static final int ORIENTATION_TOP = 3;
    public static final int ORIENTATION_BOTTOM = 4;

    private float top;
    private float left;
    private int orientation = ORIENTATION_TOP;

    /**
     * 生成粒子
     * @param orientation        方向
     * @param point              粒子在图片中的位置
     * @param color              粒子颜色
     * @param radius             粒子的半径
     * @param rect               View区域的矩形
     * @param endValue           动画的结束值
     * @param random             随机数
     * @param horizontalMultiple 水平变化幅度
     * @param verticalMultiple   垂直变化幅度
     * @param startRandomness    起跑随机延迟系数
     * @param endRandomness      结束随机提前系数
     * @param scaleMode          缩放模式
     */
    public FloatParticle(int orientation, Point point, int color, int radius, Rect rect, float endValue, 
                         Random random, float horizontalMultiple, float verticalMultiple, 
                         float startRandomness, float endRandomness, ScaleMode scaleMode) {
        
        this.color = color;
        this.baseAlpha = android.graphics.Color.alpha(color);
        this.scaleMode = scaleMode;
        alpha = 1;

        float nextFloat = random.nextFloat();

        // 下落和飘落的粒子，其半径很大概率大于初始设定的半径
        baseRadius = calculateBaseRadius(radius, random, nextFloat, 1.4f, 1.6f);
        this.radius = baseRadius;

        horizontalElement = calculateHorizontalElement(rect, random, nextFloat, horizontalMultiple);
        verticalElement = calculateVerticalElement(rect, random, nextFloat, verticalMultiple);

        baseCx = point.x;
        baseCy = point.y;
        cx = baseCx;
        cy = baseCy;

        startOffset = endValue * startRandomness * random.nextFloat();
        endFadeOffset = endRandomness * random.nextFloat();

        left = (baseCx - rect.left) / rect.width();
        top = (baseCy - rect.top) / rect.height();
        this.orientation = orientation;
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
        alpha = calculateFadeAlpha(normalization, FADE_START_THRESHOLD, FADE_DURATION_RATIO);

        float realValue = normalization * endValue;

        switch (orientation) {
            case ORIENTATION_LEFT:
                if (realValue > left) {
                    cy = baseCy + verticalElement * (realValue - left);
                    cx = baseCx + horizontalElement * (realValue - left);
                }
                break;
            case ORIENTATION_RIGHT:
                if (realValue > (1 - left)) {
                    cy = baseCy + verticalElement * (realValue - (1 - left));
                    cx = baseCx + horizontalElement * (realValue - (1 - left));
                }
                break;
            case ORIENTATION_TOP:
                if (realValue > top) {
                    cy = baseCy + verticalElement * (realValue - top);
                    cx = baseCx + horizontalElement * (realValue - top);
                }
                break;
            case ORIENTATION_BOTTOM:
                if (realValue > (1 - top)) {
                    cy = baseCy + verticalElement * (realValue - (1 - top));
                    cx = baseCx + horizontalElement * (realValue - (1 - top));
                }
                break;
        }

        radius = calculateRadius(baseRadius, normalization, scaleMode, 1f / 6f);
    }
}
