package com.induztek.protocolos.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat

/**
 * Utilidad centralizada para el manejo de notificaciones del sistema Android
 * y retroalimentación háptica (vibración) en eventos críticos de la aplicación.
 */
object NotificationHelper {
    private const val CHANNEL_ID = "induztek_bienvenida_channel"

    /**
     * Genera una vibración de corta duración en el dispositivo.
     * Utilizado al iniciar sesión con éxito y al generar nuevos protocolos en terreno.
     *
     * @param context Contexto de la aplicación o actividad.
     * @param durationMillis Duración de la vibración en milisegundos (por defecto 200ms).
     */
    fun vibrar(context: Context, durationMillis: Long = 200) {
        try {
            // Obtener el servicio de vibración según la versión de Android
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }

            // Ejecutar el efecto de vibración
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(durationMillis, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMillis)
            }
        } catch (_: Exception) {}
    }

    /**
     * Envía una notificación push local al sistema indicando el mensaje de bienvenida con el correo del usuario.
     *
     * @param context Contexto de la aplicación.
     * @param correo Correo electrónico con el cual el usuario ha iniciado sesión.
     */
    fun enviarNotificacionBienvenida(context: Context, correo: String) {
        try {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Crear el canal de notificación requerido para Android 8.0 (Oreo) en adelante
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    "Notificaciones de Bienvenida",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Canal para notificaciones de inicio de sesión Induztek"
                }
                notificationManager.createNotificationChannel(channel)
            }

            // Construir la notificación con los datos del usuario
            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Induztek Protocolos")
                .setContentText("Bienvenido ($correo)")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)

            // Verificar permisos de notificación en Android 13+ antes de emitir la alerta
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            ) {
                notificationManager.notify(1001, builder.build())
            }
        } catch (_: Exception) {}
    }
}
