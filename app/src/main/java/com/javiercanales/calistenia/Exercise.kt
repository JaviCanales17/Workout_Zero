package com.javiercanales.calistenia

data class Exercise(
    val title: String,
    val description: String,
    val duration: Long = 0L, // En milisegundos.
)
val exercisess = listOf(
    Exercise("Sentadillas", "Ejercicio para fortalecer piernas y glúteos.", 60000),
    Exercise("Flexiones", "Ejercicio para fortalecer brazos y pecho.", 60000),
    Exercise("Abdominales", "Ejercicio para fortalecer el core.", 50000),
    Exercise("Burpees", "Ejercicio de cuerpo completo para mejorar resistencia.",40000),
    Exercise("Zancadas", "Ejercicio para fortalecer piernas y equilibrio.",55000),
    Exercise("Plancha", "Ejercicio isométrico para fortalecer el core.", 60000),
    Exercise("Sentadillas1", "xEjercicio para fortalecer piernas y glúteos.", 60000),
    Exercise("Flexiones1", "xEjercicio para fortalecer brazos y pecho.", 60000,),
    Exercise("Abdominales1", "xEjercicio para fortalecer el core.", 50000),
    Exercise("Burpees1", "xEjercicio de cuerpo completo para mejorar resistencia.",40000),
    Exercise("Zancadas1", "xEjercicio para fortalecer piernas y equilibrio.",55000),
    Exercise("Plancha1", "xEjercicio isométrico para fortalecer el core.", 60000)
)
