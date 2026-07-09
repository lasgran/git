package com.restaurante.controleproducao.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.restaurante.controleproducao.data.preferences.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Expõe a preferência de tema (claro/escuro/sistema) para a Activity raiz. */
@HiltViewModel
class TemaViewModel @Inject constructor(
    private val prefs: UserPreferences
) : ViewModel() {

    val temaEscuro = prefs.temaEscuroFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), null
    )

    fun definirTemaEscuro(escuro: Boolean) {
        viewModelScope.launch { prefs.definirTemaEscuro(escuro) }
    }

    fun seguirSistema() {
        viewModelScope.launch { prefs.seguirTemaDoSistema() }
    }
}
