package com.restaurante.controleproducao.data.repository

import com.restaurante.controleproducao.data.local.dao.UsuarioDao
import com.restaurante.controleproducao.data.local.entities.UsuarioEntity
import com.restaurante.controleproducao.domain.model.TipoUsuario
import com.restaurante.controleproducao.util.SecurityUtils
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

sealed class ResultadoLogin {
    data class Sucesso(val usuario: UsuarioEntity) : ResultadoLogin()
    object UsuarioOuSenhaInvalidos : ResultadoLogin()
}

@Singleton
class UsuarioRepository @Inject constructor(private val dao: UsuarioDao) {

    fun observarUsuarios(): Flow<List<UsuarioEntity>> = dao.observarTodos()

    suspend fun login(nomeUsuario: String, senha: String): ResultadoLogin {
        val usuario = dao.buscarPorNomeUsuario(nomeUsuario.trim())
            ?: return ResultadoLogin.UsuarioOuSenhaInvalidos
        return if (SecurityUtils.validarSenha(senha, usuario.salt, usuario.senhaHash)) {
            ResultadoLogin.Sucesso(usuario)
        } else {
            ResultadoLogin.UsuarioOuSenhaInvalidos
        }
    }

    suspend fun criarUsuario(
        nomeUsuario: String,
        senha: String,
        nomeCompleto: String,
        tipo: TipoUsuario
    ): Result<Long> {
        return try {
            val existente = dao.buscarPorNomeUsuario(nomeUsuario.trim())
            if (existente != null) {
                Result.failure(IllegalStateException("Já existe um usuário com esse nome."))
            } else {
                val salt = SecurityUtils.gerarSalt()
                val hash = SecurityUtils.hashSenha(senha, salt)
                val id = dao.inserir(
                    UsuarioEntity(
                        nomeUsuario = nomeUsuario.trim(),
                        senhaHash = hash,
                        salt = salt,
                        nomeCompleto = nomeCompleto.trim(),
                        tipo = tipo
                    )
                )
                Result.success(id)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun atualizarUsuario(usuario: UsuarioEntity, novaSenha: String? = null) {
        val atualizado = if (!novaSenha.isNullOrBlank()) {
            val salt = SecurityUtils.gerarSalt()
            usuario.copy(senhaHash = SecurityUtils.hashSenha(novaSenha, salt), salt = salt)
        } else {
            usuario
        }
        dao.atualizar(atualizado)
    }

    suspend fun desativarUsuario(id: Long) = dao.desativar(id)

    suspend fun existeAlgumUsuario(): Boolean = dao.contar() > 0

    /** Cria o administrador padrão na primeira execução do app. */
    suspend fun criarAdminPadraoSeNecessario() {
        if (!existeAlgumUsuario()) {
            criarUsuario(
                nomeUsuario = "admin",
                senha = "admin123",
                nomeCompleto = "Administrador",
                tipo = TipoUsuario.ADMINISTRADOR
            )
        }
    }
}
