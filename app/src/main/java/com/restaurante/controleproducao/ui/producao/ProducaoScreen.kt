package com.restaurante.controleproducao.ui.producao

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.restaurante.controleproducao.domain.model.Turno
import com.restaurante.controleproducao.ui.components.ConfirmDialog

private data class CampoProducao(
    val label: String,
    val valor: Int,
    val onChange: (Int) -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProducaoScreen(
    turno: Turno,
    usuarioId: Long,
    nomeUsuario: String,
    onVoltar: () -> Unit,
    onSalvo: () -> Unit,
    viewModel: ProducaoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var mostrarConfirmacao by remember { mutableStateOf(false) }

    LaunchedEffect(turno) { viewModel.inicializar(turno) }
    LaunchedEffect(uiState.salvo) { if (uiState.salvo) onSalvo() }

    val titulo = if (turno == Turno.MANHA) "Produção da Manhã" else "Produção da Noite"

    val campos = listOf(
        CampoProducao("Uramaki Salmão", uiState.uramakiSalmao) { v ->
            viewModel.atualizarCampo { it.copy(uramakiSalmao = v) }
        },
        CampoProducao("Uramaki Shimeji", uiState.uramakiShimeji) { v ->
            viewModel.atualizarCampo { it.copy(uramakiShimeji = v) }
        },
        CampoProducao("Uramaki Skin", uiState.uramakiSkin) { v ->
            viewModel.atualizarCampo { it.copy(uramakiSkin = v) }
        },
        CampoProducao("Uramaki Grelhado", uiState.uramakiGrelhado) { v ->
            viewModel.atualizarCampo { it.copy(uramakiGrelhado = v) }
        },
        CampoProducao("Nigiri Salmão", uiState.nigiriSalmao) { v ->
            viewModel.atualizarCampo { it.copy(nigiriSalmao = v) }
        },
        CampoProducao("Nigiri Skin", uiState.nigiriSkin) { v ->
            viewModel.atualizarCampo { it.copy(nigiriSkin = v) }
        },
        CampoProducao("Jow", uiState.jow) { v -> viewModel.atualizarCampo { it.copy(jow = v) } },
        CampoProducao("Batera", uiState.batera) { v -> viewModel.atualizarCampo { it.copy(batera = v) } }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(titulo) },
                navigationIcon = {
                    IconButton(onClick = onVoltar) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Column(Modifier.padding(16.dp)) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total de peças", style = MaterialTheme.typography.titleMedium)
                        Text("${uiState.total}", style = MaterialTheme.typography.titleLarge)
                    }
                    Spacer(Modifier.height(8.dp))
                    if (uiState.erro != null) {
                        Text(
                            uiState.erro!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    Button(
                        onClick = { mostrarConfirmacao = true },
                        enabled = !uiState.salvando && !uiState.carregando,
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
                            Text("Salvar Produção", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }
        }
    ) { padding ->
        if (uiState.carregando) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(campos) { campo ->
                    com.restaurante.controleproducao.ui.components.NumericField(
                        label = campo.label,
                        value = campo.valor,
                        onValueChange = campo.onChange
                    )
                }
            }
        }
    }

    if (mostrarConfirmacao) {
        ConfirmDialog(
            titulo = "Confirmar salvamento",
            mensagem = "Deseja salvar a produção de hoje com um total de ${uiState.total} peças?",
            onConfirmar = {
                mostrarConfirmacao = false
                viewModel.salvar(usuarioId, nomeUsuario)
            },
            onCancelar = { mostrarConfirmacao = false }
        )
    }
}
