package com.restaurante.controleproducao.ui.historico

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.restaurante.controleproducao.data.local.entities.ProducaoEntity
import com.restaurante.controleproducao.domain.model.ItemProducao
import com.restaurante.controleproducao.domain.model.Turno
import com.restaurante.controleproducao.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoricoDetalheScreen(
    data: String,
    onVoltar: () -> Unit,
    viewModel: HistoricoDetalheViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(data) { viewModel.carregar(data) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(DateUtils.isoParaBr(data)) },
                navigationIcon = {
                    IconButton(onClick = onVoltar) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            val manha = uiState.producoes.find { it.turno == Turno.MANHA }
            val noite = uiState.producoes.find { it.turno == Turno.NOITE }

            if (manha != null) BlocoProducao("Produção da Manhã", manha)
            if (noite != null) {
                Spacer(Modifier.height(12.dp))
                BlocoProducao("Produção da Noite", noite)
            }

            Spacer(Modifier.height(12.dp))

            uiState.fechamento?.let { fechamento ->
                ElevatedCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Fechamento", style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(8.dp))
                        Text("Lombo restante: ${fechamento.lomboRestanteKg} kg")
                        Text("Barriga restante: ${fechamento.barrigaRestanteKg} kg")
                        if (!fechamento.observacoes.isNullOrBlank()) {
                            Spacer(Modifier.height(4.dp))
                            Text("Obs: ${fechamento.observacoes}")
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Registrado por ${fechamento.nomeUsuario} às ${fechamento.horario}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            if (manha == null && noite == null && uiState.fechamento == null) {
                Text("Nenhum dado encontrado para este dia.")
            }
        }
    }
}

@Composable
private fun BlocoProducao(titulo: String, producao: ProducaoEntity) {
    ElevatedCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(titulo, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            LinhaItem(ItemProducao.URAMAKI_SALMAO.label, producao.uramakiSalmao)
            LinhaItem(ItemProducao.URAMAKI_SHIMEJI.label, producao.uramakiShimeji)
            LinhaItem(ItemProducao.URAMAKI_SKIN.label, producao.uramakiSkin)
            LinhaItem(ItemProducao.URAMAKI_GRELHADO.label, producao.uramakiGrelhado)
            LinhaItem(ItemProducao.NIGIRI_SALMAO.label, producao.nigiriSalmao)
            LinhaItem(ItemProducao.NIGIRI_SKIN.label, producao.nigiriSkin)
            LinhaItem(ItemProducao.JOW.label, producao.jow)
            LinhaItem(ItemProducao.BATERA.label, producao.batera)
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total", fontWeight = FontWeight.Bold)
                Text("${producao.total}", fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(4.dp))
            Text(
                "Registrado por ${producao.nomeUsuario} às ${producao.horario}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun LinhaItem(label: String, valor: Int) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label)
        Text("$valor")
    }
}
