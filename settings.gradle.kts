try {
    val processEnvironment = Class.forName("java.lang.ProcessEnvironment")
    val unmodifiableMapField = processEnvironment.getDeclaredField("theUnmodifiableEnvironment")
    unmodifiableMapField.isAccessible = true
    val map = unmodifiableMapField.get(null) as? MutableMap<Any, Any>
    map?.remove("ANDROID_PREFS_ROOT")

    val envField = processEnvironment.getDeclaredField("theEnvironment")
    envField.isAccessible = true
    val envMap = envField.get(null) as? MutableMap<Any, Any>
    envMap?.remove("ANDROID_PREFS_ROOT")
} catch (e: Throwable) {
    
}

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "InduztekProtocolos"
include(":app")
