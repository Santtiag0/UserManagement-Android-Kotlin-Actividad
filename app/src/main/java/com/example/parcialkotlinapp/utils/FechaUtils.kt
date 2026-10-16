package com.example.parcialkotlinapp.utils

import java.util.Calendar

object FechaUtils {

    // mes de 1 a 12; resta un año si aún no ha llegado el cumpleaños
    fun calcularEdad(
        anio: Int,
        mes: Int,
        dia: Int,
        hoy: Calendar = Calendar.getInstance()
    ): Int {

        val anioActual = hoy.get(Calendar.YEAR)
        val mesActual = hoy.get(Calendar.MONTH) + 1
        val diaActual = hoy.get(Calendar.DAY_OF_MONTH)

        var edad = anioActual - anio

        if (mesActual < mes ||
            (mesActual == mes && diaActual < dia)) {
            edad--
        }

        return edad
    }

    fun obtenerSaludo(hora: Int): String {
        return when (hora) {
            in 5..11 -> "Buenos días"
            in 12..17 -> "Buenas tardes"
            else -> "Buenas noches"
        }
    }
}
