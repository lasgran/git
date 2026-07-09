package com.restaurante.controleproducao.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.restaurante.controleproducao.R
import com.restaurante.controleproducao.domain.model.TipoUsuario
import com.restaurante.controleproducao.ui.components.HomeCard

private data class ItemHome(
    val titulo: String,
    val subtitulo: String,
    val icone: androidx.compose.ui.graphics.vector.ImageVector,
    val destaque: Boolean,
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    nomeCompleto: String,
    tipoUsuario: TipoUsuario,
    onAbrirProducaoManha: () -> Unit,
    onAbrirProducaoNoite: () -> Unit,
    onAbrirFechamento: () -> Unit,
    onAbrirHistorico: () -> Unit,
    onAbrirDashboard: () -> Unit,
    onAbrirUsuarios: () -> Unit,
    onLogout: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isAdmin = tipoUsuario == TipoUsuario.ADMINISTRADOR

    val itens = buildList {
        add(
            ItemHome(
                "Produção da Manhã",
                if (uiState.manhaLancada) "Já registrada hoje ✓" else "Registrar produção",
                Icons.Filled.WbSunny,
                uiState.manhaLancada,
                onAbrirProducaoManha
            )
        )
        add(
            ItemHome(
                "Produção da Noite",
                if (uiState.noiteLancada) "Já registrada hoje ✓" else "Registrar produção",
                Icons.Filled.DarkMode,
                uiState.noiteLancada,
                onAbrirProducaoNoite
            )
        )
        add(
            ItemHome(
                "Fechamento",
                if (uiState.fechamentoFeito) "Dia já finalizado ✓" else "Finalizar o dia",
                Icons.Filled.EventAvailable,
                uiState.fechamentoFeito,
                onAbrirFechamento
            )
        )
        if (isAdmin) {
            add(ItemHome("Histórico", "Consultar dias anteriores", Icons.Filled.History, false, onAbrirHistorico))
            add(ItemHome("Dashboard", "Totais e gráficos", Icons.Filled.BarChart, false, onAbrirDashboard))
            add(ItemHome("Usuários", "Gerenciar acessos", Icons.Filled.People, false, onAbrirUsuarios))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Sair")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                Column(Modifier.padding(bottom = 8.dp)) {
                    Text("Olá, $nomeCompleto", style = MaterialTheme.typography.headlineSmall)
                    Text(
                        if (isAdmin) "Administrador" else "Funcionário",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            items(itens) { item ->
                val corContainer = if (item.destaque)
                    MaterialTheme.colorScheme.secondaryContainer
                else
                    MaterialTheme.colorScheme.primaryContainer
                val corConteudo = if (item.destaque)
                    MaterialTheme.colorScheme.onSecondaryContainer
                else
                    MaterialTheme.colorScheme.onPrimaryContainer

                HomeCard(
                    titulo = item.titulo,
                    subtitulo = item.subtitulo,
                    icone = item.icone,
                    corContainer = corContainer,
                    corConteudo = corConteudo,
                    onClick = item.onClick
                )
            }
        }
    }
}
