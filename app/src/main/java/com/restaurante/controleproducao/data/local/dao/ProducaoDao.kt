package com.restaurante.controleproducao.data.local.dao

import androidx.room.*
import com.restaurante.controleproducao.data.local.entities.ProducaoEntity
import com.restaurante.controleproducao.domain.model.Turno
import kotlinx.coroutines.flow.Flow

@Dao
interface ProducaoDao {

    @Insert
    suspend fun inserir(producao: ProducaoEntity): Long

    @Update
    suspend fun atualizar(producao: ProducaoEntity)

    @Delete
    suspend fun excluir(producao: ProducaoEntity)

    @Query("SELECT * FROM producao WHERE data = :data AND turno = :turno LIMIT 1")
    suspend fun buscarPorDataETurno(data: String, turno: Turno): ProducaoEntity?

    @Query("SELECT * FROM producao WHERE data = :data ORDER BY turno ASC")
    fun observarPorData(data: String): Flow<List<ProducaoEntity>>

    @Query("SELECT * FROM producao ORDER BY data DESC, turno ASC")
    fun observarTodos(): Flow<List<ProducaoEntity>>

    @Query("""
        SELECT * FROM producao
        WHERE (:data IS NULL OR data = :data)
        AND (:usuario IS NULL OR nomeUsuario LIKE '%' || :usuario || '%')
        ORDER BY data DESC, turno ASC
    """)
    fun pesquisar(data: String?, usuario: String?): Flow<List<ProducaoEntity>>

    @Query("SELECT * FROM producao WHERE data BETWEEN :inicio AND :fim ORDER BY data ASC")
    suspend fun buscarPorPeriodo(inicio: String, fim: String): List<ProducaoEntity>

    @Query("SELECT DISTINCT data FROM producao ORDER BY data DESC")
    fun observarDatasComRegistro(): Flow<List<String>>
}
