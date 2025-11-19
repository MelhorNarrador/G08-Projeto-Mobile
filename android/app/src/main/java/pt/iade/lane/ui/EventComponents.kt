package pt.iade.lane.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

    @Composable
    fun EventBasicInfoSection(
        titulo: String,
        onTituloChange: (String) -> Unit,
        descricao: String,
        onDescricaoChange: (String) -> Unit,
        localizacaoTexto: String,
        onLocalizacaoClick: () -> Unit
    ) {
        Column {
            Text("Título", style = MaterialTheme.typography.labelMedium)
            OutlinedTextField(
                value = titulo,
                onValueChange = onTituloChange,
                modifier = Modifier.Companion.fillMaxWidth()
            )
            Spacer(modifier = Modifier.Companion.height(16.dp))
            Text("Descrição", style = MaterialTheme.typography.labelMedium)
            OutlinedTextField(
                value = descricao,
                onValueChange = onDescricaoChange,
                modifier = Modifier.Companion.fillMaxWidth(),
                minLines = 3
            )
            Spacer(modifier = Modifier.Companion.height(16.dp))
            Text("Localização", style = MaterialTheme.typography.labelMedium)
            Box(modifier = Modifier.Companion.fillMaxWidth()) {
                OutlinedTextField(
                    value = localizacaoTexto,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Toque para pesquisar...") },
                    trailingIcon = { Icon(Icons.Default.Place, "Mapa") },
                    modifier = Modifier.Companion.fillMaxWidth()
                )
                Box(
                    modifier = Modifier.Companion
                        .matchParentSize()
                        .clickable { onLocalizacaoClick() }
                )
            }
        }
    }