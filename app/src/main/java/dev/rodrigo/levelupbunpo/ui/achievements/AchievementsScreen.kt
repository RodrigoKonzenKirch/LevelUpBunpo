package dev.rodrigo.levelupbunpo.ui.achievements

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.rodrigo.levelupbunpo.R
import dev.rodrigo.levelupbunpo.domain.GrammarPointWithMastery

@Composable
fun AchievementsScreen(
    viewModel: AchievementsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (uiState) {
        is AchievementsUiState.Loading -> {
            LoadingScreen()
        }
        is AchievementsUiState.Success -> {
            SuccessScreen(uiState = uiState as AchievementsUiState.Success)
        }
        is AchievementsUiState.Error -> {
            ErrorScreen(message = (uiState as AchievementsUiState.Error).message)

        }
    }
}

@Composable
private fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorScreen(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun SuccessScreen(uiState: AchievementsUiState.Success) {
    LazyColumn(
        modifier = Modifier.padding(16.dp)
    ) {
        // Header for Total Mastery
        item {
            Text(text = stringResource(id = R.string.total_mastery_title), style = typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Card(elevation = CardDefaults.cardElevation(4.dp)) {
                Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                    val totalProgress = if (uiState.totalMastery.maxMastery > 0) {
                        uiState.totalMastery.currentMastery.toFloat() / uiState.totalMastery.maxMastery
                    } else {
                        0f
                    }
                    Text(text = stringResource(
                        id = R.string.mastery_progress_text,
                        uiState.totalMastery.currentMastery,
                        uiState.totalMastery.maxMastery
                    ))
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { totalProgress },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(id = R.string.grammar_points_title),
                style = typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // List of Grammar Points
        items(uiState.grammarPointsWithMastery) { item ->
            GrammarPointMasteryItem(item = item)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun GrammarPointMasteryItem(item: GrammarPointWithMastery) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.grammarPoint.grammar ?: "", style = typography.titleMedium)
                Text(
                    text = stringResource(id = R.string.mastery_progress_text, item.currentMastery, item.maxMastery),
                    style = typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            val progress = if (item.maxMastery > 0) item.currentMastery.toFloat() / item.maxMastery else 0f
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.width(100.dp)
            )
        }
    }
}
