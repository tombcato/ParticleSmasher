# ParticleSmasher library ProGuard rules

# Keep all public classes and members
-keep class com.tombcato.particlesmasher.ParticleSmasher { *; }
-keep class com.tombcato.particlesmasher.SmashAnimator { *; }
-keep class com.tombcato.particlesmasher.SmashAnimator$* { *; }
-keep class com.tombcato.particlesmasher.Utils { *; }

# Keep Particle class hierarchy
-keep class com.tombcato.particlesmasher.particle.Particle { *; }
-keep class com.tombcato.particlesmasher.particle.Particle$* { *; }
-keep class * extends com.tombcato.particlesmasher.particle.Particle { *; }

# Keep enum classes
-keepclassmembers enum com.tombcato.particlesmasher.** {
    <fields>;
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
