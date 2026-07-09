package com.restaurante.controleproducao.ui.dashboard

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.restaurante.controleproducao.data.repository.FechamentoRepository
import com.restaurante.controleproducao.data.repository.ProducaoRepository
import com.restaurante.controleproducao.util.ExcelExporter
import com.restaurante.controleproducao.util.PdfExporter
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    producaoRepository: ProducaoRepository,
    fechamentoRepository: FechamentoRepository,
    onVoltar: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var exportando by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard") },
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
            return@Scaffold
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CartaoTotal("Hoje", uiState.totalHoje, Modifier.weight(1f))
                    CartaoTotal("Manhã", uiState.totalManhaHoje, Modifier.weight(1f))
                    CartaoTotal("Noite", uiState.totalNoiteHoje, Modifier.weight(1f))
                }
            }

            item {
                ElevatedCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Produção por item hoje", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                        uiState.porItemHoje.forEach { (item, qtd) ->
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(item)
                                Text("$qtd")
                            }
                        }
                    }
                }
            }

            item {
                ElevatedCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Últimos 7 dias", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                        BarChart(dados = uiState.semanaPorDia)
                    }
                }
            }

            item {
                ElevatedCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Mês atual por semana", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                        if (uiState.mesPorSemana.isEmpty()) {
                            Text("Sem dados no mês.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        } else {
                            BarChart(dados = uiState.mesPorSemana, corBarra = MaterialTheme.colorScheme.secondary)
                        }
                        uiState.itemMaisProduzidoMes?.let {
                            Spacer(Modifier.height(8.dp))
                            Text("Item mais produzido no mês: $it", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            item {
                Text("Exportar relatórios", style = MaterialTheme.typography.titleMedium)
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                exportando = true
                                try {
                                    val producoes = producaoRepository.buscarPorPeriodo("0000-01-01", "9999-12-31")
                                    val fechamentos = fechamentoRepository.buscarPorPeriodo("0000-01-01", "9999-12-31")
                                    val uri = PdfExporter.gerarRelatorio(context, producoes, fechamentos)
                                    PdfExporter.compartilhar(context, uri)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Erro ao gerar PDF: ${e.message}", Toast.LENGTH_LONG).show()
                                } finally {
                                    exportando = false
                                }
                            }
                        },
                        enabled = !exportando,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.PictureAsPdf, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("PDF")
                    }

                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                exportando = true
                                try {
                                    val producoes = producaoRepository.buscarPorPeriodo("0000-01-01", "9999-12-31")
                                    val fechamentos = fechamentoRepository.buscarPorPeriodo("0000-01-01", "9999-12-31")
                                    val uri = ExcelExporter.gerarPlanilha(context, producoes, fechamentos)
                                    ExcelExporter.compartilhar(context, uri)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Erro ao gerar Excel: ${e.message}", Toast.LENGTH_LONG).show()
                                } finally {
                                    exportando = false
                                }
                            }
                        },
                        enabled = !exportando,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.TableChart, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Excel")
                    }
                }
            }
        }
    }
}

@Composable
private fun CartaoTotal(label: String, valor: Int, modifier: Modifier = Modifier) {
    ElevatedCard(modifier = modifier) {
        Column(
            Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("$valor", style = MaterialTheme.typography.headlineSmall)
            Text(label, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
