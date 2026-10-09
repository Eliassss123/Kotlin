package com.induztek.protocolos.ui.visitas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.induztek.protocolos.data.local.UsuarioEntity
import com.induztek.protocolos.model.PerfilUsuario
import com.induztek.protocolos.model.Visita
import com.induztek.protocolos.ui.components.InduztekTopBar
import com.induztek.protocolos.ui.theme.AzulPetroleo
import com.induztek.protocolos.ui.theme.FondoApp
import com.induztek.protocolos.ui.theme.NaranjaSeguridad
import com.induztek.protocolos.ui.theme.TextoSecundario
import com.induztek.protocolos.viewmodel.VisitaViewModel

@Composable
fun ListaVisitasScreen(
    visitaViewModel: VisitaViewModel,
    usuarioActual: UsuarioEntity? = null,
    onNavigateBack: () -> Unit,
    onVisitaClick: (String) -> Unit,
    onNuevaVisitaClick: () -> Unit
) {
    val visitas by visitaViewModel.todasLasVisitas.collectAsState()
    val puedeCrear = usuarioActual == null || usuarioActual.getPerfil() != PerfilUsuario.CLIENTE

    Scaffold(
        topBar = {
            InduztekTopBar(
                titulo = "Visitas Técnicas en Terreno (RT-01)",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        },
        floatingActionButton = {
            if (puedeCrear) {
                FloatingActionButton(
                    onClick = onNuevaVisitaClick,
                    containerColor = NaranjaSeguridad,
                    contentColor = Color.Black
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Nueva Visita")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(FondoApp)
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (visitas.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay visitas registradas.\nPresione + para crear una nueva visita con su OT.",
                        color = TextoSecundario,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(visitas) { visita ->
                        VisitaItemCard(
                            visita = visita,
                            onClick = { onVisitaClick(visita.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VisitaItemCard(
    visita: Visita,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = visita.ordenTrabajo,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AzulPetroleo
                )
                if (visita.cerrada) {
                    Row(
                        modifier = Modifier
                            .background(Color(0xFFE8F5E9), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.height(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Cerrada (Acta lista)", fontSize = 11.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .background(Color(0xFFFFF3E0), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.PendingActions, contentDescription = null, tint = Color(0xFFE65100), modifier = Modifier.height(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "En Progreso", fontSize = 11.sp, color = Color(0xFFE65100), fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${visita.cliente} · ${visita.instalacion}",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Modalidad: ${visita.modalidad.label} | Ing. a Cargo: ${visita.ingenieroACargo}",
                fontSize = 12.sp,
                color = TextoSecundario
            )

            Text(
                text = "Fecha: ${visita.fecha} | Inicio: ${visita.horaInicio}",
                fontSize = 12.sp,
                color = TextoSecundario
            )
        }
    }
}
