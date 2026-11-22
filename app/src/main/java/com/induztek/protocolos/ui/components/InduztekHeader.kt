package com.induztek.protocolos.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.induztek.protocolos.model.UserProfile
import com.induztek.protocolos.ui.theme.InduztekBlue
import com.induztek.protocolos.ui.theme.TextoPrincipal
import com.induztek.protocolos.util.ImageStorageHelper

@Composable
fun InduztekHeader(
    usuario: UserProfile?,
    onPerfilClick: () -> Unit,
    onMenuClick: () -> Unit
) {
    val inicial = usuario?.nombre?.take(1)?.uppercase() ?: "U"
    val fotoPath = usuario?.fotoPerfil
    val bitmap = remember(fotoPath) {
        if (!fotoPath.isNullOrBlank() && !fotoPath.startsWith("http")) {
            ImageStorageHelper.cargarBitmap(fotoPath)
        } else null
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(InduztekBlue)
                .clickable { onPerfilClick() },
            contentAlignment = Alignment.Center
        ) {
            if (!fotoPath.isNullOrBlank() && fotoPath.startsWith("http")) {
                AsyncImage(
                    model = fotoPath,
                    contentDescription = "Foto de perfil",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
            } else if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Foto de perfil",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
            } else {
                Text(
                    text = inicial,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }

        IconButton(onClick = onMenuClick) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menú",
                tint = TextoPrincipal
            )
        }
    }
}
