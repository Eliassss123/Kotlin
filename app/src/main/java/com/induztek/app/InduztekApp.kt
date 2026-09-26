package com.induztek.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class requerida por Hilt.
 *
 * @HiltAndroidApp genera el componente Hilt de la aplicación en tiempo de compilación.
 * Esta clase debe registrarse en AndroidManifest.xml como android:name=".InduztekApp".
 */
@HiltAndroidApp
class InduztekApp : Application()
