package com.induztek.protocolos

import com.induztek.protocolos.model.ModalidadVisita
import com.induztek.protocolos.model.Visita
import com.induztek.protocolos.util.LocationHelper
import com.induztek.protocolos.util.NotificationHelper
import com.induztek.protocolos.viewmodel.NuevaVisitaState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ApisNativasTest {

    @Test
    fun formatoCoordenadasGps_coordenadasValidas_formatoCorrecto() {
        val lat = -33.44889
        val lng = -70.66926
        val formateado = LocationHelper.formatearCoordenadas(lat, lng)

        assertEquals("-33.44889, -70.66926", formateado)
    }

    @Test
    fun validacionCoordenadasGps_rangosCorrectos_retornaTrue() {
        // Coordenadas válidas en Santiago de Chile
        assertTrue(LocationHelper.validarCoordenadas(-33.4489, -70.6693))
        // Coordenadas en límites globales válidos
        assertTrue(LocationHelper.validarCoordenadas(90.0, 180.0))
        assertTrue(LocationHelper.validarCoordenadas(-90.0, -180.0))
    }

    @Test
    fun validacionCoordenadasGps_fueraDeRango_retornaFalse() {
        // Latitud superior a 90 o inferior a -90
        assertFalse(LocationHelper.validarCoordenadas(95.0, -70.6693))
        assertFalse(LocationHelper.validarCoordenadas(-91.0, -70.6693))
        // Longitud superior a 180 o inferior a -180
        assertFalse(LocationHelper.validarCoordenadas(-33.4489, 185.0))
        assertFalse(LocationHelper.validarCoordenadas(-33.4489, -190.0))
        // Coordenadas en 0,0 (punto nulo sin fijar)
        assertFalse(LocationHelper.validarCoordenadas(0.0, 0.0))
    }

    @Test
    fun nuevaVisitaState_incorporacionGps_actualizaEstadoCorrectamente() {
        val estadoInicial = NuevaVisitaState(
            ordenTrabajo = "OT-2026-901",
            cliente = "Planta Minera El Roble",
            instalacion = "Subestación Principal 110 kV"
        )

        val coordenadasSimuladas = "-33.44890, -70.66930"
        val estadoConGps = estadoInicial.copy(
            coordenadasGps = coordenadasSimuladas,
            instalacion = "${estadoInicial.instalacion} [GPS: $coordenadasSimuladas]"
        )

        assertNotNull(estadoConGps.coordenadasGps)
        assertEquals(coordenadasSimuladas, estadoConGps.coordenadasGps)
        assertTrue(estadoConGps.instalacion.contains("[GPS: -33.44890, -70.66930]"))
    }

    @Test
    fun visitaModel_conCoordenadasGps_mantieneTrazabilidad() {
        val visita = Visita(
            ordenTrabajo = "OT-2026-902",
            fecha = "2026-11-21",
            horaInicio = "10:30",
            cliente = "Planta Lampa",
            instalacion = "Celda Media Tensión [GPS: -33.28450, -70.89210]",
            modalidad = ModalidadVisita.SOLO_PRUEBAS,
            ingenieroACargo = "Carlos Mendoza",
            tecnicos = "Juan Pérez"
        )

        assertEquals("OT-2026-902", visita.ordenTrabajo)
        assertTrue(visita.instalacion.contains("[GPS:"))
        assertEquals(ModalidadVisita.SOLO_PRUEBAS, visita.modalidad)
    }

    @Test
    fun canalesNotificacion_configuracionEIdentificadores_sonValidos() {
        // Canales no vacíos
        assertTrue(NotificationHelper.CHANNEL_OPERACIONES_ID.isNotBlank())
        assertTrue(NotificationHelper.CHANNEL_SEGURIDAD_ID.isNotBlank())
        assertTrue(NotificationHelper.CHANNEL_OPERACIONES_NAME.isNotBlank())
        assertTrue(NotificationHelper.CHANNEL_SEGURIDAD_NAME.isNotBlank())

        // Canales diferentes entre sí
        assertNotEquals(
            NotificationHelper.CHANNEL_OPERACIONES_ID,
            NotificationHelper.CHANNEL_SEGURIDAD_ID
        )
    }

    @Test
    fun identificadoresNotificacion_sonUnicosYPositivos() {
        assertTrue(NotificationHelper.NOTIF_ID_RECUPERACION > 0)
        assertTrue(NotificationHelper.NOTIF_ID_FORMALIZACION > 0)
        assertTrue(NotificationHelper.NOTIF_ID_VISITA > 0)

        // Todos los IDs de notificación deben ser distintos para no sobreescribirse
        val ids = setOf(
            NotificationHelper.NOTIF_ID_RECUPERACION,
            NotificationHelper.NOTIF_ID_FORMALIZACION,
            NotificationHelper.NOTIF_ID_VISITA
        )
        assertEquals(3, ids.size)
    }
}
