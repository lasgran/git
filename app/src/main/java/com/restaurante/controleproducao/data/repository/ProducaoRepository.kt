package com.restaurante.controleproducao.data.repository

import com.restaurante.controleproducao.data.local.dao.ProducaoDao
import com.restaurante.controleproducao.data.local.entities.ProducaoEntity
import com.restaurante.controleproducao.domain.model.Turno
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProducaoRepository @Inject constructor(private val dao: ProducaoDao) {

    fun observarPorData(data: String): Flow<List<ProducaoEntity>> = dao.observarPorData(data)

    fun observarTodos(): Flow<List<ProducaoEntity>> = dao.observarTodos()

    fun observarDatasComRegistro(): Flow<List<String>> = dao.observarDatasComRegistro()

    fun pesquisar(data: String?, usuario: String?): Flow<List<ProducaoEntity>> =
        dao.pesquisar(data?.ifBlank { null }, usuario?.ifBlank { null })

    suspend fun buscarPorDataETurno(data: String, turno: Turno): ProducaoEntity? =
        dao.buscarPorDataETurno(data, turno)

    suspend fun buscarPorPeriodo(inicio: String, fim: String): List<ProducaoEntity> =
        dao.buscarPorPeriodo(inicio, fim)

    /** Salva um lançamento novo, ou atualiza o existente do mesmo dia/turno. */
    suspend fun salvar(producao: ProducaoEntity) {
        val existente = dao.buscarPorDataETurno(producao.data, producao.turno)
        if (existente != null) {
            dao.atualizar(producao.copy(id = existente.id))
        } else {
            dao.inserir(producao)
        }
    }

    suspend fun excluir(producao: ProducaoEntity) = dao.excluir(producao)
}
