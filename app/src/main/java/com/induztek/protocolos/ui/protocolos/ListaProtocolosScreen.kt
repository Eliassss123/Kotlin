package com.induztek.protocolos.ui.protocolos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.induztek.protocolos.model.PerfilUsuario
import com.induztek.protocolos.model.Protocolo
import com.induztek.protocolos.ui.components.EstadoChip
import com.induztek.protocolos.ui.components.InduztekBottomBar
import com.induztek.protocolos.ui.components.InduztekTopBar
import com.induztek.protocolos.ui.components.ResultadoChip
import com.induztek.protocolos.ui.navigation.Routes
import com.induztek.protocolos.ui.theme.*
import com.induztek.protocolos.viewmodel.LoginViewModel
import com.induztek.protocolos.viewmodel.ProtocoloViewModel

@Composable
fun ListaProtocolosScreen(
    viewModel: ProtocoloViewModel,
    loginViewModel: LoginViewModel,
    onNavigateBack: () -> Unit,
    onProtocoloSelected: (String) -> Unit,
    onTabSelected: (String) -> Unit = {}
) {
    val protocolos by viewModel.protocolos.collectAsState()
    val usuarioActivo by loginViewModel.usuarioActivo.collectAsState()
    val remoteError by viewModel.remoteError.collectAsState()

    val perfil = usuarioActivo?.getPerfil() ?: PerfilUsuario.TECNICO_TERRENO
    val esCliente = perfil == PerfilUsuario.CLIENTE
    val esAdminOJefe = perfil == PerfilUsuario.JEFE_TECNICO || perfil == PerfilUsuario.INGENIERO_PRUEBAS

    LaunchedEffect(usuarioActivo) {
        if (esCliente && !usuarioActivo?.empresaCliente.isNullOrBlank()) {
            viewModel.setEmpresaCliente(usuarioActivo?.empresaCliente)
        } else {
            viewModel.setEmpresaCliente(null)
        }
    }

    Scaffold(
        containerColor = FondoApp,
        topBar = {
            InduztekTopBar(
                titulo = if (esCliente) "Protocolos de ${usuarioActivo?.empresaCliente ?: "Empresa"}" else "Historial de Protocolos",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        },
        bottomBar = {
            InduztekBottomBar(
                currentRoute = Routes.ListaProtocolos.route,
                esCliente = esCliente,
                esAdminOJefe = esAdminOJefe,
                onTabSelected = onTabSelected
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(FondoApp)
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            remoteError?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            if (protocolos.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No se registran protocolos guardados",
                        color = TextoSecundario,
                        fontSize = 15.sp
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(protocolos) { protocolo ->
                        ProtocoloCard(
                            protocolo = protocolo,
                            esCliente = esCliente,
                            onClick = { onProtocoloSelected(protocolo.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProtocoloCard(
    protocolo: Protocolo,
    esCliente: Boolean,
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
                    text = "${protocolo.codigoProtocolo} · ${protocolo.equipoCodigo}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                EstadoChip(estado = protocolo.estado, esCliente = esCliente)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = protocolo.tipoEquipo,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextoSecundario
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "OT: ${protocolo.ordenTrabajo} | ${protocolo.fechaHora}",
                    fontSize = 12.sp,
                    color = TextoSecundario
                )
                ResultadoChip(resultado = protocolo.resultadoGlobal)
            }
        }
    }
}
