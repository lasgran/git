package com.restaurante.controleproducao.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.restaurante.controleproducao.data.local.entities.ProducaoEntity
import com.restaurante.controleproducao.data.repository.ProducaoRepository
import com.restaurante.controleproducao.domain.model.ItemProducao
import com.restaurante.controleproducao.domain.model.Turno
import com.restaurante.controleproducao.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class DashboardUiState(
    val totalHoje: Int = 0,
    val totalManhaHoje: Int = 0,
    val totalNoiteHoje: Int = 0,
    val porItemHoje: Map<String, Int> = emptyMap(),
    val semanaPorDia: List<Pair<String, Int>> = emptyList(), // label curto -> total
    val mesPorSemana: List<Pair<String, Int>> = emptyList(),
    val itemMaisProduzidoMes: String? = null,
    val carregando: Boolean = true
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: ProducaoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        carregar()
    }

    private fun somarPorItem(lista: List<ProducaoEntity>): Map<String, Int> = mapOf(
        ItemProducao.URAMAKI_SALMAO.label to lista.sumOf { it.uramakiSalmao },
        ItemProducao.URAMAKI_SHIMEJI.label to lista.sumOf { it.uramakiShimeji },
        ItemProducao.URAMAKI_SKIN.label to lista.sumOf { it.uramakiSkin },
        ItemProducao.URAMAKI_GRELHADO.label to lista.sumOf { it.uramakiGrelhado },
        ItemProducao.NIGIRI_SALMAO.label to lista.sumOf { it.nigiriSalmao },
        ItemProducao.NIGIRI_SKIN.label to lista.sumOf { it.nigiriSkin },
        ItemProducao.JOW.label to lista.sumOf { it.jow },
        ItemProducao.BATERA.label to lista.sumOf { it.batera }
    )

    fun carregar() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(carregando = true)

            val hoje = LocalDate.now()
            val hojeIso = DateUtils.hojeIso()
            val producoesHoje = repository.buscarPorPeriodo(hojeIso, hojeIso)

            val inicioSemana = hoje.minusDays(6)
            val producoesSemana = repository.buscarPorPeriodo(
                inicioSemana.format(DateTimeFormatter.ISO_LOCAL_DATE), hojeIso
            )
            val semanaPorDia = (0..6).map { offset ->
                val dia = inicioSemana.plusDays(offset.toLong())
                val diaIso = dia.format(DateTimeFormatter.ISO_LOCAL_DATE)
                val label = dia.dayOfWeek.name.take(3)
                label to producoesSemana.filter { it.data == diaIso }.sumOf { it.total }
            }

            val inicioMes = hoje.withDayOfMonth(1)
            val producoesMes = repository.buscarPorPeriodo(
                inicioMes.format(DateTimeFormatter.ISO_LOCAL_DATE), hojeIso
            )
            val semanasNoMes = producoesMes.groupBy { producao ->
                val d = LocalDate.parse(producao.data)
                (d.dayOfMonth - 1) / 7 + 1
            }
            val mesPorSemana = semanasNoMes.entries.sortedBy { it.key }.map { (semana, lista) ->
                "Sem $semana" to lista.sumOf { it.total }
            }

            val porItemMes = somarPorItem(producoesMes)
            val itemMaisProduzido = porItemMes.maxByOrNull { it.value }?.key

            _uiState.value = DashboardUiState(
                totalHoje = producoesHoje.sumOf { it.total },
                totalManhaHoje = producoesHoje.filter { it.turno == Turno.MANHA }.sumOf { it.total },
                totalNoiteHoje = producoesHoje.filter { it.turno == Turno.NOITE }.sumOf { it.total },
                porItemHoje = somarPorItem(producoesHoje),
                semanaPorDia = semanaPorDia,
                mesPorSemana = mesPorSemana,
                itemMaisProduzidoMes = itemMaisProduzido,
                carregando = false
            )
        }
    }
}
