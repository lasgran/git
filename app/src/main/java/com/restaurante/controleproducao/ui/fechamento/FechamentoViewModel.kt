package com.restaurante.controleproducao.ui.fechamento

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.restaurante.controleproducao.data.local.entities.FechamentoEntity
import com.restaurante.controleproducao.data.repository.FechamentoRepository
import com.restaurante.controleproducao.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FechamentoUiState(
    val lomboTexto: String = "",
    val barrigaTexto: String = "",
    val observacoes: String = "",
    val carregando: Boolean = true,
    val salvando: Boolean = false,
    val salvo: Boolean = false,
    val erro: String? = null
)

@HiltViewModel
class FechamentoViewModel @Inject constructor(
    private val repository: FechamentoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FechamentoUiState())
    val uiState: StateFlow<FechamentoUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val existente = repository.buscarPorData(DateUtils.hojeIso())
            _uiState.value = if (existente != null) {
                FechamentoUiState(
                    lomboTexto = existente.lomboRestanteKg.toString(),
                    barrigaTexto = existente.barrigaRestanteKg.toString(),
                    observacoes = existente.observacoes.orEmpty(),
                    carregando = false
                )
            } else {
                _uiState.value.copy(carregando = false)
            }
        }
    }

    fun onLomboChange(v: String) {
        _uiState.value = _uiState.value.copy(lomboTexto = v.filter { it.isDigit() || it == '.' }, erro = null)
    }

    fun onBarrigaChange(v: String) {
        _uiState.value = _uiState.value.copy(barrigaTexto = v.filter { it.isDigit() || it == '.' }, erro = null)
    }

    fun onObservacoesChange(v: String) {
        _uiState.value = _uiState.value.copy(observacoes = v)
    }

    fun finalizarDia(usuarioId: Long, nomeUsuario: String) {
        val estado = _uiState.value
        val lombo = estado.lomboTexto.toDoubleOrNull()
        val barriga = estado.barrigaTexto.toDoubleOrNull()
        if (lombo == null || barriga == null) {
            _uiState.value = estado.copy(erro = "Informe valores válidos para lombo e barriga.")
            return
        }
        viewModelScope.launch {
            _uiState.value = estado.copy(salvando = true, erro = null)
            repository.salvar(
                FechamentoEntity(
                    data = DateUtils.hojeIso(),
                    usuarioId = usuarioId,
                    nomeUsuario = nomeUsuario,
                    horario = DateUtils.horaAgora(),
                    lomboRestanteKg = lombo,
                    barrigaRestanteKg = barriga,
                    observacoes = estado.observacoes.ifBlank { null }
                )
            )
            _uiState.value = _uiState.value.copy(salvando = false, salvo = true)
        }
    }
}
