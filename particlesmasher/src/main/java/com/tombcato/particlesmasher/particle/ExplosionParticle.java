package com.tombcato.particlesmasher.particle;

import android.graphics.Rect;

import java.util.Random;

/**
 * <pre>
 *     author : FaDai
 *     e-mail : i_fadai@163.com
 *     time   : 2017/12/14
 *     desc   : 爆炸粒子
 *     version: 2.0
 * </pre>
 */

public class ExplosionParticle extends Particle {

    /**
     * 生成粒子
     *
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
    public ExplosionParticle(int color, int radius, Rect rect, float endValue, Random random, 
                             float horizontalMultiple, float verticalMultiple, 
                             float startRandomness, float endRandomness, ScaleMode scaleMode) {

        this.color = color;
        this.baseAlpha = android.graphics.Color.alpha(color);
        this.scaleMode = scaleMode;
        alpha = 1;

        float nextFloat = random.nextFloat();

        // 使用基类方法计算
        baseRadius = calculateBaseRadius(radius, random, nextFloat, 1.4f, 0.8f);
        this.radius = baseRadius;

        horizontalElement = calculateHorizontalElement(rect, random, nextFloat, horizontalMultiple);
        verticalElement = calculateVerticalElement(rect, random, nextFloat, verticalMultiple);

        int offsetX = rect.width() / 4;
        int offsetY = rect.height() / 4;

        baseCx = rect.centerX() + offsetX * (random.nextFloat() - 0.5f);
        baseCy = rect.centerY() + offsetY * (random.nextFloat() - 0.5f);
        cx = baseCx;
        cy = baseCy;

        startOffset = endValue * startRandomness * random.nextFloat();
        endFadeOffset = endRandomness * random.nextFloat();
    }

    @Override
    public void advance(float factor, float endValue) {
        float normalization = factor / endValue;

        if (normalization < startOffset || normalization > 1f - endFadeOffset) {
            alpha = 0;
            return;
        }
        alpha = 1;

        // 粒子可显示的状态中，动画实际进行到了几分之几
        normalization = (normalization - startOffset) / (1f - startOffset - endFadeOffset);
        
        // 使用基类常量计算淡出
        alpha = calculateFadeAlpha(normalization, FADE_START_THRESHOLD, FADE_DURATION_RATIO);

        float realValue = normalization * endValue;

        cx = baseCx + horizontalElement * realValue;
        cy = baseCy + verticalElement * (realValue * (realValue - 1));

        // 使用基类方法计算半径
        radius = calculateRadius(baseRadius, normalization, scaleMode, 0.25f);
    }
}
