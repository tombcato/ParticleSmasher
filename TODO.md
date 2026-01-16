# ParticleSmasher 优化计划清单

## 已实现功能

### 核心功能 & 修复
- [x] **Gradle & AGP 升级** (Gradle 8.13, AGP 8.13.2)
- [x] **AndroidX 迁移** (Support → AndroidX)
- [x] **Java 17 支持**
- [x] **关键 Bug 修复**
    - [x] 修复动画位置漂移问题
    - [x] 修复隐藏后点击无效问题 (reShowView)
    - [x] **修复 View 未布局完成时崩溃问题** (width/height <= 0)
    - [x] **修复局部可见 View 的动画裁切问题** (只渲染可见区域)
- [x] **并发优化** (CopyOnWriteArrayList)

### 动画效果
- [x] **新动画效果** (向上飘散 Rise 系列)
- [x] **粒子形状** (圆形/方形)
- [x] **插值器选择** (支持自定义插值器)
- [x] **抖动动画开关**
- [x] **随机延迟范围配置**
    - `setStartRandomness()`: 控制粒子起跑的整齐度
    - `setEndRandomness()`: 控制粒子消失的随机性
- [x] **粒子缩放模式** (Scale Mode)
    - `SCALE_DOWN`: 逐渐变小 (默认)
    - `SCALE_SAME`: 大小不变
    - `SCALE_UP`: 逐渐变大
- [x] **粒子间距/密度控制** (Particle Gap)
    - `setParticleGap(int px)`: 允许设置正间距(稀疏)和负间距(重叠/高密度)。

### Demo & UI
- [x] **全功能控制面板** (配置所有参数)
- [x] **配置保存与读取** (SharedPreferences)
- [x] **沉浸式状态栏** (白色标题栏 + 黑色图标)
- [x] **布局优化** (固定头尾，中间滚动)
- [x] **多 View 并发动画演示** (Demo 支持同时对多个 View 执行粒子破碎)

## 待优化功能 (Backlog)

### 1. 粒子属性控制
- [ ] **透明度配置**
    - 支持设置起始透明度和结束透明度。
    - *影响类*: `SmashAnimator`, `Particle`

### 2. 动画行为
- [ ] **EndValue 可配置**
    - 暴露 `mEndValue` 参数，控制动画结束时的扩散范围（目前固定 1.5）。
    - *影响类*: `SmashAnimator`
- [ ] **物理效果 (高级)**
    - 添加重力加速度、风力等物理模拟。
    - *影响类*: `Particle.advance()`

### 3. 性能优化
- [x] **初级优化 (CPU/Draw)**
    - [x] **Color.alpha 预计算**: 避免在 onDraw 循环中解析颜色。
    - [x] **脏区刷新 (Dirty Invalidation)**: 仅重绘粒子活动区域。
- [ ] **对象池 (Object Pool)**
    - 复用 `Particle` 对象，减少内存抖动。
    - *注意*: 适合频繁触发动画的场景。
- [ ] **硬件加速绘图 (RenderNode)**
    - 探索使用 `HardwareLayer` 或 `RenderNode` (Android Q+) 优化绘制。

### 4. 工程化
- [ ] **单元测试 (Unit Test)**
    - 为 `SmashAnimator` 逻辑添加单元测试。
- [x] **GitHub Actions**
    - 配置 CI/CD 自动构建和发布。

### 暂缓功能 (Won't Do / Later)
- [ ] **自定义颜色**: 用户暂不需要 (使用单一颜色或渐变色覆盖)。
- [ ] **透明度配置**: 暂不开发。
- [ ] **EndValue 可配置**: 暂不开发。
- [ ] **物理效果 (高级)**: 暂不开发。
- [ ] **对象池 (Object Pool)**: 暂不需要。
- [ ] **硬件加速绘图**: 暂不需要。
- [ ] **单元测试**: 暂不需要。
