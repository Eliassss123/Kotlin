// ============================================================================
// ARCHIVO : ui/protocolos/ListaProtocolosScreen.kt
// CAPA    : View (pantalla)
// RESUMEN : historial de protocolos guardados (los más recientes primero, según la consulta del DAO).
// ============================================================================

package com.induztek.protocolos.ui.protocolos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.induztek.protocolos.model.Protocolo
import com.induztek.protocolos.ui.components.EstadoChip
import com.induztek.protocolos.ui.components.InduztekTopBar
import com.induztek.protocolos.ui.theme.AzulPetroleo
import com.induztek.protocolos.ui.theme.FondoApp
import com.induztek.protocolos.ui.theme.TextoSecundario
import com.induztek.protocolos.viewmodel.ProtocoloViewModel

// onProtocoloSelected entrega el id del protocolo tocado; NavGraph abre el detalle.
@Composable
fun ListaProtocolosScreen(
    viewModel: ProtocoloViewModel,
    onNavigateBack: () -> Unit,
    onProtocoloSelected: (String) -> Unit
) {
    // Lista observada: cuando se guarda un protocolo nuevo, aparece sola.
    val protocolos by viewModel.protocolos.collectAsState()

    // Esqueleto con barra superior (ver ListaEquiposScreen).
    Scaffold(
        topBar = {
            InduztekTopBar(
                titulo = "Historial de Protocolos",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(FondoApp)
                .padding(16.dp)
        ) {
            if (protocolos.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "No se registran protocolos guardados", color = TextoSecundario)
                }
            } else {
                // Lista con scroll (solo dibuja lo visible). spacedBy(12.dp) = separación entre tarjetas.
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(protocolos) { protocolo ->
                        ProtocoloCard(
                            protocolo = protocolo,
                            onClick = { onProtocoloSelected(protocolo.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
// Componente de UNA tarjeta de protocolo.
fun ProtocoloCard(
    protocolo: Protocolo,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = protocolo.codigoProtocolo,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AzulPetroleo
                )
                EstadoChip(estado = protocolo.estado)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Equipo: ${protocolo.equipoCodigo} (${protocolo.tipoEquipo})",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "📅 ${protocolo.fechaHora} | 👤 ${protocolo.tecnico}",
                fontSize = 12.sp,
                color = TextoSecundario
            )
        }
    }
}
