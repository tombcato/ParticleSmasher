# Changelog

All notable changes to this project will be documented in this file.

## [2.1.0] - 2026-01-16

### 🚀 性能优化 (Performance)
- **一维粒子数组**：从 `Particle[][]` 改为 `Particle[]`，提升缓存命中率，减少循环开销
- **硬件加速**：启用 `LAYER_TYPE_HARDWARE` 进行 GPU 渲染
- **智能脏区合并**：多动画脏区合并 + 超过 80% 覆盖率时自动全屏刷新
- **缓存优化**：每帧缓存 `getAnimatedValue()`，复用 `ThreadLocalRandom`
- **静态工厂模式**：`ParticleSmasher.get(Activity/Fragment/ViewGroup)` 配合 WeakHashMap 缓存
- **生命周期感知**：Activity/Fragment 销毁时自动清理资源
- **混淆规则**：包含 `consumer-rules.pro` 供使用者混淆配置
- **脏区刷新**：只重绘粒子包围盒区域，大幅减少 GPU 重绘压力
- **Alpha 预计算**：初始化时缓存，消除绘制循环中的重复调用
- **内存效率**：`CopyOnWriteArrayList` 保证线程安全

### 🛠 核心功能 & 修复 (Core & Fixes)
- **Gradle & AGP 升级** (Gradle 8.13, AGP 8.13.2)
- **AndroidX 迁移** (Support → AndroidX)
- **Java 17 支持**
- **关键 Bug 修复**
    - 修复动画位置漂移问题
    - 修复隐藏后点击无效问题 (reShowView)
    - **修复 View 未布局完成时崩溃问题** (width/height <= 0)
    - **修复局部可见 View 的动画裁切问题** (只渲染可见区域)
- **并发优化** (CopyOnWriteArrayList)

### ✨ 动画效果 (Animation Effects)
- **新动画效果** (向上飘散 Rise 系列)
- **粒子形状** (圆形/方形)
- **插值器选择** (支持自定义插值器)
- **抖动动画开关**
- **随机延迟范围配置**
    - `setStartRandomness()`: 控制粒子起跑的整齐度
    - `setEndRandomness()`: 控制粒子消失的随机性
- **粒子缩放模式** (Scale Mode)
    - `SCALE_DOWN`: 逐渐变小 (默认)
    - `SCALE_SAME`: 大小不变
    - `SCALE_UP`: 逐渐变大
- **粒子间距/密度控制** (Particle Gap)
    - `setParticleGap(int px)`: 允许设置正间距(稀疏)和负间距(重叠/高密度)

### 📱 Demo & UI
- **全功能控制面板** (配置所有参数)
- **配置保存与读取** (SharedPreferences)
- **沉浸式状态栏** (白色标题栏 + 黑色图标)
- **布局优化** (固定头尾，中间滚动)
- **多 View 并发动画演示** (Demo 支持同时对多个 View 执行粒子破碎)

---

## [1.0.0] - 2017-12-14

### Added
- Initial release
- Explosion style animation
- Drop style animation
- Float style animations (left/right/top/bottom)
- Rise style animations (all/left/right/top)
