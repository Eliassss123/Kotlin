// Top-level build file — configuración que aplica a todos los módulos.
// Las dependencias de cada módulo van en app/build.gradle.kts, NO aquí.
plugins {
    alias(libs.plugins.android.application)  apply false
    alias(libs.plugins.kotlin.android)       apply false
    alias(libs.plugins.kotlin.compose)       apply false
    alias(libs.plugins.ksp)                  apply false
    alias(libs.plugins.hilt)                 apply false
}
