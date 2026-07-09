package com.restaurante.controleproducao.util

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

object DateUtils {
    private val ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE
    private val BR_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    fun hojeIso(): String = LocalDate.now().format(ISO_FORMATTER)

    fun horaAgora(): String = SimpleDateFormat("HH:mm", Locale("pt", "BR")).format(java.util.Date())

    fun isoParaBr(dataIso: String): String =
        try {
            LocalDate.parse(dataIso, ISO_FORMATTER).format(BR_FORMATTER)
        } catch (e: Exception) {
            dataIso
        }

    fun isoParaDiaSemanaEData(dataIso: String): String =
        try {
            val d = LocalDate.parse(dataIso, ISO_FORMATTER)
            val diaSemana = d.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("pt", "BR"))
                .replaceFirstChar { it.uppercase() }
            "$diaSemana, ${d.format(BR_FORMATTER)}"
        } catch (e: Exception) {
            dataIso
        }
}
