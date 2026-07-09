package com.restaurante.controleproducao.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.restaurante.controleproducao.data.preferences.SessaoUsuario
import com.restaurante.controleproducao.data.preferences.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Fonte única da sessão atual, compartilhada entre todas as telas via NavGraph.
 * Como é injetado com hiltViewModel(navController's back stack entry = graph raiz),
 * mantém-se vivo durante toda a navegação pós-login.
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val prefs: UserPreferences
) : ViewModel() {

    val sessao = prefs.sessaoFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), null
    )

    fun logout() {
        viewModelScope.launch { prefs.limparSessao() }
    }
}
