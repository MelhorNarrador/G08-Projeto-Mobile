package pt.iade.lane.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import pt.iade.lane.data.models.Filtro

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventVisualSection(
    formState: EventFormState,
    onFormChange: (EventFormState) -> Unit,

    selectedImageUri: Uri?,
    onCoverClick: () -> Unit,

    filterOptions: List<Filtro>,
    tipoEventoExpanded: Boolean,
    onTipoEventoExpandedChange: (Boolean) -> Unit,

    privacidadeExpanded: Boolean,
    onPrivacidadeExpandedChange: (Boolean) -> Unit,
    visibilidadeOptions: List<String>,

    onLocalizacaoClick: () -> Unit,
    onDataClick: () -> Unit,
    onHoraClick: () -> Unit
) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable { onCoverClick() }
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            if (selectedImageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(selectedImageUri),
                    contentDescription = "Capa",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(40.dp)
                    )
                    Text("Adicionar Capa", color = Color.Gray)
                }
            }
        }
        Text("Título", style = MaterialTheme.typography.labelMedium)
        OutlinedTextField(
            value = formState.titulo,
            onValueChange = { onFormChange(formState.copy(titulo = it)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text("Descrição", style = MaterialTheme.typography.labelMedium)
        OutlinedTextField(
            value = formState.descricao,
            onValueChange = { onFormChange(formState.copy(descricao = it)) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text("Localização", style = MaterialTheme.typography.labelMedium)
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = formState.localizacaoTexto,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("Toque para pesquisar...") },
                trailingIcon = { Icon(Icons.Default.Place, contentDescription = "Mapa") },
                modifier = Modifier.fillMaxWidth()
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { onLocalizacaoClick() }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Categoria", style = MaterialTheme.typography.labelMedium)
        ExposedDropdownMenuBox(
            expanded = tipoEventoExpanded,
            onExpandedChange = { onTipoEventoExpandedChange(!tipoEventoExpanded) }
        ) {
            val textoDisplay = formState.selectedFiltro?.nome ?: "Selecione"

            OutlinedTextField(
                value = textoDisplay,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = tipoEventoExpanded)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
            )

            if (filterOptions.isNotEmpty()) {
                ExposedDropdownMenu(
                    expanded = tipoEventoExpanded,
                    onDismissRequest = { onTipoEventoExpandedChange(false) }
                ) {
                    filterOptions.forEach { filtro ->
                        DropdownMenuItem(
                            text = { Text(filtro.nome ?: "Sem nome") },
                            onClick = {
                                onFormChange(formState.copy(selectedFiltro = filtro))
                                onTipoEventoExpandedChange(false)
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(Modifier.weight(1f)) {
                Text("Data", style = MaterialTheme.typography.labelMedium)
                OutlinedTextField(
                    value = formState.data,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = onDataClick) {
                            Icon(Icons.Default.DateRange, contentDescription = "Data")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Column(Modifier.weight(1f)) {
                Text("Hora", style = MaterialTheme.typography.labelMedium)
                OutlinedTextField(
                    value = formState.hora,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = onHoraClick) {
                            Icon(Icons.Default.AccessTime, contentDescription = "Hora")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(Modifier.weight(1f)) {
                Text("Preço (€)", style = MaterialTheme.typography.labelMedium)
                OutlinedTextField(
                    value = formState.preco,
                    onValueChange = { onFormChange(formState.copy(preco = it)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Column(Modifier.weight(1f)) {
                Text("Participantes", style = MaterialTheme.typography.labelMedium)
                OutlinedTextField(
                    value = formState.maxParticipantes,
                    onValueChange = { onFormChange(formState.copy(maxParticipantes = it)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Privacidade", style = MaterialTheme.typography.labelMedium)
        ExposedDropdownMenuBox(
            expanded = privacidadeExpanded,
            onExpandedChange = { onPrivacidadeExpandedChange(!privacidadeExpanded) }
        ) {
            val visibilidadeLabel = when (formState.selectedVisibilidade) {
                "public" -> "Público"
                "private" -> "Privado"
                else -> "Selecione"
            }

            OutlinedTextField(
                value = visibilidadeLabel,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = privacidadeExpanded)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
            )

            ExposedDropdownMenu(
                expanded = privacidadeExpanded,
                onDismissRequest = { onPrivacidadeExpandedChange(false) }
            ) {
                DropdownMenuItem(
                    text = { Text("Público") },
                    onClick = {
                        onFormChange(formState.copy(selectedVisibilidade = "public"))
                        onPrivacidadeExpandedChange(false)
                    }
                )
                DropdownMenuItem(
                    text = { Text("Privado") },
                    onClick = {
                        onFormChange(formState.copy(selectedVisibilidade = "private"))
                        onPrivacidadeExpandedChange(false)
                    }
                )
            }
        }
    }
}


