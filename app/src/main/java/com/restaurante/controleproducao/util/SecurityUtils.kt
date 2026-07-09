package com.restaurante.controleproducao.util

import java.security.MessageDigest
import java.security.SecureRandom

/**
 * Hashing simples de senha (SHA-256 + salt aleatório) para uso 100% offline.
 * Não é um KDF lento como BCrypt/Argon2, mas evita guardar senha em texto puro,
 * que é o requisito mínimo para este app interno de restaurante.
 */
object SecurityUtils {

    fun gerarSalt(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun hashSenha(senha: String, salt: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest((salt + senha).toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun validarSenha(senha: String, salt: String, hashEsperado: String): Boolean {
        return hashSenha(senha, salt) == hashEsperado
    }
}
