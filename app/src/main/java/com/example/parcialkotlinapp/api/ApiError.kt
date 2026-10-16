package com.example.parcialkotlinapp.api

import com.example.parcialkotlinapp.models.ErrorResponse
import com.google.gson.Gson
import retrofit2.Response

object ApiError {

    // Lee el "mensaje" que envía el backend en una respuesta de error
    fun obtenerMensaje(
        response: Response<*>,
        mensajePorDefecto: String
    ): String {

        return try {
            val cuerpo = response.errorBody()?.string()

            val error =
                Gson().fromJson(cuerpo, ErrorResponse::class.java)

            error?.mensaje?.takeIf { it.isNotBlank() }
                ?: mensajePorDefecto

        } catch (e: Exception) {
            mensajePorDefecto
        }
    }
}
