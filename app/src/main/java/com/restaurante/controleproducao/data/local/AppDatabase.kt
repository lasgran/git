package com.restaurante.controleproducao.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.restaurante.controleproducao.data.local.dao.FechamentoDao
import com.restaurante.controleproducao.data.local.dao.ProducaoDao
import com.restaurante.controleproducao.data.local.dao.UsuarioDao
import com.restaurante.controleproducao.data.local.entities.FechamentoEntity
import com.restaurante.controleproducao.data.local.entities.ProducaoEntity
import com.restaurante.controleproducao.data.local.entities.UsuarioEntity

/**
 * Banco Room local — o app funciona 100% offline. A arquitetura fica pronta para,
 * futuramente, adicionar uma camada de sincronização (Firebase ou servidor próprio)
 * observando os mesmos Flows expostos pelos repositórios, sem precisar alterar a UI.
 */
@Database(
    entities = [UsuarioEntity::class, ProducaoEntity::class, FechamentoEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun producaoDao(): ProducaoDao
    abstract fun fechamentoDao(): FechamentoDao

    companion object {
        const val DATABASE_NAME = "controle_producao.db"
    }
}
