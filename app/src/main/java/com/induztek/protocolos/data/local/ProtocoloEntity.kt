// ============================================================================
// ARCHIVO : data/local/ProtocoloEntity.kt
// CAPA    : Datos locales (Room)
// RESUMEN : Tabla 'protocolos' según RN-02, RN-04, RN-10, RN-16, RN-17, RT-03, RT-05 y RT-09:
//           - Vinculación a Visita y Equipo
//           - Resultado global calculado
//           - Descripción de mantenimiento si aplica
//           - Trazabilidad de formalización e inmutabilidad
// ============================================================================

package com.induztek.protocolos.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.induztek.protocolos.model.EstadoProtocolo
import com.induztek.protocolos.model.Medicion
import com.induztek.protocolos.model.Protocolo
import com.induztek.protocolos.model.Prueba
import com.induztek.protocolos.model.ResultadoPrueba
import com.induztek.protocolos.model.SyncStatus

@Entity(tableName = "protocolos")
data class ProtocoloEntity(
    @PrimaryKey val id: String,
    val codigoProtocolo: String,
    val visitaId: String = "",
    val ordenTrabajo: String = "OT-2026-084",
    val equipoCodigo: String,
    val tipoEquipo: String,
    val fechaHora: String,
    val estado: String,
    val resultadoGlobal: String = "Aprobado",
    val tecnico: String,
    val tecnicoCreador: String = "Juan Pérez",
    val descripcionMantenimiento: String = "",
    val instrumento: String = "Megger MIT525",
    val calibracionVigente: Boolean = true,
    val temperaturaAmbiente: String = "22°C",
    val humedad: String = "45%",
    val actaEntregadaCliente: Boolean = true,
    val formalizadoPor: String? = null,
    val fechaFormalizacion: String? = null,
    val syncStatus: String = "Pendiente de Sincronización",
    val observaciones: String = "",
    val pruebasJson: String = "[]",
    val medicionesJson: String = "[]"
) {
    fun toDomain(): Protocolo {
        val listMedType = object : TypeToken<List<Medicion>>() {}.type
        val listPruType = object : TypeToken<List<Prueba>>() {}.type
        val medicionesList: List<Medicion> = try {
            Gson().fromJson(medicionesJson, listMedType) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
        val pruebasList: List<Prueba> = try {
            Gson().fromJson(pruebasJson, listPruType) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }

        return Protocolo(
            id = id,
            codigoProtocolo = codigoProtocolo,
            visitaId = visitaId,
            ordenTrabajo = ordenTrabajo,
            equipoCodigo = equipoCodigo,
            tipoEquipo = tipoEquipo,
            fechaHora = fechaHora,
            estado = EstadoProtocolo.fromLabel(estado),
            resultadoGlobal = ResultadoPrueba.fromLabel(resultadoGlobal),
            tecnico = tecnico,
            tecnicoCreador = tecnicoCreador,
            descripcionMantenimiento = descripcionMantenimiento,
            instrumento = instrumento,
            calibracionVigente = calibracionVigente,
            temperaturaAmbiente = temperaturaAmbiente,
            humedad = humedad,
            actaEntregadaCliente = actaEntregadaCliente,
            formalizadoPor = formalizadoPor,
            fechaFormalizacion = fechaFormalizacion,
            syncStatus = SyncStatus.fromLabel(syncStatus),
            observaciones = observaciones,
            pruebas = pruebasList,
            mediciones = medicionesList
        )
    }

    companion object {
        fun fromDomain(protocolo: Protocolo): ProtocoloEntity {
            return ProtocoloEntity(
                id = protocolo.id,
                codigoProtocolo = protocolo.codigoProtocolo,
                visitaId = protocolo.visitaId,
                ordenTrabajo = protocolo.ordenTrabajo,
                equipoCodigo = protocolo.equipoCodigo,
                tipoEquipo = protocolo.tipoEquipo,
                fechaHora = protocolo.fechaHora,
                estado = protocolo.estado.label,
                resultadoGlobal = protocolo.resultadoGlobal.label,
                tecnico = protocolo.tecnico,
                tecnicoCreador = protocolo.tecnicoCreador,
                descripcionMantenimiento = protocolo.descripcionMantenimiento,
                instrumento = protocolo.instrumento,
                calibracionVigente = protocolo.calibracionVigente,
                temperaturaAmbiente = protocolo.temperaturaAmbiente,
                humedad = protocolo.humedad,
                actaEntregadaCliente = protocolo.actaEntregadaCliente,
                formalizadoPor = protocolo.formalizadoPor,
                fechaFormalizacion = protocolo.fechaFormalizacion,
                syncStatus = protocolo.syncStatus.label,
                observaciones = protocolo.observaciones,
                pruebasJson = Gson().toJson(protocolo.pruebas),
                medicionesJson = Gson().toJson(protocolo.mediciones)
            )
        }
    }
}
