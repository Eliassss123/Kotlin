package com.induztek.protocolos.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.induztek.protocolos.ui.theme.BordeFondo
import com.induztek.protocolos.ui.theme.FondoApp
import com.induztek.protocolos.ui.theme.InduztekBlue
import com.induztek.protocolos.ui.theme.TextoSecundario

enum class TabItem(val route: String, val label: String, val icon: ImageVector) {
    EQUIPOS("lista_equipos", "Equipos", Icons.Default.Home),
    VISITAS("lista_visitas", "Visitas", Icons.Default.Engineering),
    PROTOCOLOS("lista_protocolos", "Protocolos", Icons.AutoMirrored.Filled.ListAlt),
    ADMIN("admin", "Administración", Icons.Default.AdminPanelSettings),
    PERFIL("perfil", "Mi Perfil", Icons.Default.Person)
}

@Composable
fun InduztekBottomBar(
    currentRoute: String,
    esCliente: Boolean,
    esAdminOJefe: Boolean,
    onTabSelected: (String) -> Unit
) {
    val tabs = mutableListOf<TabItem>().apply {
        add(TabItem.EQUIPOS)
        add(TabItem.VISITAS)
        add(TabItem.PROTOCOLOS)
        if (esAdminOJefe && !esCliente) {
            add(TabItem.ADMIN)
        }
        add(TabItem.PERFIL)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(FondoApp)
            .border(width = 1.dp, color = BordeFondo)
            .padding(vertical = 8.dp, horizontal = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEach { tab ->
                val selected = currentRoute == tab.route
                val tint = if (selected) InduztekBlue else TextoSecundario
                Column(
                    modifier = Modifier
                        .clickable { onTabSelected(tab.route) }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.label,
                        tint = tint,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = tab.label,
                        fontSize = 11.sp,
                        color = tint,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}
