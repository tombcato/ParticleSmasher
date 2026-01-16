package com.tombcato.particlesmasher.particle;

import android.graphics.Rect;

import java.util.Random;

/**
 * <pre>
 *     author : FaDai
 *     e-mail : i_fadai@163.com
 *     time   : 2017/12/20
 *     desc   : 粒子基类
 *     version: 2.0
 * </pre>
 */

public abstract class Particle {

    // ==================== 动画常量 ====================
    
    /** 淡出开始阈值（动画进度达到此值时开始淡出） */
    protected static final float FADE_START_THRESHOLD = 0.7f;
    
    /** 淡出持续比例 */
    protected static final float FADE_DURATION_RATIO = 0.3f;  // 1 - FADE_START_THRESHOLD
    
    /** Rise 动画的淡出开始阈值（比其他动画更早开始淡出） */
    protected static final float RISE_FADE_START_THRESHOLD = 0.6f;
    
    /** Rise 动画的淡出持续比例 */
    protected static final float RISE_FADE_DURATION_RATIO = 0.4f;

    // ==================== 缩放模式 ====================
    
    public enum ScaleMode {
        /** 逐渐变小（默认） */
        SCALE_DOWN,
        /** 大小不变 */
        SCALE_SAME,
        /** 逐渐变大 */
        SCALE_UP
    }

    // ==================== 粒子属性 ====================
    
    public int color;                // 颜色
    public int baseAlpha;            // 初始透明度 (0~255)
    public float radius;             // 半径
    public float alpha;              // 透明度（0~1）
    public float cx;                 // 圆心 x
    public float cy;                 // 圆心 y

    public float horizontalElement;  // 水平变化参数
    public float verticalElement;    // 垂直变化参数

    public float baseRadius;         // 初始半径
    public float baseCx;             // 初始圆心 x
    public float baseCy;             // 初始圆心 y

    /** 粒子延迟启动的时间偏移 (原名 font) */
    public float startOffset;
    
    /** 粒子提前消失的时间偏移 (原名 later) */
    public float endFadeOffset;
    
    public ScaleMode scaleMode;      // 缩放模式

    // ==================== 公共计算方法 ====================
    
    /**
     * 计算粒子基础半径（带随机变化）
     * @param radius 基础半径
     * @param random 随机数生成器
     * @param nextFloat 随机因子 (0~1)
     * @param smallMultiplier 小概率缩放因子（nextFloat 0.6~0.8 时使用）
     * @param largeMultiplier 大概率缩放因子（nextFloat > 0.8 时使用）
     */
    protected static float calculateBaseRadius(float radius, Random random, float nextFloat, 
                                               float smallMultiplier, float largeMultiplier) {
        float r = radius + radius * (random.nextFloat() - 0.5f) * 0.5f;
        if (nextFloat < 0.6f) {
            return r;
        } else if (nextFloat < 0.8f) {
            return r * smallMultiplier;
        } else {
            return r * largeMultiplier;
        }
    }
    
    /**
     * 计算水平变化参数
     */
    protected static float calculateHorizontalElement(Rect rect, Random random, float nextFloat, float multiplier) {
        float horizontal = rect.width() * (random.nextFloat() - 0.5f);
        if (nextFloat < 0.2f) {
            horizontal = horizontal;
        } else if (nextFloat < 0.8f) {
            horizontal = horizontal * 0.6f;
        } else {
            horizontal = horizontal * 0.3f;
        }
        return horizontal * multiplier;
    }
    
    /**
     * 计算垂直变化参数
     */
    protected static float calculateVerticalElement(Rect rect, Random random, float nextFloat, float multiplier) {
        float vertical = rect.height() * (random.nextFloat() * 0.5f + 0.5f);
        if (nextFloat < 0.2f) {
            vertical = vertical;
        } else if (nextFloat < 0.8f) {
            vertical = vertical * 1.2f;
        } else {
            vertical = vertical * 1.4f;
        }
        return vertical * multiplier;
    }
    
    /**
     * 计算淡出 alpha 值
     * @param normalization 归一化的动画进度
     * @param fadeStartThreshold 开始淡出的阈值
     * @param fadeDurationRatio 淡出持续比例
     * @return alpha 值 (0~1)
     */
    protected static float calculateFadeAlpha(float normalization, float fadeStartThreshold, float fadeDurationRatio) {
        if (normalization >= fadeStartThreshold) {
            return 1f - (normalization - fadeStartThreshold) / fadeDurationRatio;
        }
        return 1f;
    }
    
    /**
     * 根据缩放模式计算半径
     * @param baseRadius 基础半径
     * @param progress 动画进度 (0~1)
     * @param scaleMode 缩放模式
     * @param scaleFactor 缩放因子（SCALE_UP 时使用）
     */
    protected static float calculateRadius(float baseRadius, float progress, ScaleMode scaleMode, float scaleFactor) {
        switch (scaleMode) {
            case SCALE_UP:
                return baseRadius + baseRadius * scaleFactor * progress;
            case SCALE_SAME:
                return baseRadius;
            case SCALE_DOWN:
            default:
                return baseRadius * (1f - progress);
        }
    }

    /**
     * 更新粒子状态
     * @param factor 当前动画值
     * @param endValue 动画结束值
     */
    public abstract void advance(float factor, float endValue);
}
