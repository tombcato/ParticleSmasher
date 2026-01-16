package com.tombcato.particlesmasher.particle;

import android.graphics.Point;
import android.graphics.Rect;

import java.util.Random;

/**
 * <pre>
 *     author : FaDai
 *     e-mail : i_fadai@163.com
 *     time   : 2017/12/20
 *     desc   : 下落粒子
 *     version: 2.0
 * </pre>
 */

public class DropParticle extends Particle {

    /**
     * 生成粒子
     * @param point              粒子在图片中原始位置
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
    public DropParticle(Point point, int color, int radius, Rect rect, float endValue, Random random, 
                        float horizontalMultiple, float verticalMultiple, 
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

        cx = baseCx + horizontalElement * realValue;
        cy = baseCy + verticalElement * realValue;

        radius = calculateRadius(baseRadius, normalization, scaleMode, 1f / 6f);
    }
}
