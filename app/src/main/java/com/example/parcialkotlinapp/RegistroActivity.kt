package com.example.parcialkotlinapp

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.parcialkotlinapp.api.ApiClient
import com.example.parcialkotlinapp.api.ApiError
import com.example.parcialkotlinapp.models.CrearUsuarioRequest
import com.example.parcialkotlinapp.models.Usuario
import com.example.parcialkotlinapp.utils.FechaUtils
import com.example.parcialkotlinapp.utils.aplicarInsetsSistema
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar
import java.util.Locale

class RegistroActivity : AppCompatActivity() {

    // Fecha elegida en el DatePicker (mes de 1 a 12)
    private var anioNacimiento = 0
    private var mesNacimiento = 0
    private var diaNacimiento = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContentView(R.layout.activity_registro)

        findViewById<View>(R.id.main).aplicarInsetsSistema()

        // COMPONENTES
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbarRegistro)

        val etNombre = findViewById<EditText>(R.id.etNombre)
        val etApellido = findViewById<EditText>(R.id.etApellido)
        val etCorreo = findViewById<EditText>(R.id.etCorreoRegistro)
        val etPasswordRegistro = findViewById<EditText>(R.id.etPasswordRegistro)

        val etFechaNacimiento = findViewById<EditText>(R.id.etFechaNacimiento)
        val tvEdad = findViewById<TextView>(R.id.tvEdad)

        val etUniversidad = findViewById<EditText>(R.id.etUniversidad)
        val spSemestre = findViewById<Spinner>(R.id.spSemestre)

        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)

        val tvMensaje =
            findViewById<TextView>(R.id.tvMensajeRegistro)

        // FLECHA PARA REGRESAR
        toolbar.setNavigationOnClickListener {
            finish()
        }

        // SEMESTRES (1 a 10)
        val semestres =
            listOf("Seleccione un semestre") +
                (1..10).map { "Semestre $it" }

        val adaptador = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            semestres
        )

        adaptador.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spSemestre.adapter = adaptador

        // DATEPICKER - FECHA DE NACIMIENTO
        etFechaNacimiento.setOnClickListener {

            val hoy = Calendar.getInstance()

            val anioInicial =
                if (anioNacimiento != 0) anioNacimiento
                else hoy.get(Calendar.YEAR) - 18

            val mesInicial =
                if (mesNacimiento != 0) mesNacimiento - 1
                else hoy.get(Calendar.MONTH)

            val diaInicial =
                if (diaNacimiento != 0) diaNacimiento
                else hoy.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(
                this,
                { _, anio, mes, dia ->

                    anioNacimiento = anio
                    mesNacimiento = mes + 1
                    diaNacimiento = dia

                    etFechaNacimiento.error = null
                    etFechaNacimiento.setText(
                        String.format(
                            Locale.getDefault(),
                            "%02d/%02d/%04d",
                            dia, mes + 1, anio
                        )
                    )

                    val edad = FechaUtils.calcularEdad(anio, mes + 1, dia)

                    tvEdad.text = "Edad: $edad años"
                },
                anioInicial,
                mesInicial,
                diaInicial
            )

            datePicker.datePicker.maxDate = hoy.timeInMillis

            datePicker.show()
        }

        // BOTÓN REGISTRAR
        btnRegistrar.setOnClickListener {

            val nombre =
                etNombre.text.toString().trim()

            val apellido =
                etApellido.text.toString().trim()

            val correo =
                etCorreo.text.toString().trim()

            val password = etPasswordRegistro.text.toString().trim()

            val universidad =
                etUniversidad.text.toString().trim()

            // VALIDACIONES
            if (nombre.isBlank()) {
                etNombre.error = "Ingrese el nombre"
                return@setOnClickListener
            }

            if (apellido.isBlank()) {
                etApellido.error = "Ingrese el apellido"
                return@setOnClickListener
            }

            if (correo.isBlank()) {
                etCorreo.error = "Ingrese el correo"
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS
                    .matcher(correo)
                    .matches()) {

                etCorreo.error = "Correo no válido"
                return@setOnClickListener
            }

            if (password.isBlank()) {
                etPasswordRegistro.error = "Ingrese la contraseña"
                return@setOnClickListener
            }

            if (password.length < 6) {
                etPasswordRegistro.error = "Mínimo 6 caracteres"
                return@setOnClickListener
            }

            if (anioNacimiento == 0) {
                etFechaNacimiento.error = "Seleccione la fecha de nacimiento"
                return@setOnClickListener
            }

            val edad = FechaUtils.calcularEdad(
                anioNacimiento,
                mesNacimiento,
                diaNacimiento
            )

            if (edad < 1 || edad > 120) {
                etFechaNacimiento.error = "Fecha de nacimiento no válida"
                return@setOnClickListener
            }

            if (universidad.isBlank()) {
                etUniversidad.error = "Ingrese la universidad"
                return@setOnClickListener
            }

            if (spSemestre.selectedItemPosition == 0) {

                Toast.makeText(
                    this,
                    "Seleccione un semestre",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            // La posición 1 corresponde al semestre 1
            val semestre = spSemestre.selectedItemPosition

            // OBJETO QUE ENVIAREMOS AL BACKEND
            val nuevoUsuario = CrearUsuarioRequest(
                nombre = nombre,
                apellido = apellido,
                correo = correo,
                password = password,
                fechaNacimiento = String.format(
                    Locale.US,
                    "%04d-%02d-%02d",
                    anioNacimiento, mesNacimiento, diaNacimiento
                ),
                universidad = universidad,
                semestre = semestre
            )

            tvMensaje.text = "Registrando usuario..."

            btnRegistrar.isEnabled = false

            // LLAMADA AL BACKEND
            ApiClient.usuarioApi
                .crearUsuario(nuevoUsuario)
                .enqueue(object : Callback<Usuario> {

                    override fun onResponse(
                        call: Call<Usuario>,
                        response: Response<Usuario>
                    ) {

                        btnRegistrar.isEnabled = true

                        if (response.isSuccessful) {

                            tvMensaje.text = ""

                            mostrarRegistroExitoso()

                        } else {

                            // Ej: 400 "Ya existe un usuario con ese correo."
                            val mensaje = ApiError.obtenerMensaje(
                                response,
                                "Error del servidor: ${response.code()}"
                            )

                            tvMensaje.text = mensaje

                            mostrarError(mensaje)
                        }
                    }

                    override fun onFailure(
                        call: Call<Usuario>,
                        t: Throwable
                    ) {

                        btnRegistrar.isEnabled = true

                        tvMensaje.text =
                            "Error de conexión: ${t.message}"
                    }
                })
        }
    }

    // ACEPTAR regresa al Login
    private fun mostrarRegistroExitoso() {
        AlertDialog.Builder(this)
            .setTitle("Registro exitoso")
            .setMessage("El usuario fue creado exitosamente.")
            .setCancelable(false)
            .setPositiveButton("ACEPTAR") { _, _ ->
                finish()
            }
            .show()
    }

    private fun mostrarError(mensaje: String) {
        AlertDialog.Builder(this)
            .setTitle("No se pudo registrar")
            .setMessage(mensaje)
            .setPositiveButton("ACEPTAR", null)
            .show()
    }
}
