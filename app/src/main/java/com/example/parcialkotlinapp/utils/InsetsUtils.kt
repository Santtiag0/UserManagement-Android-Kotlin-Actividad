package com.example.parcialkotlinapp.utils

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

// Evita que la barra superior quede debajo de la barra de estado
fun View.aplicarInsetsSistema() {
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->

        val systemBars =
            insets.getInsets(WindowInsetsCompat.Type.systemBars())

        v.setPadding(
            systemBars.left,
            systemBars.top,
            systemBars.right,
            systemBars.bottom
        )

        insets
    }
}
