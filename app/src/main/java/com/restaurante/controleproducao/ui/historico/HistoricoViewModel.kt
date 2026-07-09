package com.restaurante.controleproducao.ui.historico

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.restaurante.controleproducao.data.local.entities.FechamentoEntity
import com.restaurante.controleproducao.data.local.entities.ProducaoEntity
import com.restaurante.controleproducao.data.repository.FechamentoRepository
import com.restaurante.controleproducao.data.repository.ProducaoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HistoricoUiState(
    val filtroData: String = "",
    val filtroUsuario: String = "",
    val datas: List<String> = emptyList()
)

@HiltViewModel
class HistoricoViewModel @Inject constructor(
    private val producaoRepository: ProducaoRepository,
    private val fechamentoRepository: FechamentoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoricoUiState())
    val uiState: StateFlow<HistoricoUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            producaoRepository.observarDatasComRegistro().collect { datas ->
                _uiState.value = _uiState.value.copy(datas = datas)
            }
        }
    }

    fun onFiltroDataChange(v: String) {
        _uiState.value = _uiState.value.copy(filtroData = v)
    }

    fun onFiltroUsuarioChange(v: String) {
        _uiState.value = _uiState.value.copy(filtroUsuario = v)
    }
}

data class DetalheDiaUiState(
    val producoes: List<ProducaoEntity> = emptyList(),
    val fechamento: FechamentoEntity? = null
)

@HiltViewModel
class HistoricoDetalheViewModel @Inject constructor(
    private val producaoRepository: ProducaoRepository,
    private val fechamentoRepository: FechamentoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetalheDiaUiState())
    val uiState: StateFlow<DetalheDiaUiState> = _uiState.asStateFlow()

    fun carregar(data: String) {
        viewModelScope.launch {
            combine(
                producaoRepository.observarPorData(data),
                fechamentoRepository.observarPorData(data)
            ) { producoes, fechamento -> DetalheDiaUiState(producoes, fechamento) }
                .collect { _uiState.value = it }
        }
    }
}
