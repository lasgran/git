package com.restaurante.controleproducao.data.local

import androidx.room.TypeConverter
import com.restaurante.controleproducao.domain.model.TipoUsuario
import com.restaurante.controleproducao.domain.model.Turno

/** Conversores para os enums do domínio ⇄ colunas TEXT do Room. */
class Converters {

    @TypeConverter
    fun fromTurno(turno: Turno): String = turno.name

    @TypeConverter
    fun toTurno(value: String): Turno = Turno.valueOf(value)

    @TypeConverter
    fun fromTipoUsuario(tipo: TipoUsuario): String = tipo.name

    @TypeConverter
    fun toTipoUsuario(value: String): TipoUsuario = TipoUsuario.valueOf(value)
}
