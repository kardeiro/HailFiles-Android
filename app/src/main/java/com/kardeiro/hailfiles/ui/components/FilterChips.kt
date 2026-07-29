package com.kardeiro.hailfiles.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.kardeiro.hailfiles.ui.home.CategoryInfo
import com.kardeiro.hailfiles.ui.theme.ExpressiveShapes
import com.kardeiro.hailfiles.ui.theme.Spacing

@Composable
fun FilterChipsRow(
    categories: List<CategoryInfo>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        categories.forEach { category ->
            FilterChip(
                selected = category.id == selectedCategory,
                onClick = { onCategorySelected(category.id) },
                label = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(category.label)
                        Text(
                            text = "${category.count}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                leadingIcon = {
                    Icon(
                        imageVector = category.icon.toIconVector(),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                },
                shape = if (category.id == selectedCategory) ExpressiveShapes.small
                else ExpressiveShapes.extraSmall
            )
        }
    }
}

private fun String.toIconVector(): ImageVector = when (this) {
    "folder" -> Icons.Outlined.Folder
    "apps" -> Icons.Outlined.Apps
    "music_note" -> Icons.Outlined.MusicNote
    "photo_library" -> Icons.Outlined.PhotoLibrary
    else -> Icons.Outlined.Folder
}
