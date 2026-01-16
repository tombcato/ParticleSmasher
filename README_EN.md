# ParticleSmasher

[![](https://jitpack.io/v/tombcato/ParticleSmasher.svg)](https://jitpack.io/#tombcato/ParticleSmasher)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![Download Demo](https://img.shields.io/badge/Download-Demo%20App-red.svg)](https://github.com/tombcato/ParticleSmasher/releases)

**English** | [中文](README.md)

A powerful and highly optimized Android library that disintegrates any View into particles. Perfect for delete animations, transitions, or satisfying visual effects. fork from [ParticleSmasher](https://github.com/ifadai/ParticleSmasher)。

<img src="screen.gif" width="300" />

## 📖 Index

- [✨ Features](#-features)
- [🚀 Performance Optimizations (v2.0)](#-performance-optimizations-v20)
- [📦 Installation](#-installation)
- [💻 Usage](#-usage)
    - [Basic Usage](#basic-usage)
    - [Advanced Configuration](#advanced-configuration)
    - [Utility Methods](#utility-methods)
- [🎨 Styles & Configuration](#-styles--configuration)
    - [Animation Styles](#animation-styles-setstyle)
    - [Scale Modes](#scale-modes-setscalemode)
- [📄 License](#-license)

## ✨ Features

- **10 Core Animations**: Explosion, Drop, Float (4 directions), Rise (4 directions).
- **Highly Customizable**: Control duration, particle size, shape, spread range, and more.
- **Fluent API**: Builder-style configuration for clean and readable code.
- **Visual Effects**:
    - **Scale Modes**: Particles can shrink, grow, or stay the same size.
    - **Density Control**: Adjust particle gap for sparse or dense (overlapping) effects.
    - **Smart Randomness**: Configurable start delays and fade-out randomness.
- **Multi-scenario Support**: Perfectly supports Activity, Fragment, Dialog, and custom ViewGroup with automatic singleton and lifecycle management.
- **Performance Optimized**: Built for smooth 60fps animations even with high particle counts.

## 🚀 Performance Optimizations (v2.0+)

ParticleSmasher v2.0+ introduces significant performance improvements:

### v2.1 (Latest)
- **1D Particle Array**: Changed from `Particle[][]` to `Particle[]` for better cache locality and reduced loop overhead
- **Hardware Acceleration**: Enabled `LAYER_TYPE_HARDWARE` for GPU rendering
- **Smart Dirty Region**: Multi-animation dirty rect merging with auto full-screen fallback (>80% coverage)
- **Cached Values**: `getAnimatedValue()` cached per frame, `ThreadLocalRandom` reuse
- **Static Factory Pattern**: `ParticleSmasher.get(Activity/Fragment/ViewGroup)` with WeakHashMap caching
- **Lifecycle-aware**: Auto cleanup on Activity/Fragment destruction
- **ProGuard Ready**: Includes `consumer-rules.pro` for library consumers
- **Dirty Region Invalidation**: Only redraws the particle bounding box, massively reducing GPU overdraw
- **Alpha Pre-calculation**: Cached during initialization, eliminates redundant `Color.alpha()` calls
- **Memory Efficiency**: `CopyOnWriteArrayList` for thread safety

## Implemented Features

### Core & Fixes
- [x] **Gradle & AGP Upgrade** (Gradle 8.13, AGP 8.13.2)
- [x] **AndroidX Migration** (Support → AndroidX)
- [x] **Java 17 Support**
- [x] **Critical Bug Fixes**
    - [x] Fixed animation position drift
    - [x] Fixed unresponsive click after hide (reShowView)
    - [x] **Fixed crash when View is not laid out** (width/height <= 0)
    - [x] **Fixed animation clipping for partially visible Views** (Render only visible area)
- [x] **Concurrency Optimization** (CopyOnWriteArrayList)

### Animation Effects
- [x] **New Animation Styles** (Rise series: Upward float)
- [x] **Particle Shapes** (Circle / Square)
- [x] **Interpolator Selection** (Support custom interpolators)
- [x] **Shake Animation Toggle**
- [x] **Randomness Configuration**
    - `setStartRandomness()`: Controls the uniformity of particle launch
    - `setEndRandomness()`: Controls the randomness of particle fading
- [x] **Particle Scale Modes**
    - `SCALE_DOWN`: Shrink over time (Default)
    - `SCALE_SAME`: Constant size
    - `SCALE_UP`: Grow over time
- [x] **Particle Gap/Density Control**
    - `setParticleGap(int px)`: Allows positive gap (sparse) or negative gap (overlapping/dense).

### Demo & UI
- [x] **Full Control Panel** (Configure all parameters)
- [x] **Config Persistence** (SharedPreferences)
- [x] **Immersive Status Bar** (White background + Dark icons)
- [x] **Layout Optimization** (Fixed header/footer, scrollable content)
- [x] **Concurrent Animation Demo** (Support smashing multiple views simultaneously)

## 📦 Installation

### Step 1. Add the JitPack repository
In your `settings.gradle` (or project-level `build.gradle`):

```groovy
dependencyResolutionManagement {
    repositories {
        // ...
        maven { url 'https://jitpack.io' }
    }
}
```

### Step 2. Add the dependency
In your app-level `build.gradle`:

```groovy
dependencies {
    implementation 'com.github.tombcato:ParticleSmasher:v2.1.0'
}
```

## 💻 Usage

### Basic Usage

#### 1. Activity Binding (Most Common)
Supports the entire Activity range (particles can fly anywhere).

```java
ParticleSmasher smasher = ParticleSmasher.get(this); // this is Activity
smasher.with(view).start();
```

#### 2. Fragment Binding
Supports Views inside a Fragment.

```java
// Inside Fragment
ParticleSmasher smasher = ParticleSmasher.get(this); // this is Fragment
smasher.with(view).start();
```

#### 3. ViewGroup Binding
Restrict particle animation within a specific container, or for Dialog / PopupWindow.

```java
ParticleSmasher smasher = ParticleSmasher.get(viewGroup);
smasher.with(view).start();
```

### Advanced Configuration
Customize every aspect of the animation:

```java
smasher.with(targetView)
    .setStyle(SmashAnimator.STYLE_RISE)        // Animation Style
    .setShape(SmashAnimator.SHAPE_CIRCLE)      // Particle Shape: CIRCLE or SQUARE
    .setDuration(1500)                         // Duration in ms
    .setStartDelay(100)                        // Delay before start
    .setHorizontalMultiple(3f)                 // Horizontal spread factor
    .setVerticalMultiple(4f)                   // Vertical spread factor
    .setParticleRadius(Utils.dp2Px(2))         // Particle Base Radius
    .setParticleGap(Utils.dp2Px(0))            // Gap: <0 for overlap(dense), >0 for sparse
    .setScaleMode(SmashAnimator.SCALE_DOWN)    // Scale: DOWN, SAME, or UP
    .setStartRandomness(0.1f)                  // 0.0 ~ 1.0: Randomness of departure
    .setEndRandomness(0.5f)                    // 0.0 ~ 1.0: Randomness of fading
    .setHideAnimation(true)                    // Enable shake & scale-down of original view
    .addAnimatorListener(new SmashAnimator.OnAnimatorListener() {
        @Override
        public void onAnimatorEnd() {
            // Callback when animation finishes
        }
    })
    .start();
```
Demo config:
![alt text](6ba9f2ffad988a7c6e57162f1353eed4.jpg)

### Utility Methods

```java
// Check if a view is currently animating
if (smasher.isAnimating(view)) { ... }

// Restore a smashed view to its original state
smasher.reShowView(view);
```

## 🎨 Styles & Configuration

### Animation Styles (`setStyle`)
| Constant | Description |
|:---|:---|
| `STYLE_EXPLOSION` | Particles explode in all directions (Default). |
| `STYLE_DROP` | Particles fall downwards due to gravity. |
| `STYLE_RISE` | Particles float upwards like smoke/magic. |
| `STYLE_FLOAT_LEFT` | Float towards the right. |
| `STYLE_FLOAT_RIGHT`| Float towards the left. |
| `STYLE_FLOAT_TOP`  | Float downwards (layered). |
| `STYLE_FLOAT_BOTTOM`| Float upwards (layered). |
| ... | (And directional variations for Rise) |

### Scale Modes (`setScaleMode`)
| Constant | Description |
|:---|:---|
| `SCALE_DOWN` | Particles shrink over time (Default). Great for debris. |
| `SCALE_SAME` | Particles maintain constant size. |
| `SCALE_UP` | Particles grow over time. Good for smoke or magic effects. |

## 📄 License

```
Copyright 2026 TombCato

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
