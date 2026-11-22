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
import com.induztek.protocolos.model.UserProfile
import com.induztek.protocolos.model.PerfilUsuario
import com.induztek.protocolos.model.Visita
import com.induztek.protocolos.ui.components.InduztekBottomBar
import com.induztek.protocolos.ui.components.InduztekTopBar
import com.induztek.protocolos.ui.navigation.Routes
import com.induztek.protocolos.ui.theme.*
import com.induztek.protocolos.viewmodel.VisitaViewModel

@Composable
fun ListaVisitasScreen(
    visitaViewModel: VisitaViewModel,
    usuarioActual: UserProfile? = null,
    onNavigateBack: () -> Unit,
    onVisitaClick: (String) -> Unit,
    onNuevaVisitaClick: () -> Unit,
    onTabSelected: (String) -> Unit = {}
) {
    val visitas by visitaViewModel.todasLasVisitas.collectAsState()
    val mensajeError by visitaViewModel.mensajeError.collectAsState()
    val perfil = usuarioActual?.getPerfil() ?: PerfilUsuario.TECNICO_TERRENO
    val esCliente = perfil == PerfilUsuario.CLIENTE
    val esAdminOJefe = perfil == PerfilUsuario.JEFE_TECNICO || perfil == PerfilUsuario.INGENIERO_PRUEBAS
    val puedeCrear = !esCliente

    Scaffold(
        containerColor = FondoApp,
        topBar = {
            InduztekTopBar(
                titulo = "Visitas Técnicas en Terreno (RT-01)",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        },
        bottomBar = {
            InduztekBottomBar(
                currentRoute = Routes.ListaVisitas.route,
                esCliente = esCliente,
                esAdminOJefe = esAdminOJefe,
                onTabSelected = onTabSelected
            )
        },
        floatingActionButton = {
            if (puedeCrear) {
                FloatingActionButton(
                    onClick = onNuevaVisitaClick,
                    containerColor = InduztekBlue,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(999.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                        Text("Crear Visita Técnica", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
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
            mensajeError?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            if (visitas.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay visitas registradas.\nPresione Crear Visita para comenzar.",
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
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TarjetaFondo),
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
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoPrincipal
                )
                if (visita.cerrada) {
                    Row(
                        modifier = Modifier
                            .background(Color(0xFF0F9D6B).copy(alpha = 0.16f), RoundedCornerShape(999.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF0F9D6B), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Cerrada (Acta lista)", fontSize = 12.sp, color = Color(0xFF0F9D6B), fontWeight = FontWeight.Bold)
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .background(Color(0xFFFFB300).copy(alpha = 0.16f), RoundedCornerShape(999.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.PendingActions, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "En Progreso", fontSize = 12.sp, color = Color(0xFFFFB300), fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "${visita.cliente} · ${visita.instalacion}",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextoPrincipal
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Modalidad: ${visita.modalidad.label} | Ing. a Cargo: ${visita.ingenieroACargo}",
                fontSize = 13.sp,
                color = TextoSecundario
            )

            Text(
                text = "Fecha: ${visita.fecha} | Inicio: ${visita.horaInicio}",
                fontSize = 13.sp,
                color = TextoSecundario
            )
        }
    }
}
