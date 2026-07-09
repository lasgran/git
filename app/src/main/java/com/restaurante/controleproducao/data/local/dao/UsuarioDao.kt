package com.restaurante.controleproducao.data.local.dao

import androidx.room.*
import com.restaurante.controleproducao.data.local.entities.UsuarioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    @Query("SELECT * FROM usuarios WHERE nomeUsuario = :nomeUsuario AND ativo = 1 LIMIT 1")
    suspend fun buscarPorNomeUsuario(nomeUsuario: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios ORDER BY nomeCompleto ASC")
    fun observarTodos(): Flow<List<UsuarioEntity>>

    @Query("SELECT * FROM usuarios WHERE id = :id")
    suspend fun buscarPorId(id: Long): UsuarioEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun inserir(usuario: UsuarioEntity): Long

    @Update
    suspend fun atualizar(usuario: UsuarioEntity)

    @Query("UPDATE usuarios SET ativo = 0 WHERE id = :id")
    suspend fun desativar(id: Long)

    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun contar(): Int
}
