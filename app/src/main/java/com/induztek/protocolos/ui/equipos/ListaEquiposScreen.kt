// ============================================================================
// ARCHIVO : ui/equipos/ListaEquiposScreen.kt
// CAPA    : View (pantalla)
// RESUMEN : Catálogo de equipos con barra de sesión (P-01 a P-04), filtros por
//           clasificación Primario/Secundario (Aclar. 1), filtro por empresa para cliente (RT-10)
//           e indicador de tendencia de empeoramiento (RT-07).
//           Incluye menú hamburguesa lateral (NavigationDrawer) con acceso a Perfil,
//           Visitas, Protocolos y Cerrar Sesión.
// ============================================================================

package com.induztek.protocolos.ui.equipos

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.induztek.protocolos.model.ClasificacionEquipo
import com.induztek.protocolos.model.Equipo
import com.induztek.protocolos.model.PerfilUsuario
import com.induztek.protocolos.ui.components.EstadoChip
import com.induztek.protocolos.ui.theme.AzulPetroleo
import com.induztek.protocolos.ui.theme.BordeFondo
import com.induztek.protocolos.ui.theme.FondoApp
import com.induztek.protocolos.ui.theme.NaranjaSeguridad
import com.induztek.protocolos.ui.theme.TextoPrincipal
import com.induztek.protocolos.ui.theme.TextoSecundario
import com.induztek.protocolos.viewmodel.EquipoViewModel
import com.induztek.protocolos.viewmodel.LoginViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaEquiposScreen(
    viewModel: EquipoViewModel,
    loginViewModel: LoginViewModel,
    onEquipoSelected: (String) -> Unit,
    onCrearEquipoClick: () -> Unit,
    onVerVisitasClick: () -> Unit,
    onVerProtocolosClick: () -> Unit,
    onAdminClick: () -> Unit,
    onCerrarSesion: () -> Unit,
    onVerPerfilClick: () -> Unit = {}
) {
    val equipos by viewModel.equipos.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filtroClasificacion by viewModel.filtroClasificacion.collectAsState()
    val usuarioActivo by loginViewModel.usuarioActivo.collectAsState()

    val perfil = usuarioActivo?.getPerfil() ?: PerfilUsuario.TECNICO_TERRENO
    val esCliente = perfil == PerfilUsuario.CLIENTE
    val esJefeTecnico = perfil == PerfilUsuario.JEFE_TECNICO

    // Estado del drawer
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(usuarioActivo) {
        if (esCliente && !usuarioActivo?.empresaCliente.isNullOrBlank()) {
            viewModel.setEmpresaCliente(usuarioActivo?.empresaCliente)
        } else {
            viewModel.setEmpresaCliente(null)
        }
    }

    // Menú hamburguesa lateral
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color.White
            ) {
                // Header del drawer
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AzulPetroleo)
                        .padding(vertical = 24.dp, horizontal = 16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(44.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = usuarioActivo?.nombre ?: "Usuario Induztek",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "${perfil.codigo} · ${perfil.label}",
                            color = NaranjaSeguridad,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Item: Mi Perfil
                NavigationDrawerItem(
                    icon = {
                        Icon(Icons.Default.Person, contentDescription = null)
                    },
                    label = { Text("Mi Perfil") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onVerPerfilClick()
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                // Item: Equipos
                NavigationDrawerItem(
                    icon = {
                        Icon(Icons.Default.Home, contentDescription = null)
                    },
                    label = { Text("Equipos") },
                    selected = true,
                    onClick = {
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                // Item: Visitas
                NavigationDrawerItem(
                    icon = {
                        Icon(Icons.Default.Engineering, contentDescription = null)
                    },
                    label = { Text("Visitas") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onVerVisitasClick()
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                // Item: Protocolos
                NavigationDrawerItem(
                    icon = {
                        Icon(Icons.AutoMirrored.Filled.ListAlt, contentDescription = null)
                    },
                    label = { Text("Protocolos") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onVerProtocolosClick()
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                if (esJefeTecnico) {
                    NavigationDrawerItem(
                        icon = {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = null)
                        },
                        label = { Text("Administración") },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            onAdminClick()
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Item: Cerrar Sesión
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = null,
                            tint = Color(0xFFD32F2F)
                        )
                    },
                    label = {
                        Text("Cerrar Sesión", color = Color(0xFFD32F2F))
                    },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onCerrarSesion()
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    ) {
        Scaffold(
            containerColor = FondoApp,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Equipos & Diagnóstico Induztek",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menú",
                                tint = Color.White
                            )
                        }
                    },
                    actions = {
                        if (esJefeTecnico) {
                            IconButton(onClick = onAdminClick) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = "Administración",
                                    tint = NaranjaSeguridad
                                )
                            }
                        }
                        IconButton(onClick = onCerrarSesion) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Cerrar Sesión",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = AzulPetroleo
                    )
                )
            },
            floatingActionButton = {
                if (perfil.puedeRegistrarEquipos) {
                    FloatingActionButton(
                        onClick = onCrearEquipoClick,
                        containerColor = NaranjaSeguridad,
                        contentColor = Color.Black
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Nuevo Equipo en Terreno")
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                // Banner de Usuario Activo y Rol
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = AzulPetroleo)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Sesión: ${usuarioActivo?.nombre ?: "Usuario Induztek"}",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            val empresaDesc = if (!usuarioActivo?.empresaCliente.isNullOrBlank()) " · ${usuarioActivo?.empresaCliente}" else ""
                            Text(
                                text = "${perfil.codigo} · ${perfil.label}$empresaDesc",
                                color = NaranjaSeguridad,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        if (esCliente) {
                            Text(
                                text = "Solo Consulta",
                                color = Color.White,
                                fontSize = 11.sp,
                                modifier = Modifier
                                    .background(Color(0xFF37474F), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Accesos directos a Visitas y Protocolos
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onVerVisitasClick,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = AzulPetroleo),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Engineering, contentDescription = null, modifier = Modifier.height(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Visitas", fontSize = 13.sp)
                    }

                    OutlinedButton(
                        onClick = onVerProtocolosClick,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ListAlt, contentDescription = null, modifier = Modifier.height(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Protocolos", fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Buscador
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    placeholder = { Text("Buscar por código, TAG, cliente, tipo...", color = TextoSecundario) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AzulPetroleo) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedTextColor = TextoPrincipal,
                        unfocusedTextColor = TextoPrincipal,
                        focusedBorderColor = AzulPetroleo,
                        unfocusedBorderColor = BordeFondo
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Filtros Primario / Secundario (Aclaración 1)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FilterChip(
                            selected = filtroClasificacion == null,
                            onClick = { viewModel.setFiltroClasificacion(null) },
                            label = { Text("Todos") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AzulPetroleo,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                    item {
                        FilterChip(
                            selected = filtroClasificacion == ClasificacionEquipo.PRIMARIO,
                            onClick = { viewModel.setFiltroClasificacion(ClasificacionEquipo.PRIMARIO) },
                            label = { Text("Primarios (Transformador, Interruptor, Celda, Cable)") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AzulPetroleo,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                    item {
                        FilterChip(
                            selected = filtroClasificacion == ClasificacionEquipo.SECUNDARIO,
                            onClick = { viewModel.setFiltroClasificacion(ClasificacionEquipo.SECUNDARIO) },
                            label = { Text("Secundarios (Relé)") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AzulPetroleo,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (equipos.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No se encontraron equipos registrados",
                            color = TextoSecundario,
                            fontSize = 15.sp
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(equipos) { equipo ->
                            EquipoCard(
                                equipo = equipo,
                                esCliente = esCliente,
                                onClick = { onEquipoSelected(equipo.codigo) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EquipoCard(
    equipo: Equipo,
    esCliente: Boolean,
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
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = equipo.codigo,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AzulPetroleo
                    )
                    if (equipo.tagCliente.isNotBlank()) {
                        Text(
                            text = "TAG: ${equipo.tagCliente}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NaranjaSeguridad
                        )
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // RT-07: Indicador de equipo empeorando
                    if (equipo.empeorando) {
                        Row(
                            modifier = Modifier
                                .background(Color(0xFFFFEBEE), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.TrendingDown,
                                contentDescription = "Empeorando",
                                tint = Color(0xFFD32F2F),
                                modifier = Modifier.height(14.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "En Alerta",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD32F2F)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    EstadoChip(estado = equipo.ultimoEstado, esCliente = esCliente)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${equipo.tipo} · Clasificación: ${equipo.clasificacion.label}",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.DarkGray
            )

            if (equipo.cliente.isNotBlank()) {
                Text(
                    text = "🏢 ${equipo.cliente} | 📍 ${equipo.ubicacion}",
                    fontSize = 12.sp,
                    color = TextoSecundario
                )
            }

            if (equipo.marca.isNotBlank() || equipo.potenciaTension.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "⚡ ${equipo.potenciaTension} · ${equipo.marca} (${equipo.anio})",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
