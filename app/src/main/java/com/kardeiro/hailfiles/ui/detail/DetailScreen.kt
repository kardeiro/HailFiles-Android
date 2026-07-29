package com.kardeiro.hailfiles.ui.detail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.kardeiro.hailfiles.data.model.AppDetail
import com.kardeiro.hailfiles.data.model.ChangelogEntry
import com.kardeiro.hailfiles.ui.components.Badge
import com.kardeiro.hailfiles.ui.theme.ExpressiveShapes
import com.kardeiro.hailfiles.ui.theme.Spacing
import com.kardeiro.hailfiles.util.Constants
import com.kardeiro.hailfiles.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    appId: String,
    viewModel: DetailViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(appId) {
        viewModel.loadAppDetail(appId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.appDetail?.name ?: "Detalhes",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadAppDetail(appId, forceRefresh = true) }) {
                        Icon(
                            imageVector = Icons.Outlined.Refresh,
                            contentDescription = "Recarregar"
                        )
                    }
                    if (uiState.appDetail != null) {
                        val context = LocalContext.current
                        IconButton(onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, "${uiState.appDetail!!.name} - ${uiState.appDetail!!.file}")
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Compartilhar"))
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Share,
                                contentDescription = "Compartilhar"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceDim
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.surfaceDim
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.ErrorOutline,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(Spacing.lg))
                        Text(text = uiState.error!!, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
            uiState.appDetail != null -> {
                val detail = uiState.appDetail!!
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentPadding = PaddingValues(
                        start = Spacing.lg, end = Spacing.lg,
                        top = Spacing.md, bottom = Spacing.xxxl
                    ),
                    verticalArrangement = Arrangement.spacedBy(Spacing.xl)
                ) {
                    item(key = "hero") { DetailHero(detail) }
                    if (detail.requiresShizuku) {
                        item(key = "shizuku") { ShizukuBanner() }
                    }
                    item(key = "download") { DownloadSection(detail) }
                    if (detail.longDescription.isNotBlank()) {
                        item(key = "description") { DescriptionSection(detail.longDescription) }
                    }
                    if (detail.screenshots.isNotEmpty()) {
                        item(key = "screenshots") { ScreenshotsSection(detail.screenshots) }
                    }
                    item(key = "info") { InfoSection(detail) }
                    if (detail.permissions.isNotEmpty()) {
                        item(key = "permissions") { PermissionsSection(detail.permissions) }
                    }
                    if (detail.changelog.isNotEmpty()) {
                        item(key = "changelog") { ChangelogSection(detail.changelog) }
                    }
                    if (detail.tags.isNotEmpty()) {
                        item(key = "tags") { TagsSection(detail.tags) }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailHero(detail: AppDetail) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.lg),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = Constants.DB_BASE_URL + detail.icon,
            contentDescription = "Ícone do app",
            modifier = Modifier.size(80.dp).clip(ExpressiveShapes.largeIncreased),
            contentScale = ContentScale.Crop
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = detail.name,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = detail.author,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(Spacing.xs))
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                Badge(text = detail.version)
                Badge(text = detail.size)
            }
        }
    }
}

@Composable
private fun ShizukuBanner() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = ExpressiveShapes.large,
        color = MaterialTheme.colorScheme.tertiaryContainer
    ) {
        Row(
            modifier = Modifier.padding(Spacing.lg),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Security,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onTertiaryContainer
            )
            Text(
                text = "Requer Shizuku",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
    }
}

@Composable
private fun DownloadSection(detail: AppDetail) {
    val context = LocalContext.current
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        Button(
            onClick = {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(detail.file)))
            },
            modifier = Modifier.fillMaxWidth(),
            shape = ExpressiveShapes.large
        ) {
            Icon(Icons.Outlined.Download, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(Spacing.sm))
            Text("Baixar APK", style = MaterialTheme.typography.labelLarge)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            if (detail.website.isNotBlank()) {
                OutlinedButton(
                    onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(detail.website))) },
                    shape = ExpressiveShapes.large
                ) {
                    Icon(Icons.Outlined.Language, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(Spacing.xs))
                    Text("Site")
                }
            }
            if (detail.sourceCode.isNotBlank()) {
                OutlinedButton(
                    onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(detail.sourceCode))) },
                    shape = ExpressiveShapes.large
                ) {
                    Icon(Icons.Outlined.Code, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(Spacing.xs))
                    Text("Código Fonte")
                }
            }
        }
        if (detail.mirrors.isNotEmpty()) {
            Text("Mirrors", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                modifier = Modifier.horizontalScroll(rememberScrollState())
            ) {
                detail.mirrors.forEach { mirror ->
                    FilterChip(
                        selected = false,
                        onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(mirror.url))) },
                        label = { Text(mirror.label) },
                        leadingIcon = { Icon(Icons.Outlined.Link, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun DescriptionSection(description: String) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        Text("Descrição", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ScreenshotsSection(screenshots: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        Text("Screenshots", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            items(screenshots) { screenshot ->
                Box(
                    modifier = Modifier.width(200.dp).height(356.dp)
                        .clip(ExpressiveShapes.large)
                ) {
                    AsyncImage(
                        model = Constants.DB_BASE_URL + screenshot,
                        contentDescription = "Screenshot",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoSection(detail: AppDetail) {
    Card(
        shape = ExpressiveShapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Column(
            modifier = Modifier.padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            Text("Informações", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
            InfoRow("Versão", detail.version)
            InfoRow("Tamanho", detail.size)
            InfoRow("Atualizado", DateUtils.formatDate(detail.updated))
            InfoRow("Requer", detail.minAndroid)
            InfoRow("Idioma", detail.language)
            InfoRow("Autor", detail.author)
            InfoRow("Categoria", detail.category)
            if (detail.downloads > 0) InfoRow("Downloads", "${detail.downloads}")
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    if (value.isNotBlank()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
        }
    }
}

@Composable
private fun PermissionsSection(permissions: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        Text("Permissões", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            permissions.forEach { permission ->
                Surface(
                    shape = ExpressiveShapes.small,
                    color = MaterialTheme.colorScheme.surfaceContainerHigh
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.sm),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                        Text(permission, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun ChangelogSection(changelog: List<ChangelogEntry>) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        Text("Novidades", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
        changelog.forEach { entry ->
            Card(
                shape = ExpressiveShapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
            ) {
                Column(
                    modifier = Modifier.padding(Spacing.md),
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Badge(text = entry.version)
                        Text(DateUtils.formatDate(entry.date), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    entry.changes.forEach { change ->
                        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm), verticalAlignment = Alignment.Top) {
                            Text("•", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                            Text(change, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TagsSection(tags: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        Text("Tags", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            tags.forEach { tag ->
                Surface(
                    shape = ExpressiveShapes.small,
                    color = MaterialTheme.colorScheme.tertiaryContainer
                ) {
                    Text(
                        text = tag,
                        modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.sm),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
        }
    }
}
