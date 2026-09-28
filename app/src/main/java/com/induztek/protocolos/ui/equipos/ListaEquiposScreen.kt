// ============================================================================
// ARCHIVO : ui/equipos/ListaEquiposScreen.kt
// CAPA    : View (pantalla)
// RESUMEN : pantalla principal tras el login: buscador + lista de equipos + 2 botones flotantes (ver protocolos /
//           nueva prueba).
// ============================================================================

package com.induztek.protocolos.ui.equipos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import com.induztek.protocolos.model.Equipo
import com.induztek.protocolos.ui.components.EstadoChip
import com.induztek.protocolos.ui.components.InduztekTopBar
import com.induztek.protocolos.ui.theme.AzulPetroleo
import com.induztek.protocolos.ui.theme.NaranjaSeguridad
import com.induztek.protocolos.ui.theme.TextoSecundario
import com.induztek.protocolos.viewmodel.EquipoViewModel

// Pantalla principal. Las lambdas del final avisan a NavGraph (ver equipo, nueva prueba, ver protocolos).
@Composable
fun ListaEquiposScreen(
    viewModel: EquipoViewModel,
    onEquipoSelected: (String) -> Unit,
    onNuevaPruebaClick: () -> Unit,
    onVerProtocolosClick: () -> Unit
) {
    // La lista ya viene FILTRADA por el ViewModel (según lo escrito en el buscador). La pantalla solo la dibuja.
    val equipos by viewModel.equipos.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    // Scaffold = 'esqueleto' de pantalla de Material: define zonas estándar (barra superior, botón flotante,
    //   contenido).
    // El contenido va en la lambda final; 'innerPadding' es el espacio que ocupan las barras, para que nada quede
    //   tapado.
    Scaffold(
        topBar = {
            InduztekTopBar(titulo = "Equipos Eléctricos")
        },
        // Botones flotantes (FAB) redondos en la esquina inferior derecha.
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                // Botón redondo flotante. Este abre la lista de protocolos guardados.
                FloatingActionButton(
                    onClick = onVerProtocolosClick,
                    containerColor = AzulPetroleo,
                    contentColor = Color.White,
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Icon(Icons.Default.ListAlt, contentDescription = "Ver Protocolos")
                }

                FloatingActionButton(
                    onClick = onNuevaPruebaClick,
                    containerColor = NaranjaSeguridad,
                    contentColor = Color.Black
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Nueva Prueba")
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
            // Buscador: cada letra llama a viewModel.onSearchQueryChanged y la lista se filtra sola.
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                placeholder = { Text("Buscar equipo por código, tipo o ubicación...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Si no hay resultados muestra un mensaje; si hay, muestra la lista.
            if (equipos.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No se encontraron equipos",
                        color = TextoSecundario,
                        fontSize = 16.sp
                    )
                }
            } else {
                // LazyColumn = lista con scroll que solo dibuja los elementos visibles (como RecyclerView / lista
                //   virtualizada).
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Repite EquipoCard por cada equipo de la lista (como un v-for / .map()).
                    items(equipos) { equipo ->
                        EquipoCard(
                            equipo = equipo,
                            onClick = { onEquipoSelected(equipo.codigo) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
// Componente de UNA tarjeta de equipo. Recibe el equipo y qué hacer al tocarla.
fun EquipoCard(
    equipo: Equipo,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            // Hace tocable toda la tarjeta.
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = equipo.codigo,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AzulPetroleo
                )
                // Reutiliza el componente de la 'píldora' de estado (ui/components/EstadoChip.kt).
                EstadoChip(estado = equipo.ultimoEstado)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = equipo.tipo,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                // '${...}' inserta el valor de una variable dentro del texto (string template).
                text = "📍 ${equipo.ubicacion}",
                fontSize = 13.sp,
                color = TextoSecundario
            )
        }
    }
}
