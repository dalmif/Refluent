package io.kayt.refluent.feature.home.adddeck

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun AddDeckScreen(viewModel: AddDeckViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    AddDeckScreen(
        state = state,
        onNameChanges = { viewModel.onNameChanges(it) },
        onColor1Changes = { viewModel.onColor1Changes(it) },
        onColor2Changes = { viewModel.onColor2Changes(it) },
        onAddClick = { viewModel.addNewDeck() }
    )
}

@Composable
private fun AddDeckScreen(
    state: AddDeckUiState,
    onNameChanges: (String) -> Unit,
    onColor1Changes: (Int) -> Unit,
    onColor2Changes: (Int) -> Unit,
    onAddClick: () -> Unit
) {
    Scaffold { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            TextField(
                value = state.name,
                onValueChange = onNameChanges,
                modifier = Modifier.padding(16.dp),
                label = { Text("Deck Name") }
            )
            TextField(
                value = state.color1.toString(),
                onValueChange = { onColor1Changes(it.toIntOrNull() ?: 0) },
                modifier = Modifier.padding(16.dp),
                label = { Text("Color 1 (Hex)") }
            )
            TextField(
                value = state.color2.toString(),
                onValueChange = { onColor2Changes(it.toIntOrNull() ?: 0) },
                modifier = Modifier.padding(16.dp),
                label = { Text("Color 2 (Hex)") }
            )
            Button(
                onClick = onAddClick,
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Add Deck")
            }
        }
    }
}