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
import com.induztek.protocolos.ui.theme.EstadoFormalizadoVerde
import com.induztek.protocolos.ui.theme.EstadoFueraDeRangoRojo
import com.induztek.protocolos.ui.theme.EstadoRegistradoAmbar

@Composable
fun EstadoChip(
    estado: EstadoProtocolo,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = when (estado) {
        EstadoProtocolo.FORMALIZADO -> EstadoFormalizadoVerde to Color.White
        EstadoProtocolo.REGISTRADO_TERRENO -> EstadoRegistradoAmbar to Color.Black
        EstadoProtocolo.FUERA_DE_RANGO -> EstadoFueraDeRangoRojo to Color.White
    }

    Text(
        text = estado.label,
        color = textColor,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .background(color = backgroundColor, shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}
