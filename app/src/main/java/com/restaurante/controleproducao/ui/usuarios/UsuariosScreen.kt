package com.restaurante.controleproducao.ui.usuarios

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.restaurante.controleproducao.data.local.entities.UsuarioEntity
import com.restaurante.controleproducao.domain.model.TipoUsuario
import com.restaurante.controleproducao.ui.components.ConfirmDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuariosScreen(
    onVoltar: () -> Unit,
    viewModel: UsuariosViewModel = hiltViewModel()
) {
    val usuarios by viewModel.usuarios.collectAsState()
    var mostrarFormulario by remember { mutableStateOf(false) }
    var usuarioParaDesativar by remember { mutableStateOf<UsuarioEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Usuários") },
                navigationIcon = {
                    IconButton(onClick = onVoltar) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarFormulario = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Novo usuário")
            }
        }
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(usuarios) { usuario ->
                ElevatedCard(Modifier.fillMaxWidth()) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(usuario.nomeCompleto, style = MaterialTheme.typography.titleMedium)
                            Text(
                                "@${usuario.nomeUsuario} · ${if (usuario.tipo == TipoUsuario.ADMINISTRADOR) "Administrador" else "Funcionário"}" +
                                    if (!usuario.ativo) " · Inativo" else "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (usuario.ativo) {
                            IconButton(onClick = { usuarioParaDesativar = usuario }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Desativar")
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarFormulario) {
        FormularioNovoUsuario(
            viewModel = viewModel,
            onFechar = { mostrarFormulario = false }
        )
    }

    usuarioParaDesativar?.let { usuario ->
        ConfirmDialog(
            titulo = "Desativar usuário",
            mensagem = "Deseja desativar o acesso de ${usuario.nomeCompleto}?",
            textoConfirmar = "Desativar",
            onConfirmar = {
                viewModel.desativarUsuario(usuario.id)
                usuarioParaDesativar = null
            },
            onCancelar = { usuarioParaDesativar = null }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormularioNovoUsuario(
    viewModel: UsuariosViewModel,
    onFechar: () -> Unit
) {
    val form by viewModel.formState.collectAsState()

    AlertDialog(
        onDismissRequest = onFechar,
        title = { Text("Novo usuário") },
        text = {
            Column {
                OutlinedTextField(
                    value = form.nomeCompleto,
                    onValueChange = { v -> viewModel.atualizarForm { it.copy(nomeCompleto = v) } },
                    label = { Text("Nome completo") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = form.nomeUsuario,
                    onValueChange = { v -> viewModel.atualizarForm { it.copy(nomeUsuario = v) } },
                    label = { Text("Usuário (login)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = form.senha,
                    onValueChange = { v -> viewModel.atualizarForm { it.copy(senha = v) } },
                    label = { Text("Senha") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Administrador", modifier = Modifier.weight(1f))
                    Switch(
                        checked = form.tipo == TipoUsuario.ADMINISTRADOR,
                        onCheckedChange = { marcado ->
                            viewModel.atualizarForm {
                                it.copy(tipo = if (marcado) TipoUsuario.ADMINISTRADOR else TipoUsuario.FUNCIONARIO)
                            }
                        }
                    )
                }
                if (form.erro != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(form.erro!!, color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { viewModel.salvarNovoUsuario(onSucesso = onFechar) },
                enabled = !form.salvando
            ) { Text("Salvar") }
        },
        dismissButton = {
            TextButton(onClick = onFechar) { Text("Cancelar") }
        }
    )
}
