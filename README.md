# User Management Android App

Aplicación móvil desarrollada en Kotlin para Android que consume una API REST desarrollada con ASP.NET Core.

## Tecnologías

- Kotlin
- Android Studio
- Retrofit
- Gson
- XML Layouts
- REST API
- ASP.NET Core Backend
- MySQL

## Funcionalidades

- Inicio de sesión
- Registro de usuarios
- Validación de formularios
- Consumo de API REST
- Navegación entre Activities
- Comunicación mediante Retrofit
- Manejo de respuestas HTTP

## Arquitectura

```text
Android App
    |
    v
Activities
    |
    v
Models
    |
    v
Retrofit
    |
    | HTTP / JSON
    v
ASP.NET Core REST API
    |
    v
MySQL
```

## Cambios realizados (actividad de modificación)

### Registro de usuario
- Se eliminaron los campos **Rol** y **Edad**.
- Se agregó **Fecha de nacimiento** con `DatePickerDialog` (no permite fechas futuras).
- La **edad se calcula** con día, mes y año (`utils/FechaUtils.calcularEdad`) y se muestra debajo de la fecha.
- Se agregaron **Universidad** y **Semestre** (Spinner con valores del 1 al 10).
- Se quitó el botón "VOLVER AL LOGIN" y se agregó una **flecha superior** (`MaterialToolbar`).
- Si el registro es exitoso se muestra un **AlertDialog**; al pulsar ACEPTAR se regresa al Login.

### Manejo de errores del backend
- `api/ApiError.kt` lee el campo `mensaje` del cuerpo de error del API.
- Ejemplo: un `HTTP 400` con `{"mensaje": "Ya existe un usuario con ese correo."}` se muestra tal cual al usuario, en lugar de "Error del servidor".
- El Login también muestra el mensaje enviado por el API en un `401`.

### Pantalla principal
- Saludo según la hora del dispositivo con el nombre del usuario autenticado (recibido del Login):

| Horario | Saludo |
|---|---|
| 05:00 – 11:59 | Buenos días, [Nombre] |
| 12:00 – 17:59 | Buenas tardes, [Nombre] |
| 18:00 – 04:59 | Buenas noches, [Nombre] |

- El botón "Cerrar sesión" se reemplazó por un **ícono de salida** en la esquina superior derecha.
- Al pulsarlo se pide confirmación: **CANCELAR** cierra el diálogo y **SALIR** regresa al Login limpiando la pila de Activities (`FLAG_ACTIVITY_CLEAR_TASK`), así el botón Atrás no vuelve a la sesión anterior.

### Par o Impar
- Se mantiene la validación del número.
- Se quitó el botón "VOLVER APPS" y se agregó la flecha superior para regresar a la pantalla principal.

### Navegación uniforme
- Todas las pantallas secundarias (Registro y Par o Impar) usan la flecha superior; no hay botones "Volver".

### Archivos nuevos
- `api/ApiError.kt`, `models/ErrorResponse.kt`
- `utils/FechaUtils.kt` (edad y saludo), `utils/InsetsUtils.kt`
- `res/menu/menu_main.xml`, `res/drawable/ic_arrow_back.xml`, `ic_logout.xml`, `ic_calendar.xml`

### Flujo
```text
Registro (Android) → POST /api/Usuarios → Service (valida y calcula edad) → MySQL
Login (Android)    → POST /api/Usuarios/login → UsuarioDto → MainActivity (saludo)
```
