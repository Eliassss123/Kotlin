









package com.induztek.protocolos.ui.equipos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.LocationOn
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
import androidx.compose.material3.MaterialTheme
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
import com.induztek.protocolos.ui.components.InduztekHeader
import com.induztek.protocolos.ui.components.InduztekBottomBar
import com.induztek.protocolos.ui.navigation.Routes
import com.induztek.protocolos.ui.theme.AzulPetroleo
import com.induztek.protocolos.ui.theme.InduztekBlue
import com.induztek.protocolos.ui.theme.TarjetaFondo
import com.induztek.protocolos.ui.theme.ChipFondo
import androidx.compose.material.icons.filled.Bolt
import com.induztek.protocolos.ui.theme.InduztekBlue
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
    val remoteError by viewModel.remoteError.collectAsState()

    val perfil = usuarioActivo?.getPerfil() ?: PerfilUsuario.TECNICO_TERRENO
    val esCliente = perfil == PerfilUsuario.CLIENTE
    val esJefeTecnico = perfil == PerfilUsuario.JEFE_TECNICO


    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(usuarioActivo) {
        if (esCliente && !usuarioActivo?.empresaCliente.isNullOrBlank()) {
            viewModel.setEmpresaCliente(usuarioActivo?.empresaCliente)
        } else {
            viewModel.setEmpresaCliente(null)
        }
    }


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = FondoApp,
                drawerContentColor = TextoPrincipal
            ) {

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
            bottomBar = {
                InduztekBottomBar(
                    currentRoute = Routes.ListaEquipos.route,
                    esCliente = esCliente,
                    esAdminOJefe = esJefeTecnico,
                    onTabSelected = { route ->
                        when (route) {
                            Routes.ListaEquipos.route -> {}
                            Routes.ListaVisitas.route -> onVerVisitasClick()
                            Routes.ListaProtocolos.route -> onVerProtocolosClick()
                            Routes.Admin.route -> onAdminClick()
                            Routes.Perfil.route -> onVerPerfilClick()
                        }
                    }
                )
            },
            floatingActionButton = {
                if (perfil.puedeRegistrarEquipos) {
                    FloatingActionButton(
                        onClick = onCrearEquipoClick,
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
                            Text("Nuevo Equipo en Terreno", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp)
            ) {
                InduztekHeader(
                    usuario = usuarioActivo,
                    onPerfilClick = onVerPerfilClick,
                    onMenuClick = { scope.launch { drawerState.open() } }
                )

                Text(
                    text = "Equipos & Diagnóstico Induztek",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    lineHeight = 32.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Sesión: ${usuarioActivo?.nombre ?: "Usuario Induztek"}\n${perfil.codigo} · ${perfil.label}${if (!usuarioActivo?.empresaCliente.isNullOrBlank()) " · ${usuarioActivo?.empresaCliente}" else ""}",
                    fontSize = 13.sp,
                    color = TextoSecundario,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                remoteError?.let { message ->
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onVerVisitasClick,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AzulPetroleo,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Engineering,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Visitas",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
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
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TarjetaFondo),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(if (equipo.empeorando) Color(0xFFE5484D) else Color.Transparent)
            )

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
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(InduztekBlue.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = InduztekBlue,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                text = equipo.codigo,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextoPrincipal
                            )
                            if (equipo.tagCliente.isNotBlank()) {
                                Text(
                                    text = "TAG: ${equipo.tagCliente}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextoSecundario
                                )
                            }
                        }
                    }

                    if (equipo.empeorando) {
                        Row(
                            modifier = Modifier
                                .background(Color(0xFFE5484D).copy(alpha = 0.16f), RoundedCornerShape(999.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.TrendingDown,
                                contentDescription = null,
                                tint = Color(0xFFE5484D),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "En Alerta",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE5484D)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(ChipFondo, RoundedCornerShape(999.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = equipo.tipo,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextoPrincipal
                        )
                    }
                    Box(
                        modifier = Modifier
                            .background(ChipFondo, RoundedCornerShape(999.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Clasificación: ${equipo.clasificacion.label}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextoPrincipal
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    EstadoChip(estado = equipo.ultimoEstado, esCliente = esCliente)
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (equipo.cliente.isNotBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Business,
                            contentDescription = null,
                            tint = TextoSecundario,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = equipo.cliente,
                            fontSize = 14.sp,
                            color = TextoSecundario
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                }
                if (equipo.ubicacion.isNotBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = TextoSecundario,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = equipo.ubicacion,
                            fontSize = 14.sp,
                            color = TextoSecundario
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = BordeFondo)
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = TextoSecundario,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "${equipo.potenciaTension} · ${equipo.marca} (${equipo.anio})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextoSecundario
                    )
                }
            }
        }
    }
}
