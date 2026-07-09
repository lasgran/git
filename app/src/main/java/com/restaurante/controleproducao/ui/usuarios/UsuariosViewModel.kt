package com.restaurante.controleproducao.ui.usuarios

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.restaurante.controleproducao.data.local.entities.UsuarioEntity
import com.restaurante.controleproducao.data.repository.UsuarioRepository
import com.restaurante.controleproducao.domain.model.TipoUsuario
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NovoUsuarioForm(
    val nomeUsuario: String = "",
    val senha: String = "",
    val nomeCompleto: String = "",
    val tipo: TipoUsuario = TipoUsuario.FUNCIONARIO,
    val erro: String? = null,
    val salvando: Boolean = false
)

@HiltViewModel
class UsuariosViewModel @Inject constructor(
    private val repository: UsuarioRepository
) : ViewModel() {

    val usuarios = repository.observarUsuarios()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _formState = MutableStateFlow(NovoUsuarioForm())
    val formState: StateFlow<NovoUsuarioForm> = _formState

    fun atualizarForm(transform: (NovoUsuarioForm) -> NovoUsuarioForm) {
        _formState.value = transform(_formState.value).copy(erro = null)
    }

    fun salvarNovoUsuario(onSucesso: () -> Unit) {
        val f = _formState.value
        if (f.nomeUsuario.isBlank() || f.senha.isBlank() || f.nomeCompleto.isBlank()) {
            _formState.value = f.copy(erro = "Preencha todos os campos.")
            return
        }
        if (f.senha.length < 4) {
            _formState.value = f.copy(erro = "A senha deve ter ao menos 4 caracteres.")
            return
        }
        viewModelScope.launch {
            _formState.value = f.copy(salvando = true, erro = null)
            val resultado = repository.criarUsuario(f.nomeUsuario, f.senha, f.nomeCompleto, f.tipo)
            resultado.fold(
                onSuccess = {
                    _formState.value = NovoUsuarioForm()
                    onSucesso()
                },
                onFailure = { e ->
                    _formState.value = f.copy(salvando = false, erro = e.message ?: "Erro ao criar usuário.")
                }
            )
        }
    }

    fun desativarUsuario(id: Long) {
        viewModelScope.launch { repository.desativarUsuario(id) }
    }
}
