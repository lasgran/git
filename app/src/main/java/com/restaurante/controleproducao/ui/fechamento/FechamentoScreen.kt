package com.restaurante.controleproducao.ui.fechamento

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.restaurante.controleproducao.ui.components.ConfirmDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FechamentoScreen(
    usuarioId: Long,
    nomeUsuario: String,
    onVoltar: () -> Unit,
    onFinalizado: () -> Unit,
    viewModel: FechamentoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var mostrarConfirmacao by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.salvo) { if (uiState.salvo) onFinalizado() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fechamento do Dia") },
                navigationIcon = {
                    IconButton(onClick = onVoltar) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.carregando) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = uiState.lomboTexto,
                    onValueChange = viewModel::onLomboChange,
                    label = { Text("Lombo restante (kg)") },
                    placeholder = { Text("Ex: 2.5") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = uiState.barrigaTexto,
                    onValueChange = viewModel::onBarrigaChange,
                    label = { Text("Barriga restante (kg)") },
                    placeholder = { Text("Ex: 1.3") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = uiState.observacoes,
                    onValueChange = viewModel::onObservacoesChange,
                    label = { Text("Observações (opcional)") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                if (uiState.erro != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(uiState.erro!!, color = MaterialTheme.colorScheme.error)
                }

                Spacer(Modifier.weight(1f))

                Button(
                    onClick = { mostrarConfirmacao = true },
                    enabled = !uiState.salvando,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    if (uiState.salvando) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Finalizar Dia", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }

    if (mostrarConfirmacao) {
        ConfirmDialog(
            titulo = "Finalizar o dia",
            mensagem = "Após finalizar, o dia só poderá ser editado por um administrador. Confirmar?",
            onConfirmar = {
                mostrarConfirmacao = false
                viewModel.finalizarDia(usuarioId, nomeUsuario)
            },
            onCancelar = { mostrarConfirmacao = false }
        )
    }
}
