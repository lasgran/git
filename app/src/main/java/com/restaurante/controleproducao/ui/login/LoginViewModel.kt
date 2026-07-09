package com.restaurante.controleproducao.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.restaurante.controleproducao.data.preferences.SessaoUsuario
import com.restaurante.controleproducao.data.preferences.UserPreferences
import com.restaurante.controleproducao.data.repository.ResultadoLogin
import com.restaurante.controleproducao.data.repository.UsuarioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val nomeUsuario: String = "",
    val senha: String = "",
    val carregando: Boolean = false,
    val erro: String? = null,
    val loginRealizado: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val usuarioRepository: UsuarioRepository,
    private val prefs: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onNomeUsuarioChange(v: String) {
        _uiState.value = _uiState.value.copy(nomeUsuario = v, erro = null)
    }

    fun onSenhaChange(v: String) {
        _uiState.value = _uiState.value.copy(senha = v, erro = null)
    }

    fun entrar() {
        val estado = _uiState.value
        if (estado.nomeUsuario.isBlank() || estado.senha.isBlank()) {
            _uiState.value = estado.copy(erro = "Preencha usuário e senha.")
            return
        }
        viewModelScope.launch {
            _uiState.value = estado.copy(carregando = true, erro = null)
            when (val resultado = usuarioRepository.login(estado.nomeUsuario, estado.senha)) {
                is ResultadoLogin.Sucesso -> {
                    prefs.salvarSessao(
                        SessaoUsuario(
                            usuarioId = resultado.usuario.id,
                            nomeUsuario = resultado.usuario.nomeUsuario,
                            nomeCompleto = resultado.usuario.nomeCompleto,
                            tipo = resultado.usuario.tipo
                        )
                    )
                    _uiState.value = _uiState.value.copy(carregando = false, loginRealizado = true)
                }
                ResultadoLogin.UsuarioOuSenhaInvalidos -> {
                    _uiState.value = _uiState.value.copy(
                        carregando = false,
                        erro = "Usuário ou senha inválidos."
                    )
                }
            }
        }
    }
}
