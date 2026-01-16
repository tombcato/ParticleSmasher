# ParticleSmasher 优化建议

本文档记录了代码审查后发现的优化空间，按优先级和分类整理。

---

## 1. 性能优化

### 1.1 粒子数组结构优化
**现状**: 使用 `Particle[][]` 二维数组，双重循环遍历效率低。

**建议**: 改为一维 `Particle[]` 数组，减少循环开销和数组访问开销。

```diff
- private Particle[][] mParticles;
+ private Particle[] mParticles;
```

### 1.2 脏区计算优化
**现状**: 每帧 `draw()` 都重新遍历计算 `minX/maxX/minY/maxY`。

**建议**: 在粒子 `advance()` 时顺便更新全局脏区边界，`draw()` 直接使用缓存值。

### 1.3 Bitmap 复用
**现状**: 每次 `start()` 都调用 `Bitmap.createBitmap()` 创建新位图。

**建议**: 对相同尺寸的 View 使用对象池复用 Bitmap，减少 GC 压力。

### 1.4 Paint 共享
**现状**: 每个 `SmashAnimator` 独立持有 `Paint` 对象。

**建议**: 使用共享的 static Paint（需注意线程安全），或在 `ParticleSmasher` 中统一管理。

### 1.5 硬件加速
**现状**: 未使用 `LAYER_TYPE_HARDWARE`。

**建议**: 对 `ParticleSmasher` View 设置硬件加速层提升绘制性能：
```java
setLayerType(View.LAYER_TYPE_HARDWARE, null);
```

---

## 2. 代码重构

### 2.1 粒子类重复代码
**现状**: 4 个粒子子类中 `getBaseRadius()`, `getHorizontalElement()`, `getVerticalElement()` 实现几乎相同。

**建议**: 抽取到 `Particle` 基类为 protected static 方法。

### 2.2 魔数常量化
**现状**: 代码中存在魔数如 `scaleMode == 2`, `normalization >= 0.7f`。

**建议**: 
- 使用 `SmashAnimator.SCALE_UP` 替代数字
- 提取淡出阈值为常量 `FADE_START_THRESHOLD = 0.7f`

### 2.3 变量命名优化
**现状**: `font` / `later` 命名语义不清。

**建议**: 
- `font` → `startOffset` (粒子延迟启动的时间偏移)
- `later` → `endFadeOffset` (粒子提前消失的时间偏移)

### 2.4 使用枚举类型
**现状**: 风格和缩放模式使用 `int` 常量。

**建议**: 改为 `enum` 类型增强类型安全：
```java
public enum Style {
    EXPLOSION, DROP, FLOAT_LEFT, FLOAT_RIGHT, 
    FLOAT_TOP, FLOAT_BOTTOM, RISE, RISE_LEFT, RISE_RIGHT, RISE_TOP
}

public enum ScaleMode {
    SCALE_DOWN, SCALE_SAME, SCALE_UP
}
```

---

## 3. API 易用性

### 3.1 支持更多使用场景
**现状**: 构造函数仅接受 `Activity`。

**建议**: 增加 `ViewGroup` 参数重载，支持 Fragment 等场景：
```java
public ParticleSmasher(ViewGroup container) { ... }
```

### 3.2 添加 build() 方法
**现状**: Builder 模式直接调用 `start()` 执行。

**建议**: 提供 `build()` 返回配置好的 `SmashAnimator`，方便延迟执行。

### 3.3 暂停/恢复功能
**现状**: 只有 `stop()` 方法。

**建议**: 添加 `pause()` / `resume()` API 支持动画暂停恢复。

### 3.4 进度回调
**现状**: 只有 `onAnimatorEnd()` 回调。

**建议**: 增加 `onProgress(float progress)` 回调可观察动画进度。

### 3.5 单例管理
**现状**: 同一 Activity 创建多个 Smasher 会叠加多个覆盖层。

**建议**: 提供单例模式或检测已有 ParticleSmasher View。

---

## 4. 稳定性

### 4.1 生命周期管理
**现状**: Activity finish 时如果动画未结束可能内存泄漏。

**建议**: 
- 监听 `onDetachedFromWindow()` 自动 `clear()`
- 或使用 `WeakReference<Activity>`

### 4.2 View 尺寸边界检查
**现状**: 已有保护但仍可能边界情况遗漏。

**建议**: 在 `createBitmapFromView()` 入口处增加早返回：
```java
if (view.getWidth() <= 0 || view.getHeight() <= 0) {
    return null;
}
```

### 4.3 ProGuard 规则
**现状**: `proguard-rules.pro` 为空。

**建议**: 添加 keep 规则：
```proguard
-keep class com.tombcato.particlesmasher.** { *; }
-keep class * extends com.tombcato.particlesmasher.particle.Particle { *; }
```

### 4.4 参数校验
**现状**: `scaleMode` 等参数传入非法值时默默使用默认值。

**建议**: 添加校验，非法值时抛出 `IllegalArgumentException` 或记录警告日志。

---

## 5. Demo App 优化

### 5.1 消除重复代码
**现状**: 两个 ImageView 执行相同配置的动画，代码复制粘贴。

**建议**: 抽取 `animateView(View target)` 方法。

### 5.2 Interpolator 数组
**现状**: `INTERPOLATOR_VALUES` 静态初始化可能在某些设备上有问题。

**建议**: 改为方法 `getInterpolator(int index)` 按需创建。

### 5.3 数据绑定
**现状**: SeekBar 与 Label 更新分散。

**建议**: 使用 DataBinding 或 ViewModel 统一管理 UI 状态。

---

## 6. 发布与文档

### 6.1 consumer-rules.pro
**现状**: 未提供。

**建议**: 添加 `consumer-rules.pro` 供使用者混淆配置：
```proguard
-keep class com.tombcato.particlesmasher.ParticleSmasher { *; }
-keep class com.tombcato.particlesmasher.SmashAnimator { *; }
-keep class com.tombcato.particlesmasher.SmashAnimator$* { *; }
```

### 6.2 版本动态化
**现状**: `version = '2.0.0'` 硬编码在 build.gradle 中。

**建议**: 改为读取 git tag 或 gradle.properties：
```groovy
version = project.findProperty('VERSION') ?: 'LOCAL'
```

### 6.3 CHANGELOG
**现状**: 无版本变更记录。

**建议**: 添加 `CHANGELOG.md` 记录每个版本的改动。

### 6.4 Javadoc 发布
**现状**: 配置了但未在 CI 中执行。

**建议**: GitHub Actions 中加入 Javadoc 生成并发布到 GitHub Pages。

---

## 7. 进阶功能 (可选)

| 功能 | 描述 |
|------|------|
| **颜色采样策略** | 支持区域平均色、调色板提取，而非单点采样 |
| **GPU 粒子渲染** | 使用 RenderScript 或 OpenGL ES 实现大规模粒子 (10w+) |
| **Jetpack Compose** | 提供 `@Composable` Modifier 版本 |
| **粒子形状扩展** | 支持三角形、星形、自定义 Path |
| **物理引擎** | 集成简单物理模拟（重力、碰撞、风力） |

---

## 优先级建议

| 优先级 | 项目 | 工作量 | 状态 |
|--------|------|--------|------|
| 🔴 高 | 粒子数组一维化 | 中 | ✅ 已完成 |
| 🔴 高 | 硬件加速层 | 低 | ✅ 已完成 |
| 🔴 高 | 缓存 animatedValue | 低 | ✅ 已完成 |
| 🔴 高 | Random 复用 (ThreadLocalRandom) | 低 | ✅ 已完成 |
| 🟠 中 | 粒子类重复代码抽取 | 低 | 待优化 |
| 🟠 中 | 生命周期管理 | 低 | 待优化 |
| 🟠 中 | ProGuard/consumer-rules | 低 | 待优化 |
| 🟠 中 | **对象池复用粒子** | 中 | 🔜 待优化 |
| 🟡 低 | **Paint 共享** | 低 | 🔜 待优化 |
| 🟡 低 | 枚举类型重构 | 中 | 待优化 |
| 🟡 低 | API 增强 (pause/resume) | 中 | 待优化 |
| 🔵 可选 | GPU 渲染 | 高 | 待评估 |
| 🔵 可选 | Compose 支持 | 高 | 待评估 |

---

## 修改记录

| 日期 | 版本 | 内容 |
|------|------|------|
| 2026-01-16 | v1.0 | 初始版本，完成代码审查与优化建议 |
