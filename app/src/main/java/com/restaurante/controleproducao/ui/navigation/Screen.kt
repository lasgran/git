package com.restaurante.controleproducao.ui.navigation

/** Rotas do app. Turno e data são passados como argumentos quando necessário. */
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object ProducaoManha : Screen("producao/manha")
    object ProducaoNoite : Screen("producao/noite")
    object Fechamento : Screen("fechamento")
    object Historico : Screen("historico")
    object HistoricoDetalhe : Screen("historico/{data}") {
        fun rota(data: String) = "historico/$data"
    }
    object Dashboard : Screen("dashboard")
    object Usuarios : Screen("usuarios")
}
