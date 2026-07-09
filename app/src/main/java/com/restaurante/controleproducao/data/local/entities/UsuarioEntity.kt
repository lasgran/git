package com.restaurante.controleproducao.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.restaurante.controleproducao.domain.model.TipoUsuario

/**
 * Usuário do sistema. A senha nunca é armazenada em texto puro: guardamos o hash
 * SHA-256 + salt (ver util/SecurityUtils.kt).
 */
@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nomeUsuario: String,
    val senhaHash: String,
    val salt: String,
    val nomeCompleto: String,
    val tipo: TipoUsuario,
    val ativo: Boolean = true
)
