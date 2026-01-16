package com.fadai.particlesmasher;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;

import com.fadai.particlesmasher.particle.DropParticle;
import com.fadai.particlesmasher.particle.ExplosionParticle;
import com.fadai.particlesmasher.particle.FloatParticle;
import com.fadai.particlesmasher.particle.Particle;
import com.fadai.particlesmasher.particle.RiseParticle;

import java.util.Random;

/**
 * <pre>
 *     author : FaDai
 *     e-mail : i_fadai@163.com
 *     time   : 2017/12/14
 *     desc   : xxxx描述
 *     version: 1.0
 * </pre>
 */

public class SmashAnimator {

    public static final int STYLE_EXPLOSION=1,       // 爆炸
            STYLE_DROP=2,                            // 下落
            STYLE_FLOAT_LEFT=3,                      // 飘落——>自左往右，逐列飘落
            STYLE_FLOAT_RIGHT=4,                     // 飘落——>自右往左，逐列飘落
            STYLE_FLOAT_TOP=5,                       // 飘落——>自上往下，逐行飘落
            STYLE_FLOAT_BOTTOM=6,                    // 飘落——>自下往上，逐行飘落
            STYLE_RISE=7,                            // 向上飘散（同时向上）
            STYLE_RISE_LEFT=8,                       // 向上飘散（从左往右逐列向上）
            STYLE_RISE_RIGHT=9,                      // 向上飘散（从右往左逐列向上）
            STYLE_RISE_TOP=10;                       // 向上飘散（从上到下逐行向上）

    // 粒子形状
    public static final int SHAPE_CIRCLE = 0;        // 圆形（默认）
    public static final int SHAPE_SQUARE = 1;        // 方形

    private int mStyle=STYLE_EXPLOSION;             // 动画样式
    private int mShape=SHAPE_CIRCLE;                // 粒子形状

    private ValueAnimator mValueAnimator;

    private ParticleSmasher mContainer;                  // 绘制动画效果的View
    private View mAnimatorView;                        // 要进行爆炸动画的View
    
    private Bitmap mBitmap;
    private Rect mRect;                                // 要进行动画的View在坐标系中的矩形
    
    private Paint mPaint;                              // 绘制粒子的画笔
    private Particle[][] mParticles;                   // 粒子数组
    
    private float mEndValue = 1.5f;

    private long mDuration = 1000L;
    private long mStartDelay = 150L;
    private float mHorizontalMultiple = 3;             // 粒子水平变化幅度
    private float mVerticalMultiple = 4;               // 粒子垂直变化幅度
    private int mRadius=Utils.dp2Px(2);                // 粒子基础半径
    private boolean mEnableHideAnimation = true;       // 是否启用抖动+缩放动画

    // 加速度插值器
    private static final Interpolator DEFAULT_INTERPOLATOR = new AccelerateInterpolator(0.6f);
    private OnAnimatorListener mOnAnimatorLIstener;

    public SmashAnimator(ParticleSmasher view, View animatorView) {
        this.mContainer = view;
        init(animatorView);
    }

    private void init(View animatorView) {
        this.mAnimatorView = animatorView;
        // 注意：不在 init 中创建 bitmap，因为 start() 时会重新创建
        // 确保 View 的状态正确即可
        mRect = mContainer.getViewRect(animatorView);
        initValueAnimator();
        initPaint();
    }

    /**
     * 获取正在执行动画的View
     * @return 目标View
     */
    public View getAnimatorView() {
        return mAnimatorView;
    }

    private void initValueAnimator() {
        mValueAnimator = new ValueAnimator();
        mValueAnimator.setFloatValues(0F, mEndValue);
        mValueAnimator.setInterpolator(DEFAULT_INTERPOLATOR);
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }


    /**
     *   爆炸动画回调事件
     */
    public static abstract class OnAnimatorListener {

        /**
         * 动画开始时回调
         */
        public void onAnimatorStart() {
        }

        /**
         * 动画结束后回调
         */
        public void onAnimatorEnd() {
        }

    }

    /**
     *   设置动画样式
     *   @param style {@link #STYLE_EXPLOSION},{@link #STYLE_DROP},{@link #STYLE_FLOAT_TOP},{@link #STYLE_FLOAT_BOTTOM},{@link #STYLE_FLOAT_LEFT},{@link #STYLE_FLOAT_RIGHT};
     *
     *   @return      链式调用，因此返回本身
     */
    public SmashAnimator setStyle(int style){
        this.mStyle=style;
        return this;
    }

    /**
     *   设置爆炸动画时间
     *   @param duration    时间，单位为毫秒
     *   @return      链式调用，因此返回本身
     */
    public SmashAnimator setDuration(long duration) {
        this.mDuration = duration;
        return this;
    }

    /**
     *   设置爆炸动画前延时
     *   @param startDelay    动画开始前的延时，单位为毫秒
     *   @return      链式调用，因此返回本身
     *
     */
    public SmashAnimator setStartDelay(long startDelay) {
        mStartDelay = startDelay;
        return this;
    }

    /**
     *   设置水平变化参数
     *   @param horizontalMultiple          水平变化幅度，默认为3。为0则不产生变化。
     *   @return      链式调用，因此返回本身
     */
    public SmashAnimator setHorizontalMultiple(float horizontalMultiple) {
        this.mHorizontalMultiple = horizontalMultiple;
        return this;
    }

    /**
     *   设置垂直变化参数
     *   @param verticalMultiple  垂直变化参数，默认为4，为0则不产生变化
     *   @return      链式调用，因此返回本身
     *
     */
    public SmashAnimator setVerticalMultiple(float verticalMultiple) {
        this.mVerticalMultiple = verticalMultiple;
        return this;
    }

    /**
     *   设置粒子基础半径
     *   @param radius  半径，单位为px
     *   @return      链式调用，因此返回本身
     */
    public SmashAnimator setParticleRadius(int radius){
        this.mRadius=radius;
        return this;
    }

    /**
     *   设置粒子形状
     *   @param shape  形状，{@link #SHAPE_CIRCLE} 圆形, {@link #SHAPE_SQUARE} 方形
     *   @return      链式调用，因此返回本身
     */
    public SmashAnimator setShape(int shape){
        this.mShape = shape;
        return this;
    }

    /**
     *   设置是否启用抖动+缩放隐藏动画
     *   @param enable  true=启用（默认），false=禁用（View直接透明消失）
     *   @return      链式调用，因此返回本身
     */
    public SmashAnimator setHideAnimation(boolean enable){
        this.mEnableHideAnimation = enable;
        return this;
    }

    /**
     *   添加回调
     *   @param listener   回调事件，包含开始回调、结束回调。
     *   @return      链式调用，因此返回本身
     */
    public SmashAnimator addAnimatorListener(final OnAnimatorListener listener) {
        this.mOnAnimatorLIstener = listener;
        return this;
    }

    /**
     *   开始动画
     */
    public void start() {
        // 防止动画正在运行时重复启动
        if (mValueAnimator.isRunning()) {
            return;
        }
        
        // 确保 View 处于正常可见状态再创建 bitmap
        // 如果 View 已经被隐藏（scale=0），需要先恢复
        if (mAnimatorView.getScaleX() == 0 || mAnimatorView.getAlpha() == 0) {
            mAnimatorView.animate().cancel();
            mAnimatorView.setScaleX(1f);
            mAnimatorView.setScaleY(1f);
            mAnimatorView.setAlpha(1f);
            mAnimatorView.setTranslationX(0f);
            mAnimatorView.setTranslationY(0f);
        }
        
        // 每次start时重新获取View的bitmap和位置，确保数据准确
        mBitmap = mContainer.createBitmapFromView(mAnimatorView);
        mRect = mContainer.getViewRect(mAnimatorView);
        setValueAnimator();
        calculateParticles(mBitmap);
        hideView(mAnimatorView, mStartDelay);
        mValueAnimator.start();
        mContainer.invalidate();
    }

    /**
     *   设置动画参数
     */
    private void setValueAnimator() {
        mValueAnimator.setDuration(mDuration);
        mValueAnimator.setStartDelay(mStartDelay);
        // 先清除旧的 listener，防止重复添加
        mValueAnimator.removeAllListeners();
        mValueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mOnAnimatorLIstener != null) {
                    mOnAnimatorLIstener.onAnimatorEnd();
                }
                mContainer.removeAnimator(SmashAnimator.this);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                if (mOnAnimatorLIstener != null) {
                    mOnAnimatorLIstener.onAnimatorStart();
                }

            }
        });
    }

    /**
     * 根据图片计算粒子
     * @param bitmap      需要计算的图片
     */
    private void calculateParticles(Bitmap bitmap) {

        int col = bitmap.getWidth() /(mRadius*2);
        int row = bitmap.getHeight() / (mRadius*2);

        Random random = new Random(System.currentTimeMillis());
        mParticles = new Particle[row][col];

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                int x=j * mRadius*2 + mRadius;
                int y=i * mRadius*2 + mRadius;
                int color = bitmap.getPixel(x, y);
                Point point=new Point(mRect.left+x,mRect.top+y);

                switch (mStyle){
                    case STYLE_EXPLOSION:
                        mParticles[i][j] = new ExplosionParticle(color, mRadius, mRect, mEndValue, random, mHorizontalMultiple, mVerticalMultiple);
                        break;
                    case STYLE_DROP:
                        mParticles[i][j] = new DropParticle(point,color, mRadius, mRect, mEndValue, random, mHorizontalMultiple, mVerticalMultiple);
                        break;
                    case STYLE_FLOAT_LEFT:
                        mParticles[i][j] = new FloatParticle(FloatParticle.ORIENTATION_LEFT,point,color, mRadius, mRect, mEndValue, random, mHorizontalMultiple, mVerticalMultiple);
                        break;
                    case STYLE_FLOAT_RIGHT:
                        mParticles[i][j] = new FloatParticle(FloatParticle.ORIENTATION_RIGHT,point,color, mRadius, mRect, mEndValue, random, mHorizontalMultiple, mVerticalMultiple);
                        break;
                    case STYLE_FLOAT_TOP:
                        mParticles[i][j] = new FloatParticle(FloatParticle.ORIENTATION_TOP,point,color, mRadius, mRect, mEndValue, random, mHorizontalMultiple, mVerticalMultiple);
                        break;
                    case STYLE_FLOAT_BOTTOM:
                        mParticles[i][j] = new FloatParticle(FloatParticle.ORIENTATION_BOTTOM,point,color, mRadius, mRect, mEndValue, random, mHorizontalMultiple, mVerticalMultiple);
                        break;
                    case STYLE_RISE:
                        mParticles[i][j] = new RiseParticle(RiseParticle.DIRECTION_ALL, point, color, mRadius, mRect, mEndValue, random, mHorizontalMultiple, mVerticalMultiple);
                        break;
                    case STYLE_RISE_LEFT:
                        mParticles[i][j] = new RiseParticle(RiseParticle.DIRECTION_LEFT, point, color, mRadius, mRect, mEndValue, random, mHorizontalMultiple, mVerticalMultiple);
                        break;
                    case STYLE_RISE_RIGHT:
                        mParticles[i][j] = new RiseParticle(RiseParticle.DIRECTION_RIGHT, point, color, mRadius, mRect, mEndValue, random, mHorizontalMultiple, mVerticalMultiple);
                        break;
                    case STYLE_RISE_TOP:
                        mParticles[i][j] = new RiseParticle(RiseParticle.DIRECTION_TOP, point, color, mRadius, mRect, mEndValue, random, mHorizontalMultiple, mVerticalMultiple);
                        break;
                }

            }
        }
        mBitmap.recycle();
        mBitmap = null;
    }


    /**
     *  View执行颤抖动画，之后再执行和透明动画，达到隐藏View的效果
     *  @param view 执行效果的View
     *  @param startDelay 爆炸动画的开始前延时时间
     */
    public void hideView(final View view, long startDelay) {
        // 先取消View上的所有pending动画，确保状态一致
        view.animate().cancel();
        // 确保View当前状态是正常的
        view.setScaleX(1f);
        view.setScaleY(1f);
        view.setAlpha(1f);
        view.setTranslationX(0f);
        view.setTranslationY(0f);
        
        if (mEnableHideAnimation) {
            // 启用抖动+缩放动画
            ValueAnimator valueAnimator = new ValueAnimator();
            valueAnimator.setDuration(startDelay + 50).setFloatValues(0f, 1f);
            // 使View颤抖
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                Random random = new Random();

                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    view.setTranslationX((random.nextFloat() - 0.5F) * view.getWidth() * 0.05F);
                    view.setTranslationY((random.nextFloat() - 0.5f) * view.getHeight() * 0.05f);
                }
            });
            // 抖动动画结束后重置translation，防止位置漂移
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setTranslationX(0);
                    view.setTranslationY(0);
                }
            });
            valueAnimator.start();
            // 将View 缩放至0、透明至0
            view.animate().setDuration(260).setStartDelay(startDelay).scaleX(0).scaleY(0).alpha(0).start();
        } else {
            // 禁用抖动+缩放动画，直接隐藏
            view.setAlpha(0f);
        }
    }


    /**
     *   开始逐个绘制粒子
     *   @param canvas  绘制的画板
     *   @return 是否成功
     */
    public boolean draw(Canvas canvas) {
        if (!mValueAnimator.isStarted()) {
            return false;
        }
        for (Particle[] particle : mParticles) {
            for (Particle p : particle) {
                // 根据动画进程，修改粒子的参数
                p.advance((float) (mValueAnimator.getAnimatedValue()), mEndValue);
                if (p.alpha > 0) {
                    mPaint.setColor(p.color);
                    mPaint.setAlpha((int) (Color.alpha(p.color) * p.alpha));
                    if (mShape == SHAPE_SQUARE) {
                        // 方形：以 (cx, cy) 为中心，radius 为半边长
                        canvas.drawRect(
                            p.cx - p.radius, 
                            p.cy - p.radius, 
                            p.cx + p.radius, 
                            p.cy + p.radius, 
                            mPaint
                        );
                    } else {
                        // 圆形（默认）
                        canvas.drawCircle(p.cx, p.cy, p.radius, mPaint);
                    }
                }
            }
        }
        mContainer.invalidate();
        return true;
    }


}
