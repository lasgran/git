package com.restaurante.controleproducao.data.local.dao

import androidx.room.*
import com.restaurante.controleproducao.data.local.entities.FechamentoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FechamentoDao {

    @Insert
    suspend fun inserir(fechamento: FechamentoEntity): Long

    @Update
    suspend fun atualizar(fechamento: FechamentoEntity)

    @Query("SELECT * FROM fechamento WHERE data = :data LIMIT 1")
    suspend fun buscarPorData(data: String): FechamentoEntity?

    @Query("SELECT * FROM fechamento WHERE data = :data LIMIT 1")
    fun observarPorData(data: String): Flow<FechamentoEntity?>

    @Query("SELECT * FROM fechamento ORDER BY data DESC")
    fun observarTodos(): Flow<List<FechamentoEntity>>

    @Query("SELECT * FROM fechamento WHERE data BETWEEN :inicio AND :fim ORDER BY data ASC")
    suspend fun buscarPorPeriodo(inicio: String, fim: String): List<FechamentoEntity>
}
