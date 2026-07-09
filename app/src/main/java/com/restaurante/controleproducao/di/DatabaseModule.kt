package com.restaurante.controleproducao.di

import android.content.Context
import androidx.room.Room
import com.restaurante.controleproducao.data.local.AppDatabase
import com.restaurante.controleproducao.data.local.dao.FechamentoDao
import com.restaurante.controleproducao.data.local.dao.ProducaoDao
import com.restaurante.controleproducao.data.local.dao.UsuarioDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DATABASE_NAME)
            .fallbackToDestructiveMigration() // aceitável em v1; substituir por Migrations reais depois
            .build()

    @Provides
    fun provideUsuarioDao(db: AppDatabase): UsuarioDao = db.usuarioDao()

    @Provides
    fun provideProducaoDao(db: AppDatabase): ProducaoDao = db.producaoDao()

    @Provides
    fun provideFechamentoDao(db: AppDatabase): FechamentoDao = db.fechamentoDao()
}
