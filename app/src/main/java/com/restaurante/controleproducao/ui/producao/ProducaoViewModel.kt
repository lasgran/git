package com.restaurante.controleproducao.ui.producao

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.restaurante.controleproducao.data.local.entities.ProducaoEntity
import com.restaurante.controleproducao.data.repository.ProducaoRepository
import com.restaurante.controleproducao.domain.model.Turno
import com.restaurante.controleproducao.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProducaoUiState(
    val turno: Turno = Turno.MANHA,
    val uramakiSalmao: Int = 0,
    val uramakiShimeji: Int = 0,
    val uramakiSkin: Int = 0,
    val uramakiGrelhado: Int = 0,
    val nigiriSalmao: Int = 0,
    val nigiriSkin: Int = 0,
    val jow: Int = 0,
    val batera: Int = 0,
    val carregando: Boolean = true,
    val salvando: Boolean = false,
    val salvo: Boolean = false,
    val erro: String? = null
) {
    val total: Int
        get() = uramakiSalmao + uramakiShimeji + uramakiSkin + uramakiGrelhado +
            nigiriSalmao + nigiriSkin + jow + batera
}

@HiltViewModel
class ProducaoViewModel @Inject constructor(
    private val repository: ProducaoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProducaoUiState())
    val uiState: StateFlow<ProducaoUiState> = _uiState.asStateFlow()

    fun inicializar(turno: Turno) {
        _uiState.value = _uiState.value.copy(turno = turno, carregando = true)
        viewModelScope.launch {
            val existente = repository.buscarPorDataETurno(DateUtils.hojeIso(), turno)
            _uiState.value = if (existente != null) {
                _uiState.value.copy(
                    turno = turno,
                    uramakiSalmao = existente.uramakiSalmao,
                    uramakiShimeji = existente.uramakiShimeji,
                    uramakiSkin = existente.uramakiSkin,
                    uramakiGrelhado = existente.uramakiGrelhado,
                    nigiriSalmao = existente.nigiriSalmao,
                    nigiriSkin = existente.nigiriSkin,
                    jow = existente.jow,
                    batera = existente.batera,
                    carregando = false
                )
            } else {
                _uiState.value.copy(turno = turno, carregando = false)
            }
        }
    }

    fun atualizarCampo(campo: (ProducaoUiState) -> ProducaoUiState) {
        _uiState.value = campo(_uiState.value).copy(erro = null)
    }

    fun salvar(usuarioId: Long, nomeUsuario: String) {
        val estado = _uiState.value
        if (estado.total == 0) {
            _uiState.value = estado.copy(erro = "Informe ao menos um item produzido.")
            return
        }
        viewModelScope.launch {
            _uiState.value = estado.copy(salvando = true, erro = null)
            repository.salvar(
                ProducaoEntity(
                    data = DateUtils.hojeIso(),
                    turno = estado.turno,
                    usuarioId = usuarioId,
                    nomeUsuario = nomeUsuario,
                    horario = DateUtils.horaAgora(),
                    uramakiSalmao = estado.uramakiSalmao,
                    uramakiShimeji = estado.uramakiShimeji,
                    uramakiSkin = estado.uramakiSkin,
                    uramakiGrelhado = estado.uramakiGrelhado,
                    nigiriSalmao = estado.nigiriSalmao,
                    nigiriSkin = estado.nigiriSkin,
                    jow = estado.jow,
                    batera = estado.batera
                )
            )
            _uiState.value = _uiState.value.copy(salvando = false, salvo = true)
        }
    }
}
