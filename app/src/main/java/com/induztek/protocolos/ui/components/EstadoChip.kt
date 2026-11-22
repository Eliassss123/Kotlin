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
        EstadoProtocolo.FORMALIZADO -> Color(0xFF0F9D6B).copy(alpha = 0.2f) to Color(0xFF34D399)
        EstadoProtocolo.REGISTRADO_TERRENO -> Color(0xFFFFB300).copy(alpha = 0.2f) to Color(0xFFFBBF24)
        EstadoProtocolo.OBSERVADO -> Color(0xFFEF5350).copy(alpha = 0.2f) to Color(0xFFF87171)
        EstadoProtocolo.BORRADOR -> Color(0xFF64748B).copy(alpha = 0.2f) to Color(0xFF94A3B8)
    }

    Text(
        text = texto,
        color = textColor,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        maxLines = 1,
        softWrap = false,
        modifier = modifier
            .background(color = backgroundColor, shape = RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}

@Composable
fun ResultadoChip(
    resultado: ResultadoPrueba,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = when (resultado) {
        ResultadoPrueba.APROBADO -> Color(0xFF0F9D6B).copy(alpha = 0.2f) to Color(0xFF34D399)
        ResultadoPrueba.OBSERVADO -> Color(0xFFFFB300).copy(alpha = 0.2f) to Color(0xFFFBBF24)
        ResultadoPrueba.RECHAZADO -> Color(0xFFEF5350).copy(alpha = 0.2f) to Color(0xFFF87171)
        ResultadoPrueba.NO_REALIZADA -> Color(0xFF64748B).copy(alpha = 0.2f) to Color(0xFF94A3B8)
    }

    Text(
        text = resultado.label,
        color = textColor,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        maxLines = 1,
        softWrap = false,
        modifier = modifier
            .background(color = backgroundColor, shape = RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}
