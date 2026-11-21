package com.induztek.protocolos.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import java.util.Locale

object LocationHelper {

    fun formatearCoordenadas(latitud: Double, longitud: Double): String {
        return String.format(Locale.US, "%.5f, %.5f", latitud, longitud)
    }

    fun validarCoordenadas(latitud: Double, longitud: Double): Boolean {
        return latitud in -90.0..90.0 && longitud in -180.0..180.0 && (latitud != 0.0 || longitud != 0.0)
    }

    fun estaGpsHabilitado(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            ?: return false
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    @SuppressLint("MissingPermission")
    fun obtenerCoordenadasActuales(
        context: Context,
        onExito: (latitud: Double, longitud: Double, formatoTexto: String) -> Unit,
        onError: (mensaje: String) -> Unit
    ) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        if (locationManager == null) {
            onError("Servicio de ubicación no disponible en el dispositivo")
            return
        }

        if (!estaGpsHabilitado(context)) {
            onError("El GPS está desactivado. Por favor actívalo en los ajustes.")
            return
        }

        try {
            val ultimaGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            val ultimaRed = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            val mejorUbicacion: Location? = when {
                ultimaGps != null && ultimaRed != null -> if (ultimaGps.time >= ultimaRed.time) ultimaGps else ultimaRed
                ultimaGps != null -> ultimaGps
                else -> ultimaRed
            }

            if (mejorUbicacion != null) {
                val formato = formatearCoordenadas(mejorUbicacion.latitude, mejorUbicacion.longitude)
                onExito(mejorUbicacion.latitude, mejorUbicacion.longitude, formato)
                return
            }

            val provider = if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                LocationManager.NETWORK_PROVIDER
            } else {
                LocationManager.GPS_PROVIDER
            }

            val listener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    locationManager.removeUpdates(this)
                    val formato = formatearCoordenadas(location.latitude, location.longitude)
                    onExito(location.latitude, location.longitude, formato)
                }
                override fun onProviderDisabled(provider: String) {}
                override fun onProviderEnabled(provider: String) {}
                @Deprecated("Deprecated in Java")
                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            }

            locationManager.requestSingleUpdate(provider, listener, Looper.getMainLooper())
        } catch (e: SecurityException) {
            onError("Permiso de ubicación denegado: ${e.message}")
        } catch (e: Exception) {
            onError("Error al obtener ubicación GPS: ${e.message}")
        }
    }
}
