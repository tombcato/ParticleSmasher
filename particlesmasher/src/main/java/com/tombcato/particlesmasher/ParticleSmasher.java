package com.tombcato.particlesmasher;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import java.util.List;
import java.util.WeakHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * <pre>
 *     author : FaDai
 *     e-mail : i_fadai@163.com
 *     time   : 2017/12/14
 *     desc   : 粒子粉碎动画容器
 *     version: 2.0
 * </pre>
 */

public class ParticleSmasher extends View {

    // ==================== 单例缓存 ====================
    
    /** Activity 级别缓存（同 Activity 的所有 Fragment 共用） */
    private static final WeakHashMap<Activity, ParticleSmasher> sActivityCache = new WeakHashMap<>();
    
    /** ViewGroup 级别缓存（每个容器独立） */
    private static final WeakHashMap<ViewGroup, ParticleSmasher> sViewGroupCache = new WeakHashMap<>();

    // ==================== 实例成员 ====================
    
    private List<SmashAnimator> mAnimators = new CopyOnWriteArrayList<>();
    private Canvas mCanvas;
    private LifecycleEventObserver mLifecycleObserver;

    // ==================== 静态工厂方法 ====================
    
    /**
     * 获取或创建 Activity 级别的 ParticleSmasher
     * 同一 Activity 内的所有 Fragment 共用同一个实例
     * 
     * @param activity 目标 Activity
     * @return ParticleSmasher 实例
     */
    public static synchronized ParticleSmasher get(Activity activity) {
        ParticleSmasher instance = sActivityCache.get(activity);
        if (instance == null || !instance.isAttachedToWindow()) {
            instance = new ParticleSmasher(activity);
            sActivityCache.put(activity, instance);
        }
        return instance;
    }
    
    /**
     * 获取或创建 Fragment 级别的 ParticleSmasher
     * 粒子动画限制在 Fragment 根视图内
     * 
     * @param fragment 目标 Fragment
     * @return ParticleSmasher 实例
     */
    public static synchronized ParticleSmasher get(Fragment fragment) {
        View rootView = fragment.getView();
        if (rootView instanceof ViewGroup) {
            return get((ViewGroup) rootView);
        }
        // Fragment 根视图不是 ViewGroup，回退到 Activity 级别
        return get(fragment.requireActivity());
    }
    
    /**
     * 获取或创建 ViewGroup 级别的 ParticleSmasher
     * 粒子动画限制在指定容器内
     * 
     * @param container 目标容器
     * @return ParticleSmasher 实例
     */
    public static synchronized ParticleSmasher get(ViewGroup container) {
        ParticleSmasher instance = sViewGroupCache.get(container);
        if (instance == null || !instance.isAttachedToWindow()) {
            instance = new ParticleSmasher(container);
            sViewGroupCache.put(container, instance);
        }
        return instance;
    }

    // ==================== 构造函数 ====================
    
    /**
     * Activity 级别构造函数
     * @param activity 目标 Activity
     */
    public ParticleSmasher(Activity activity) {
        super((Context) activity);
        addViewToWindow(activity);
        init();
        observeLifecycle(activity);
    }
    
    /**
     * ViewGroup 级别构造函数
     * @param container 目标容器
     */
    public ParticleSmasher(ViewGroup container) {
        super(container.getContext());
        addViewToContainer(container);
        init();
        observeLifecycle(container.getContext());
    }

    // ==================== 初始化方法 ====================
    
    private void addViewToWindow(Activity activity) {
        ViewGroup rootView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 
            ViewGroup.LayoutParams.MATCH_PARENT
        );
        rootView.addView(this, lp);
    }
    
    private void addViewToContainer(ViewGroup container) {
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 
            ViewGroup.LayoutParams.MATCH_PARENT
        );
        container.addView(this, lp);
    }

    private void init() {
        mCanvas = new Canvas();
        setLayerType(LAYER_TYPE_HARDWARE, null);
    }
    
    /**
     * 监听生命周期，自动清理资源
     */
    private void observeLifecycle(Context context) {
        if (context instanceof LifecycleOwner) {
            LifecycleOwner owner = (LifecycleOwner) context;
            mLifecycleObserver = (source, event) -> {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    detach();
                }
            };
            owner.getLifecycle().addObserver(mLifecycleObserver);
        }
    }

    // ==================== 生命周期管理 ====================
    
    /**
     * 从父容器移除并清理资源
     */
    public synchronized void detach() {
        clear();
        ViewGroup parent = (ViewGroup) getParent();
        if (parent != null) {
            parent.removeView(this);
        }
        // 从缓存移除
        sActivityCache.values().remove(this);
        sViewGroupCache.values().remove(this);
        // 移除生命周期观察者
        if (mLifecycleObserver != null && getContext() instanceof LifecycleOwner) {
            ((LifecycleOwner) getContext()).getLifecycle().removeObserver(mLifecycleObserver);
            mLifecycleObserver = null;
        }
    }
    
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        clear();
    }

    // ==================== 绘制逻辑 ====================
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        Rect combinedDirtyRect = new Rect();
        boolean hasActiveAnimator = false;
        
        for (SmashAnimator animator : mAnimators) {
            animator.draw(canvas);
            Rect dirtyRect = animator.getDirtyRect();
            if (!dirtyRect.isEmpty()) {
                combinedDirtyRect.union(dirtyRect);
                hasActiveAnimator = true;
            }
        }
        
        if (hasActiveAnimator) {
            int viewWidth = getWidth();
            int viewHeight = getHeight();
            int dirtyArea = combinedDirtyRect.width() * combinedDirtyRect.height();
            int viewArea = viewWidth * viewHeight;
            
            if (viewArea > 0 && dirtyArea > viewArea * 0.8f) {
                invalidate();
            } else {
                invalidate(combinedDirtyRect);
            }
        }
    }

    // ==================== 动画 API ====================
    
    public SmashAnimator with(View view) {
        stopAnimation(view);
        SmashAnimator animator = new SmashAnimator(this, view);
        mAnimators.add(animator);
        return animator;
    }

    public void stopAnimation(View view) {
        for (SmashAnimator animator : mAnimators) {
            if (animator.getAnimatorView() == view) {
                animator.stop();
                mAnimators.remove(animator);
            }
        }
    }

    public boolean isAnimating(View view) {
        for (SmashAnimator animator : mAnimators) {
            if (animator.getAnimatorView() == view) {
                return true;
            }
        }
        return false;
    }
    
    public void removeAnimator(SmashAnimator animator) {
        mAnimators.remove(animator);
    }

    public void clear() {
        for (SmashAnimator animator : mAnimators) {
            animator.stop();
        }
        mAnimators.clear();
        invalidate();
    }

    // ==================== 工具方法 ====================
    
    public Rect getViewRect(View view) {
        Rect rect = new Rect();
        view.getGlobalVisibleRect(rect);

        int[] location = new int[2];
        getLocationOnScreen(location);

        rect.offset(-location[0], -location[1]);
        return rect;
    }

    public Bitmap createBitmapFromView(View view) {
        return createBitmapFromView(view, new Rect(0, 0, view.getWidth(), view.getHeight()));
    }

    public Bitmap createBitmapFromView(View view, Rect cropRect) {
        view.clearFocus();
        if (cropRect == null || cropRect.isEmpty() || view.getWidth() <= 0 || view.getHeight() <= 0) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(cropRect.width(), cropRect.height(), Bitmap.Config.ARGB_8888);
        if (bitmap != null) {
            synchronized (mCanvas) {
                Canvas canvas = mCanvas;
                canvas.setBitmap(bitmap);
                canvas.translate(-cropRect.left, -cropRect.top);
                view.draw(canvas);
                canvas.setBitmap(null);
            }
        }
        return bitmap;
    }

    public void reShowView(View view) {
        view.animate().cancel();
        view.setScaleX(1f);
        view.setScaleY(1f);
        view.setAlpha(1f);
        view.setTranslationX(0f);
        view.setTranslationY(0f);
        view.setVisibility(View.VISIBLE);
    }
}
