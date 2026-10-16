// ============================================================================
// ARCHIVO : ui/components/EstadoChip.kt
// CAPA    : View - componente reutilizable
// RESUMEN : Chips de estado y resultados con colores corporativos Induztek:
//           - Estados de protocolo: Formalizado (Verde), Registrado en terreno (Ámbar),
//             Observado (Naranja/Rojo), Borrador (Gris), Pendiente de formalización (Azul/Gris).
//           - Resultados de prueba: Aprobado (Verde), Observado (Ámbar), Rechazado (Rojo), No Realizada (Gris).
// ============================================================================

package com.induztek.protocolos.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.induztek.protocolos.model.EstadoProtocolo
import com.induztek.protocolos.model.ResultadoPrueba

@Composable
fun EstadoChip(
    estado: EstadoProtocolo,
    esCliente: Boolean = false,
    modifier: Modifier = Modifier
) {
    val texto = estado.getLabelCliente(esCliente)
    val (backgroundColor, textColor) = when (estado) {
        EstadoProtocolo.FORMALIZADO -> Color(0xFF2E7D32) to Color.White
        EstadoProtocolo.REGISTRADO_TERRENO -> {
            if (esCliente) Color(0xFF455A64) to Color.White else Color(0xFFFFA000) to Color.Black
        }
        EstadoProtocolo.OBSERVADO -> Color(0xFFD32F2F) to Color.White
        EstadoProtocolo.BORRADOR -> Color(0xFF78909C) to Color.White
    }

    Text(
        text = texto,
        color = textColor,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .background(color = backgroundColor, shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}

@Composable
fun ResultadoChip(
    resultado: ResultadoPrueba,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = when (resultado) {
        ResultadoPrueba.APROBADO -> Color(0xFF2E7D32) to Color.White
        ResultadoPrueba.OBSERVADO -> Color(0xFFFFA000) to Color.Black
        ResultadoPrueba.RECHAZADO -> Color(0xFFD32F2F) to Color.White
        ResultadoPrueba.NO_REALIZADA -> Color(0xFF757575) to Color.White
    }

    Text(
        text = resultado.label,
        color = textColor,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .background(color = backgroundColor, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    )
}
