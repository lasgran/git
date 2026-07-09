package com.restaurante.controleproducao

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.restaurante.controleproducao.ui.navigation.AppNavGraph
import com.restaurante.controleproducao.ui.theme.ControleProducaoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val temaViewModel: com.restaurante.controleproducao.ui.theme.TemaViewModel = hiltViewModel()
            val temaEscuro by temaViewModel.temaEscuro.collectAsState()

            ControleProducaoTheme(temaEscuroForcado = temaEscuro) {
                AppNavGraph()
            }
        }
    }
}
