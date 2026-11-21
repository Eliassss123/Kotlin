package com.induztek.protocolos.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.induztek.protocolos.MainActivity
import com.induztek.protocolos.R

object NotificationHelper {

    const val CHANNEL_OPERACIONES_ID = "induztek_operaciones_channel_v3"
    const val CHANNEL_OPERACIONES_NAME = "Operaciones y Protocolos Induztek"

    const val CHANNEL_SEGURIDAD_ID = "induztek_seguridad_channel_v3"
    const val CHANNEL_SEGURIDAD_NAME = "Seguridad y Mensajes Induztek"

    const val NOTIF_ID_RECUPERACION = 1001
    const val NOTIF_ID_FORMALIZACION = 1002
    const val NOTIF_ID_VISITA = 1003

    private val patronVibracion = longArrayOf(0, 300, 200, 300)

    fun crearCanalesNotificacion(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                ?: return

            val sonidoDefecto = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()

            val canalOperaciones = NotificationChannel(
                CHANNEL_OPERACIONES_ID,
                CHANNEL_OPERACIONES_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones de protocolos formalizados y visitas técnicas"
                enableVibration(true)
                vibrationPattern = patronVibracion
                setSound(sonidoDefecto, audioAttributes)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }

            val canalSeguridad = NotificationChannel(
                CHANNEL_SEGURIDAD_ID,
                CHANNEL_SEGURIDAD_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Códigos de recuperación y alertas de seguridad emergentes"
                enableVibration(true)
                vibrationPattern = patronVibracion
                setSound(sonidoDefecto, audioAttributes)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }

            notificationManager.createNotificationChannel(canalOperaciones)
            notificationManager.createNotificationChannel(canalSeguridad)
        }
    }

    /**
     * Muestra una notificación flotante (Heads-up banner) estilo WhatsApp con sonido y vibración
     */
    fun mostrarNotificacionCodigoRecuperacion(context: Context, codigo: String): Boolean {
        return try {
            crearCanalesNotificacion(context)

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            val sonidoDefecto = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            val builder = NotificationCompat.Builder(context, CHANNEL_SEGURIDAD_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("💬 Induztek Seguridad")
                .setContentText("Tu código de verificación es: $codigo")
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .setBigContentTitle("💬 Induztek Seguridad")
                        .bigText("Tu código de verificación es: $codigo\nExpira en 10 minutos. Úsalo para restablecer tu contraseña.")
                )
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSound(sonidoDefecto)
                .setVibrate(patronVibracion)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)

            NotificationManagerCompat.from(context).notify(NOTIF_ID_RECUPERACION, builder.build())
            true
        } catch (e: SecurityException) {
            false
        } catch (e: Exception) {
            false
        }
    }

    fun mostrarNotificacionProtocoloFormalizado(context: Context, codigoProtocolo: String, equipo: String): Boolean {
        return try {
            crearCanalesNotificacion(context)

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            val sonidoDefecto = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            val builder = NotificationCompat.Builder(context, CHANNEL_OPERACIONES_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("📋 Protocolo Formalizado")
                .setContentText("Protocolo $codigoProtocolo ($equipo) firmado y cerrado")
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .setBigContentTitle("📋 Protocolo Formalizado")
                        .bigText("El protocolo técnico $codigoProtocolo para el equipo $equipo ha sido firmado y formalizado exitosamente.")
                )
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSound(sonidoDefecto)
                .setVibrate(patronVibracion)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)

            NotificationManagerCompat.from(context).notify(NOTIF_ID_FORMALIZACION, builder.build())
            true
        } catch (e: SecurityException) {
            false
        } catch (e: Exception) {
            false
        }
    }

    fun mostrarNotificacionVisitaCreada(context: Context, ordenTrabajo: String, lugar: String): Boolean {
        return try {
            crearCanalesNotificacion(context)

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            val sonidoDefecto = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            val builder = NotificationCompat.Builder(context, CHANNEL_OPERACIONES_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("📍 Nueva Visita Técnica")
                .setContentText("OT $ordenTrabajo en $lugar")
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .setBigContentTitle("📍 Nueva Visita Técnica")
                        .bigText("La orden de trabajo $ordenTrabajo en $lugar ha sido registrada con trazabilidad técnica.")
                )
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSound(sonidoDefecto)
                .setVibrate(patronVibracion)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)

            NotificationManagerCompat.from(context).notify(NOTIF_ID_VISITA, builder.build())
            true
        } catch (e: SecurityException) {
            false
        } catch (e: Exception) {
            false
        }
    }
}
