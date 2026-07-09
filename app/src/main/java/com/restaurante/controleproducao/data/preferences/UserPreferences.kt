package com.restaurante.controleproducao.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.restaurante.controleproducao.domain.model.TipoUsuario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

data class SessaoUsuario(
    val usuarioId: Long,
    val nomeUsuario: String,
    val nomeCompleto: String,
    val tipo: TipoUsuario
)

/**
 * Guarda a sessão do usuário logado e a preferência de tema (claro/escuro/sistema)
 * usando Jetpack DataStore, para persistir entre reinícios do app sem precisar de rede.
 */
@Singleton
class UserPreferences @Inject constructor(private val context: Context) {

    private object Keys {
        val USUARIO_ID = longPreferencesKey("usuario_id")
        val NOME_USUARIO = stringPreferencesKey("nome_usuario")
        val NOME_COMPLETO = stringPreferencesKey("nome_completo")
        val TIPO = stringPreferencesKey("tipo_usuario")
        val TEMA_ESCURO = booleanPreferencesKey("tema_escuro")
        val SEGUIR_SISTEMA = booleanPreferencesKey("seguir_sistema")
    }

    val sessaoFlow: Flow<SessaoUsuario?> = context.dataStore.data.map { prefs ->
        val id = prefs[Keys.USUARIO_ID] ?: return@map null
        val tipoStr = prefs[Keys.TIPO] ?: return@map null
        SessaoUsuario(
            usuarioId = id,
            nomeUsuario = prefs[Keys.NOME_USUARIO] ?: "",
            nomeCompleto = prefs[Keys.NOME_COMPLETO] ?: "",
            tipo = TipoUsuario.valueOf(tipoStr)
        )
    }

    val temaEscuroFlow: Flow<Boolean?> = context.dataStore.data.map { prefs ->
        if (prefs[Keys.SEGUIR_SISTEMA] != false) null else prefs[Keys.TEMA_ESCURO] ?: false
    }

    suspend fun salvarSessao(sessao: SessaoUsuario) {
        context.dataStore.edit { prefs ->
            prefs[Keys.USUARIO_ID] = sessao.usuarioId
            prefs[Keys.NOME_USUARIO] = sessao.nomeUsuario
            prefs[Keys.NOME_COMPLETO] = sessao.nomeCompleto
            prefs[Keys.TIPO] = sessao.tipo.name
        }
    }

    suspend fun limparSessao() {
        context.dataStore.edit { prefs ->
            prefs.remove(Keys.USUARIO_ID)
            prefs.remove(Keys.NOME_USUARIO)
            prefs.remove(Keys.NOME_COMPLETO)
            prefs.remove(Keys.TIPO)
        }
    }

    suspend fun definirTemaEscuro(escuro: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.SEGUIR_SISTEMA] = false
            prefs[Keys.TEMA_ESCURO] = escuro
        }
    }

    suspend fun seguirTemaDoSistema() {
        context.dataStore.edit { prefs -> prefs[Keys.SEGUIR_SISTEMA] = true }
    }
}
