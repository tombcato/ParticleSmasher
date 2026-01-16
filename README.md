> Android 粒子破碎效果，可以使用于任何View。

## 特色：
- **10种动画效果**：爆炸、下落、四个方向飘落、四个方向向上飘散
- 链式调用，自定义动画时间、样式、动画幅度等
- 支持圆形/方形粒子
- 支持开关抖动缩放动画

## 效果图：

![六种效果演示](https://github.com/ifadai/ParticleSmasher/blob/master/screenshot/screen1.gif)

## 用法：
### 导入

**Step 1.** 在根目录 `settings.gradle` 添加 JitPack 仓库：
```groovy
dependencyResolutionManagement {
    repositories {
        // ...
        maven { url 'https://jitpack.io' }
    }
}
```

**Step 2.** 在 `app/build.gradle` 添加依赖：
```groovy
dependencies {
    implementation 'com.github.tombcato:ParticleSmasher:v2.0.0'
}
```
### 简单使用：

```java
ParticleSmasher smasher = new ParticleSmasher(this);
// 默认为爆炸动画
smasher.with(view).start();
```

### 完整配置：

```java
smasher.with(view)
    .setStyle(SmashAnimator.STYLE_RISE)       // 动画样式
    .setShape(SmashAnimator.SHAPE_CIRCLE)     // 粒子形状：圆形/方形
    .setDuration(1000)                         // 动画时长 (ms)
    .setStartDelay(150)                        // 开始延迟 (ms)
    .setHorizontalMultiple(3f)                 // 水平运动幅度
    .setVerticalMultiple(4f)                   // 垂直运动幅度
    .setParticleRadius(Utils.dp2Px(2))         // 粒子半径
    .setHideAnimation(true)                    // 抖动+缩放隐藏动画
    .addAnimatorListener(new SmashAnimator.OnAnimatorListener() {
        @Override
        public void onAnimatorStart() {
            // 动画开始回调
        }

        @Override
        public void onAnimatorEnd() {
            // 动画结束回调
        }
    })
    .start();
```

### 动画样式：

| 样式常量 | 效果 |
|---------|------|
| `STYLE_EXPLOSION` | 四散爆炸（默认） |
| `STYLE_DROP` | 向下坠落 |
| `STYLE_FLOAT_LEFT` | 从左往右逐列飘落 |
| `STYLE_FLOAT_RIGHT` | 从右往左逐列飘落 |
| `STYLE_FLOAT_TOP` | 从上往下逐行飘落 |
| `STYLE_FLOAT_BOTTOM` | 从下往上逐行飘落 |
| `STYLE_RISE` | 向上飘散（同时） |
| `STYLE_RISE_LEFT` | 从左往右逐列向上飘散 |
| `STYLE_RISE_RIGHT` | 从右往左逐列向上飘散 |
| `STYLE_RISE_TOP` | 从上往下逐行向上飘散 |

### 让View重新显示：

```java
smasher.reShowView(view);
```

### 检查动画状态：

```java
if (smasher.isAnimating(view)) {
    // View 正在动画中
}
```

---

## 更新日志

### v2.0.0 (2026-01-16) 现代化升级

**新功能**
- 新增 4 种向上飘散效果：`STYLE_RISE`, `STYLE_RISE_LEFT`, `STYLE_RISE_RIGHT`, `STYLE_RISE_TOP`
- 新增粒子形状配置：`setShape()` 支持圆形/方形
- 新增抖动缩放动画开关：`setHideAnimation()`
- 新增 `isAnimating(View)` 方法检查动画状态
- 示例 App 改为控制面板 UI，可配置所有参数

**Gradle & AGP 升级**
- Gradle 4.1 → **8.13**
- Android Gradle Plugin 3.0.0 → **8.13.2**
- compileSdk / targetSdk 26 → **34**
- minSdk 14 → **21**
- Java 7 → **Java 17**

**AndroidX 迁移**
- `android.support.*` → `androidx.*`
- 更新测试依赖到 AndroidX Test

**Bug 修复**
- 修复动画后再次点击位置漂移问题
- 修复 View 隐藏状态下再次点击无动画问题
- 使用 `CopyOnWriteArrayList` 避免并发修改异常
