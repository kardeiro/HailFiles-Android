package com.kardeiro.hailfiles.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kardeiro.hailfiles.data.model.AppIndexItem
import com.kardeiro.hailfiles.ui.theme.ExpressiveShapes
import com.kardeiro.hailfiles.ui.theme.Spacing
import com.kardeiro.hailfiles.util.Constants
import com.kardeiro.hailfiles.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppCard(
    app: AppIndexItem,
    index: Int,
    onClick: () -> Unit
) {
    val enterTransition = animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(
            durationMillis = 400,
            delayMillis = (index % 12) * 40,
            easing = FastOutSlowInEasing
        ),
        label = "cardEntry"
    )

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .alpha(enterTransition.value),
        shape = ExpressiveShapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp,
            pressedElevation = 4.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg),
            horizontalArrangement = Arrangement.spacedBy(Spacing.lg),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = Constants.DB_BASE_URL + app.icon,
                contentDescription = "Ícone do app",
                modifier = Modifier
                    .size(56.dp)
                    .clip(ExpressiveShapes.medium),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                Text(
                    text = app.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Badge(text = app.version)
                    Badge(text = app.size)
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Badge(
                        text = app.category,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = DateUtils.formatDate(app.updated),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            FilledTonalIconButton(
                onClick = onClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = "Ver detalhes",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
