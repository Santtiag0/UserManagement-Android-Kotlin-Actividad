package com.example.parcialkotlinapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.parcialkotlinapp.utils.FechaUtils
import com.example.parcialkotlinapp.utils.aplicarInsetsSistema
import com.google.android.material.appbar.MaterialToolbar
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.main).aplicarInsetsSistema()

        // Nombre recibido desde LoginActivity
        val nombreUsuario = intent.getStringExtra("nombreUsuario")

        // Sin usuario autenticado no se puede estar en esta pantalla
        if (nombreUsuario.isNullOrBlank()) {
            irAlLogin()
            return
        }

        // =====================================
        // SALUDO SEGÚN LA HORA DEL DISPOSITIVO
        // =====================================

        val tvSaludo = findViewById<TextView>(R.id.tvSaludo)

        val horaActual =
            Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        val saludo = FechaUtils.obtenerSaludo(horaActual)

        tvSaludo.text = "$saludo, $nombreUsuario"

        // =====================================
        // CERRAR SESIÓN (ícono superior)
        // =====================================

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbarMain)

        toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_cerrar_sesion) {
                confirmarCierreSesion()
                true
            } else {
                false
            }
        }

        // =====================================
        // BOTÓN EJERCICIO PAR O IMPAR
        // =====================================

        val btnParImpar =
            findViewById<Button>(R.id.btnParImpar)

        btnParImpar.setOnClickListener {

            val intent = Intent(
                this,
                ParImparActivity::class.java
            )

            startActivity(intent)
        }
    }

    private fun confirmarCierreSesion() {
        AlertDialog.Builder(this)
            .setTitle("Cerrar sesión")
            .setMessage("¿Está seguro de que desea cerrar sesión?")
            .setNegativeButton("CANCELAR", null)
            .setPositiveButton("SALIR") { _, _ ->
                irAlLogin()
            }
            .show()
    }

    // Limpia la pila para que Atrás no regrese a la sesión anterior
    private fun irAlLogin() {
        val intent = Intent(this, LoginActivity::class.java)

        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)

        finish()
    }
}
