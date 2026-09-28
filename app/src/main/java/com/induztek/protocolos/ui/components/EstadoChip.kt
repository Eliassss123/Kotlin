// ============================================================================
// ARCHIVO : ui/components/EstadoChip.kt
// CAPA    : View - componente reutilizable
// RESUMEN : la "píldora" de color con el estado (verde/ámbar/rojo). Se define UNA vez y se usa en varias pantallas,
//           como un componente web reutilizable.
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
import com.induztek.protocolos.ui.theme.EstadoFormalizadoVerde
import com.induztek.protocolos.ui.theme.EstadoFueraDeRangoRojo
import com.induztek.protocolos.ui.theme.EstadoRegistradoAmbar

// @Composable: esta función dibuja interfaz.
@Composable
// Recibe el estado a mostrar y un 'modifier' opcional (permite que quien lo use le agregue estilos).
fun EstadoChip(
    estado: EstadoProtocolo,
    modifier: Modifier = Modifier
) {
    // when = como un 'switch'. Devuelve un par de colores (fondo y texto) según el estado. 'a to b' crea un par; '(x,
    //   y) =' lo separa (desestructuración).
    val (backgroundColor, textColor) = when (estado) {
        EstadoProtocolo.FORMALIZADO -> EstadoFormalizadoVerde to Color.White
        EstadoProtocolo.REGISTRADO_TERRENO -> EstadoRegistradoAmbar to Color.Black
        EstadoProtocolo.FUERA_DE_RANGO -> EstadoFueraDeRangoRojo to Color.White
    }

    // Text muestra el texto del estado. El modifier encadena estilos como CSS: background (fondo con esquinas
    //   redondeadas) y padding (espacio interno).
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
