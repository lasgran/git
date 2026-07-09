package com.restaurante.controleproducao.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Fechamento do dia: sobras de lombo e barriga + observações. Um por data. */
@Entity(tableName = "fechamento")
data class FechamentoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val data: String,          // yyyy-MM-dd, único por dia
    val usuarioId: Long,
    val nomeUsuario: String,
    val horario: String,
    val lomboRestanteKg: Double,
    val barrigaRestanteKg: Double,
    val observacoes: String? = null
)
