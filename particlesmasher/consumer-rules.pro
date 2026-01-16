# ParticleSmasher consumer ProGuard rules
# These rules are automatically included when using this library

# Keep public API
-keep class com.tombcato.particlesmasher.ParticleSmasher { public *; }
-keep class com.tombcato.particlesmasher.SmashAnimator { public *; }
-keep interface com.tombcato.particlesmasher.SmashAnimator$* { *; }
-keep class com.tombcato.particlesmasher.Utils { public *; }

# Keep ScaleMode enum
-keep class com.tombcato.particlesmasher.particle.Particle$ScaleMode { *; }
