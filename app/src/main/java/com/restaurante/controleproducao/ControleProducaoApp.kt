package com.restaurante.controleproducao

import android.app.Application
import com.restaurante.controleproducao.data.repository.UsuarioRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class ControleProducaoApp : Application() {

    @Inject lateinit var usuarioRepository: UsuarioRepository

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        // Garante que sempre exista um administrador para o primeiro acesso:
        // usuário "admin", senha "admin123" (deve ser trocada no primeiro uso).
        appScope.launch {
            usuarioRepository.criarAdminPadraoSeNecessario()
        }
    }
}
