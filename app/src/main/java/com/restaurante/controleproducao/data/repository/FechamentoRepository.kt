package com.restaurante.controleproducao.data.repository

import com.restaurante.controleproducao.data.local.dao.FechamentoDao
import com.restaurante.controleproducao.data.local.entities.FechamentoEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FechamentoRepository @Inject constructor(private val dao: FechamentoDao) {

    fun observarPorData(data: String): Flow<FechamentoEntity?> = dao.observarPorData(data)

    fun observarTodos(): Flow<List<FechamentoEntity>> = dao.observarTodos()

    suspend fun buscarPorData(data: String): FechamentoEntity? = dao.buscarPorData(data)

    suspend fun buscarPorPeriodo(inicio: String, fim: String): List<FechamentoEntity> =
        dao.buscarPorPeriodo(inicio, fim)

    suspend fun salvar(fechamento: FechamentoEntity) {
        val existente = dao.buscarPorData(fechamento.data)
        if (existente != null) {
            dao.atualizar(fechamento.copy(id = existente.id))
        } else {
            dao.inserir(fechamento)
        }
    }
}
