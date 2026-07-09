package com.restaurante.controleproducao.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.restaurante.controleproducao.domain.model.Turno

/**
 * Um lançamento de produção (manhã ou noite) de um dia específico.
 * As quantidades de cada item ficam em colunas próprias para facilitar consultas
 * e exportações — evita a complexidade de uma tabela filha para um número fixo
 * e pequeno de itens de cardápio.
 */
@Entity(tableName = "producao")
data class ProducaoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val data: String,          // formato yyyy-MM-dd
    val turno: Turno,
    val usuarioId: Long,
    val nomeUsuario: String,   // desnormalizado para facilitar relatórios/histórico
    val horario: String,       // HH:mm
    val uramakiSalmao: Int = 0,
    val uramakiShimeji: Int = 0,
    val uramakiSkin: Int = 0,
    val uramakiGrelhado: Int = 0,
    val nigiriSalmao: Int = 0,
    val nigiriSkin: Int = 0,
    val jow: Int = 0,
    val batera: Int = 0
) {
    val total: Int
        get() = uramakiSalmao + uramakiShimeji + uramakiSkin + uramakiGrelhado +
            nigiriSalmao + nigiriSkin + jow + batera
}
