package com.restaurante.controleproducao.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.restaurante.controleproducao.data.repository.FechamentoRepository
import com.restaurante.controleproducao.data.repository.ProducaoRepository
import com.restaurante.controleproducao.domain.model.TipoUsuario
import com.restaurante.controleproducao.domain.model.Turno
import com.restaurante.controleproducao.ui.dashboard.DashboardScreen
import com.restaurante.controleproducao.ui.fechamento.FechamentoScreen
import com.restaurante.controleproducao.ui.historico.HistoricoDetalheScreen
import com.restaurante.controleproducao.ui.historico.HistoricoScreen
import com.restaurante.controleproducao.ui.home.HomeScreen
import com.restaurante.controleproducao.ui.login.AuthViewModel
import com.restaurante.controleproducao.ui.login.LoginScreen
import com.restaurante.controleproducao.ui.producao.ProducaoScreen
import com.restaurante.controleproducao.ui.usuarios.UsuariosScreen
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.components.SingletonComponent
import dagger.hilt.EntryPoint

/** EntryPoint para obter repositórios de dentro de um Composable (usados na exportação). */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface RepositoryEntryPoint {
    fun producaoRepository(): ProducaoRepository
    fun fechamentoRepository(): FechamentoRepository
}

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val sessao by authViewModel.sessao.collectAsState()

    val context = LocalContext.current
    val entryPoint = remember(context) {
        EntryPointAccessors.fromApplication(context.applicationContext, RepositoryEntryPoint::class.java)
    }

    NavHost(
        navController = navController,
        startDestination = if (sessao == null) Screen.Login.route else Screen.Home.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(onLoginSucesso = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            })
        }

        composable(Screen.Home.route) {
            val s = sessao
            if (s == null) {
                navController.navigate(Screen.Login.route) {
                    popUpTo(0)
                }
            } else {
                HomeScreen(
                    nomeCompleto = s.nomeCompleto,
                    tipoUsuario = s.tipo,
                    onAbrirProducaoManha = { navController.navigate(Screen.ProducaoManha.route) },
                    onAbrirProducaoNoite = { navController.navigate(Screen.ProducaoNoite.route) },
                    onAbrirFechamento = { navController.navigate(Screen.Fechamento.route) },
                    onAbrirHistorico = { navController.navigate(Screen.Historico.route) },
                    onAbrirDashboard = { navController.navigate(Screen.Dashboard.route) },
                    onAbrirUsuarios = { navController.navigate(Screen.Usuarios.route) },
                    onLogout = {
                        authViewModel.logout()
                        navController.navigate(Screen.Login.route) { popUpTo(0) }
                    }
                )
            }
        }

        composable(Screen.ProducaoManha.route) {
            val s = sessao ?: return@composable
            ProducaoScreen(
                turno = Turno.MANHA,
                usuarioId = s.usuarioId,
                nomeUsuario = s.nomeCompleto,
                onVoltar = { navController.popBackStack() },
                onSalvo = { navController.popBackStack() }
            )
        }

        composable(Screen.ProducaoNoite.route) {
            val s = sessao ?: return@composable
            ProducaoScreen(
                turno = Turno.NOITE,
                usuarioId = s.usuarioId,
                nomeUsuario = s.nomeCompleto,
                onVoltar = { navController.popBackStack() },
                onSalvo = { navController.popBackStack() }
            )
        }

        composable(Screen.Fechamento.route) {
            val s = sessao ?: return@composable
            FechamentoScreen(
                usuarioId = s.usuarioId,
                nomeUsuario = s.nomeCompleto,
                onVoltar = { navController.popBackStack() },
                onFinalizado = { navController.popBackStack() }
            )
        }

        composable(Screen.Historico.route) {
            HistoricoScreen(
                onVoltar = { navController.popBackStack() },
                onAbrirDia = { data -> navController.navigate(Screen.HistoricoDetalhe.rota(data)) }
            )
        }

        composable(
            route = Screen.HistoricoDetalhe.route,
            arguments = listOf(navArgument("data") { type = NavType.StringType })
        ) { backStackEntry ->
            val data = backStackEntry.arguments?.getString("data").orEmpty()
            HistoricoDetalheScreen(data = data, onVoltar = { navController.popBackStack() })
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(
                producaoRepository = entryPoint.producaoRepository(),
                fechamentoRepository = entryPoint.fechamentoRepository(),
                onVoltar = { navController.popBackStack() }
            )
        }

        composable(Screen.Usuarios.route) {
            val s = sessao
            if (s != null && s.tipo == TipoUsuario.ADMINISTRADOR) {
                UsuariosScreen(onVoltar = { navController.popBackStack() })
            }
        }
    }
}
