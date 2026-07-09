package com.restaurante.controleproducao.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.restaurante.controleproducao.data.local.entities.FechamentoEntity
import com.restaurante.controleproducao.data.local.entities.ProducaoEntity
import com.restaurante.controleproducao.data.repository.FechamentoRepository
import com.restaurante.controleproducao.data.repository.ProducaoRepository
import com.restaurante.controleproducao.domain.model.Turno
import com.restaurante.controleproducao.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class HomeUiState(
    val manhaLancada: Boolean = false,
    val noiteLancada: Boolean = false,
    val fechamentoFeito: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    producaoRepository: ProducaoRepository,
    fechamentoRepository: FechamentoRepository
) : ViewModel() {

    private val hoje = DateUtils.hojeIso()

    val uiState: StateFlow<HomeUiState> = combine(
        producaoRepository.observarPorData(hoje),
        fechamentoRepository.observarPorData(hoje)
    ) { producoes: List<ProducaoEntity>, fechamento: FechamentoEntity? ->
        HomeUiState(
            manhaLancada = producoes.any { it.turno == Turno.MANHA },
            noiteLancada = producoes.any { it.turno == Turno.NOITE },
            fechamentoFeito = fechamento != null
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())
}
