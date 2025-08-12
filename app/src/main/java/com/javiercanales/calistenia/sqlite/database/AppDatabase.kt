package com.javiercanales.calistenia.sqlite.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.javiercanales.calistenia.sqlite.dao.EjercicioDao
import com.javiercanales.calistenia.sqlite.entities.Dificultad
import com.javiercanales.calistenia.sqlite.entities.Ejercicio
import com.javiercanales.calistenia.sqlite.entities.EjercicioMusculoCrossRef
import com.javiercanales.calistenia.sqlite.entities.EjercicioPlanesCrossRef
import com.javiercanales.calistenia.sqlite.entities.Musculo
import com.javiercanales.calistenia.sqlite.entities.Planes
import com.javiercanales.calistenia.sqlite.entities.Tipo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Ejercicio::class, Tipo::class, Dificultad::class, Musculo::class, Planes::class, EjercicioMusculoCrossRef::class, EjercicioPlanesCrossRef::class],
    version = 1,
    exportSchema = false //Se elimina un error pero se pierde el historial.
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ejercicioDao(): EjercicioDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "calistenia_database"
                )
                    .addCallback(roomCallback)
                    .build()
                INSTANCE = instance //Guardar la instancia.
                instance
            }
        }

        private val roomCallback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)


                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        prepopulateDatabase(database.ejercicioDao())
                    }
                }
            }
        }
            private suspend fun prepopulateDatabase(dao: EjercicioDao) {
                // Insertar base de datos
                val idTipoFuerza = dao.insertTipo(Tipo(nombre = "Fuerza")).toInt()
                val idTipoCardio = dao.insertTipo(Tipo(nombre = "Cardio")).toInt()
                val idTipoMovilidad = dao.insertTipo(Tipo(nombre = "Movilidad")).toInt()
                val idTipoBarras = dao.insertTipo(Tipo(nombre = "Barras")).toInt()

                val idDificultadFacil = dao.insertDificultad(Dificultad(nivel = "Fácil")).toInt()
                val idDificultadMedia = dao.insertDificultad(Dificultad(nivel = "Media")).toInt()
                val idDificultadDificil = dao.insertDificultad(Dificultad(nivel = "Difícil")).toInt()

                val idMusculoBiceps = dao.insertMusculo(Musculo(nombre = "Bíceps")).toInt()
                val idMusculoTriceps = dao.insertMusculo(Musculo(nombre = "Tríceps")).toInt()
                val idMusculoPectoral = dao.insertMusculo(Musculo(nombre = "Pectoral")).toInt()
                val idMusculoAbdomen = dao.insertMusculo(Musculo(nombre = "Abdomen")).toInt()
                val idMusculoHombros = dao.insertMusculo(Musculo(nombre = "Hombros")).toInt()
                val idMusculoEspalda = dao.insertMusculo(Musculo(nombre = "Espalda")).toInt()
                val idMusculoPiernas = dao.insertMusculo(Musculo(nombre = "Piernas")).toInt()

                val idPlanesPlan1 = dao.insertPlanes(Planes(nombre = "Calentamiento", descripcion = "Fase previa al entrenamiento que ayuda a preparar el cuerpo, aumenta la temperatura muscular y mejora la circulación, reduciendo el riesgo de lesiones y optimizando el rendimiento.", duracion = "15")).toInt()
                val idPlanesPlan2 = dao.insertPlanes(Planes(nombre = "Principiante", descripcion = "Entrenamiento suave enfocado en aprender técnicas básicas, mejorar resistencia y crear una rutina constante para comenzar de forma segura.", duracion = "30")).toInt()
                val idPlanesPlan3 = dao.insertPlanes(Planes(nombre = "Medio", descripcion = "Ejercicios de nivel intermedio que aumentan la intensidad, fortalecen los músculos y mejoran la condición física, fomentando una progresión equilibrada y una mayor resistencia y habilidades.", duracion = "30")).toInt()
                val idPlanesPlan4 = dao.insertPlanes(Planes(nombre = "Avanzado", descripcion = "Rutinas avanzadas con alta intensidad, combinando fuerza, movilidad y resistencia para potenciar al máximo tu rendimiento y desafiar tus límites.", duracion = "30")).toInt()
                val idPlanesPlan5 = dao.insertPlanes(Planes(nombre = "Tren Superior", descripcion = "Entrenamiento enfocado en fortalecer brazos, pecho, espalda y hombros, este plan mejora tu postura, fuerza y resistencia muscular para un mejor rendimiento deportivo.", duracion = "30")).toInt()
                val idPlanesPlan6 = dao.insertPlanes(Planes(nombre = "Cuerpo Completo", descripcion = "Entrenamiento con ejercicios integrados que trabajan todos los grupos musculares, aumentando fuerza, resistencia y movilidad en una rutina eficiente y equilibrada para todo el cuerpo.", duracion = "30")).toInt()
                val idPlanesPlan7 = dao.insertPlanes(Planes(nombre = "Cardio", descripcion = "Entrenamiento cardiovascular para potenciar tu resistencia, quemar calorías y mejorar tu salud cardiovascular, ideal para perder peso y aumentar tu energía diaria.", duracion = "30")).toInt()
                val idPlanesPlan8 = dao.insertPlanes(Planes(nombre = "En Barras", descripcion = "Trabaja fuerza y técnica en barras, desarrollando músculos, equilibrio y coordinación. Perfecto para mejorar la fuerza en brazos, espalda y core con ejercicios en suspensión.", duracion = "30")).toInt()

                val ejercicios = listOf(
                    Ejercicio(nombre = "Flexiones Isométrico", descripcion = "Colócate en posición de flexiones con los brazos estirados y aguanta durante un tiempo determinado.", tipoId = idTipoFuerza, dificultadId = idDificultadFacil),
                    Ejercicio(nombre = "Flexiones Esfinge", descripcion = "Colócate en posición de flexiones con los codos y las manos unidas y levanta desde ahí extendiendo los codos.", tipoId = idTipoFuerza, dificultadId = idDificultadMedia),
                    Ejercicio(nombre = "Flexiones T", descripcion = "Se realiza una flexión, al estirar los brazos se levanta uno de ellos hacia el cielo, girando el tronco.", tipoId = idTipoFuerza, dificultadId = idDificultadMedia),
                    Ejercicio(nombre = "Flexiones Arqueras", descripcion = "Colócate en posición de flexiones con un agarre mas amplio de lo normal, y en cada repetición estira un brazo a la vez que flexionas el otro, llevando el cuerpo hacia un lado.", tipoId = idTipoFuerza, dificultadId = idDificultadDificil),
                    Ejercicio(nombre = "Flexiones a una Mano", descripcion = "Abre bien las piernas para poder mantener el equilibrio, coloca un brazo en la parte trasera del muslo y realiza flexiones con el otro.", tipoId = idTipoFuerza, dificultadId = idDificultadDificil),
                    Ejercicio(nombre = "Lean Planche", descripcion = "Colócate en posición de lean planche pero con las manos el doble de abiertas de lo normal.", tipoId = idTipoFuerza, dificultadId = idDificultadMedia),
                    Ejercicio(nombre = "Flexiones con Rodilla", descripcion = "Colócate en el suelo con las manos y las rodillas apoyadas, realiza flexiones de brazo.", tipoId = idTipoFuerza, dificultadId = idDificultadFacil),
                    Ejercicio(nombre = "Flexiones", descripcion = "Coloca las manos en el suelo, con la espalda recta, flexiona los codos hasta que el pecho llegue al suelo.", tipoId = idTipoFuerza, dificultadId = idDificultadFacil),
                    Ejercicio(nombre = "Flexiones Diamante", descripcion = "Colócate en el suelo con las manos unidos formando una figura de diamante, realiza flexiones de brazo en esta posición.", tipoId = idTipoFuerza, dificultadId = idDificultadMedia),
                    Ejercicio(nombre = "Flexiones Espartanas", descripcion = "Realiza flexiones con una mano adelantada con respecto a la otra, y varia la mano para evitar descompensaciones.", tipoId = idTipoFuerza, dificultadId = idDificultadMedia),
                    Ejercicio(nombre = "Flexiones a Codos", descripcion = "Realiza flexiones pero cada vez que realizas una repetición, te dejas caer sobre tus antebrazos.", tipoId = idTipoFuerza, dificultadId = idDificultadDificil),
                    Ejercicio(nombre = "Flexiones Espartanas Explosivas", descripcion = "Realiza flexiones con un brazo adelantado con respecto al otro,en cada repetición utiliza explosividad para intercambiar la posición de las manos.", tipoId = idTipoFuerza, dificultadId = idDificultadDificil),
                    Ejercicio(nombre = "Crunch Lumbar", descripcion = "Acostado boca abajo, estira los brazos y realiza una contracción lumbar, de forma que los pies se levanten.", tipoId = idTipoFuerza, dificultadId = idDificultadFacil),
                    Ejercicio(nombre = "Flexiones Pike", descripcion = "En el suelo boca abajo, acerca las manos y los pies, levantando la cadera; realiza flexiones con la cabeza entre los brazos.", tipoId = idTipoFuerza, dificultadId = idDificultadMedia),
                    Ejercicio(nombre = "Flexiones Superman", descripcion = "Realiza flexiones explosivas, eleva tu cuerpo de forma que quedes tanto con las manos como los pies en el aire, cuerpo recto.", tipoId = idTipoFuerza, dificultadId = idDificultadDificil),
                    Ejercicio(nombre = "Sentadillas", descripcion = "Coloca las piernas con una apertura un poco mayor que la de los hombros, flexionar las rodillas para bajar y volver a subir. Con la espalda recta.", tipoId = idTipoFuerza, dificultadId = idDificultadFacil),
                    Ejercicio(nombre = "Zancadas", descripcion = "Adelantar una pierna, dejando la otra atrás y flexionar la rodilla hasta un angulo de 90. Volver a la posición inicial y cambiar de pierna.", tipoId = idTipoFuerza, dificultadId = idDificultadFacil),
                    Ejercicio(nombre = "Zancada Lateral", descripcion = "Colócate de pie y da un paso largo hacia un lado, flexionando la rodilla mientras mantienes la otra estirada.", tipoId = idTipoFuerza, dificultadId = idDificultadMedia),
                    Ejercicio(nombre = "Sentadilla Profunda", descripcion = "Realizar sentadillas pero bajando lo máximo posible, superando de largo los 90 grados.", tipoId = idTipoFuerza, dificultadId = idDificultadMedia),
                    Ejercicio(nombre = "Pistol Squat", descripcion = "De pie a la pata coja, baja con una pierna extendida, los glúteos deben llegar a rozar el suelo y no se debe levantar el talón.", tipoId = idTipoFuerza, dificultadId = idDificultadDificil),
                    Ejercicio(nombre = "Pistol Squat Explosiva", descripcion = "Realiza un Pistol-Squat, al subir hazlo de manera explosiva, dando un salto al extender la pierna.", tipoId = idTipoFuerza, dificultadId = idDificultadDificil),
                    Ejercicio(nombre = "Burpees", descripcion = "Realiza una flexión, luego lleva los pies a los lados de las manos y da un salto.", tipoId = idTipoCardio, dificultadId = idDificultadFacil),
                    Ejercicio(nombre = "Mountain Climbers", descripcion = "Desde la posición de flexiones, adelanta un pie cada vez como si intentaras correr.", tipoId = idTipoCardio, dificultadId = idDificultadFacil),
                    Ejercicio(nombre = "Doble Zancada", descripcion = "Realiza una doble zancada explosiva, y aprovecha el segundo salto para hacer una sentadilla.", tipoId = idTipoCardio, dificultadId = idDificultadMedia),
                    Ejercicio(nombre = "Tijeras en el Suelo", descripcion = "Acostado boca arriba, levanta las piernas lo justo para que los talones no toquen el suelo, muevelas repetidamente.", tipoId = idTipoCardio, dificultadId = idDificultadFacil),
                    Ejercicio(nombre = "Shoulders Taps", descripcion = "En el suelo en posición de flexiones, flexiona rápidamente un codo llevando la mano al hombro contrario y vuelve a extender y apoyar en el suelo.", tipoId = idTipoCardio, dificultadId = idDificultadFacil),
                    Ejercicio(nombre = "Rotación de Cadera", descripcion = "Colócate de pie con las manos en la cintura, realiza un amplio movimiento circular con la cadera durante un tiempo.", tipoId = idTipoMovilidad, dificultadId = idDificultadFacil),
                    Ejercicio(nombre = "Rotación de Cintura", descripcion = "De pie, gira tu tronco suavemente de izquierda a derecha manteniendo la cadera fija.", tipoId = idTipoMovilidad, dificultadId = idDificultadFacil),
                    Ejercicio(nombre = "Zancadas Amplias", descripcion = "Realiza una zancada dando un paso largo hacia delante, baja hasta la rodilla que quede atrás roce el suelo y vuelve a la posición inicial.", tipoId = idTipoMovilidad, dificultadId = idDificultadMedia),
                    Ejercicio(nombre = "Rotación de Hombros", descripcion = "Levanta tus brazos unos 45 grados, realiza movimientos circulares con ellos, no muy amplios.", tipoId = idTipoMovilidad, dificultadId = idDificultadFacil),
                    Ejercicio(nombre = "Agitar Hombros", descripcion = "Agita los brazos, haciendo que se genere movimiento en la articulación del hombro en diferentes direcciones.", tipoId = idTipoMovilidad, dificultadId = idDificultadFacil),
                    Ejercicio(nombre = "Rana", descripcion = "Colócate en cuclillas en el suelo con los brazos por dentro de los muslos, apoya las manos en el suelo y inclínate hacia delante hasta levantar los pies del suelo.", tipoId = idTipoMovilidad, dificultadId = idDificultadFacil),
                    Ejercicio(nombre = "Tucked Planche", descripcion = "Colócate agachado con las piernas encogidas y las manos en el suelo, inclínate hacia delante, levanta los pies del suelo y aguanta durante un tiempo.", tipoId = idTipoMovilidad, dificultadId = idDificultadMedia),
                    Ejercicio(nombre = "Pseudo-Planche", descripcion = "Coloca las manos en el suelo en posición supina y clava los codos en el abdomen. Intenta mantener una posición paralela al suelo con los pies levantados y las piernas juntas.", tipoId = idTipoMovilidad, dificultadId = idDificultadMedia),
                    Ejercicio(nombre = "Straddle Planche", descripcion = "Coloca las manos en el suelo a una altura mas amplia que los hombros. Con las manos apuntando hacia afuera, abre las piernas en inclínate hacia delante hasta levantar los pies del suelo.", tipoId = idTipoMovilidad, dificultadId = idDificultadDificil),
                    Ejercicio(nombre = "Full Planche", descripcion = "Con las manos apoyadas en el suelo, inclínate hacia delante manteniendo los brazos bloqueados hasta que tus pies se eleven del suelo, con las piernas juntas y estiradas.", tipoId = idTipoMovilidad, dificultadId = idDificultadDificil),
                    Ejercicio(nombre = "Jumpimg Jacks", descripcion = "Da pequeños saltos, abriendo y cerrando las piernas al mismo tiempo que levantas los brazos hacia los lados.", tipoId = idTipoCardio, dificultadId = idDificultadFacil),
                    Ejercicio(nombre = "Skaters", descripcion = "De pie a una pierna, realiza un salto lateral y cae con la pierna contraria amortiguando la caída. Repite con el otro lado.", tipoId = idTipoCardio, dificultadId = idDificultadMedia),
                    Ejercicio(nombre = "Dead Hang", descripcion = "Quédate colgando con agarre prono y aguanta en esa posición un tiempo determinado.", tipoId = idTipoBarras, dificultadId = idDificultadFacil),
                    Ejercicio(nombre = "Dominadas Supinas Cortas", descripcion = "Salta hasta colocarte con la barbilla por encima de la barra en agarre supino. Intenta bajar un poco y volver a subir, con repeticiones cortas.", tipoId = idTipoBarras, dificultadId = idDificultadFacil),
                    Ejercicio(nombre = "Dominadas Pronas Cortas", descripcion = "Salta hasta colocarte con la barbilla por encima de la barra en agarre prono. Intenta bajar un poco y volver a subir, con repeticiones cortas.", tipoId = idTipoBarras, dificultadId = idDificultadFacil),
                    Ejercicio(nombre = "Dominadas Supinas", descripcion = "En una barra de dominadas, quédate colgando con agarre supino, flexiona los brazos hasta que la barbilla supere la altura de la barra.", tipoId = idTipoBarras, dificultadId = idDificultadMedia),
                    Ejercicio(nombre = "Dominadas Pronas", descripcion = "En una barra de dominadas, quédate colgando con agarre prono, flexiona los brazos hasta que la barbilla supere la altura de la barra.", tipoId = idTipoBarras, dificultadId = idDificultadMedia),
                    Ejercicio(nombre = "Dominadas Arqueras", descripcion = "En cada repetición lleva la barbilla a una de las manos, mientras mantienes el otro brazo estirado, sin doblarlo en ningún momento.", tipoId = idTipoBarras, dificultadId = idDificultadDificil),
                    Ejercicio(nombre = "Headbangers", descripcion = "Colócate con agarre supino y la barbilla por encima de la barra. Realiza repeticiones hacia atrás intentando bajar lo menos posible.", tipoId = idTipoBarras, dificultadId = idDificultadMedia),
                    Ejercicio(nombre = "Dominadas en L", descripcion = "Manteniendo las piernas rectas en posición paralela al suelo, realiza repeticiones de dominadas supinas.", tipoId = idTipoBarras, dificultadId = idDificultadDificil),
                    Ejercicio(nombre = "Muscle-up Asistido", descripcion = "En una barra de altura media, ayúdate de un salto para subir y quedarte en posición de fondos en barra.", tipoId = idTipoBarras, dificultadId = idDificultadFacil),
                    Ejercicio(nombre = "Muscle-up", descripcion = "Colgado en barra, sube de forma explosiva, de manera que metas la cabeza por encima de la barra y quedes en posición de fondo en barra.", tipoId = idTipoBarras, dificultadId = idDificultadMedia),
                    Ejercicio(nombre = "Muscle-up Olímpico", descripcion = "Cuelga de la barra y coge un fuerte impulso con las piernas. Aprovecha ese impulso para llevar los pies ala barra y generar una palanca que te permita levantarte por encima de la misma.", tipoId = idTipoBarras, dificultadId = idDificultadMedia),
                    Ejercicio(nombre = "Muscle-up Estricto", descripcion = "Realiza un Muscle-up lo mas amplio posible, manteniendo las piernas rectas.", tipoId = idTipoBarras, dificultadId = idDificultadDificil),
                    Ejercicio(nombre = "Backlever", descripcion = "Colócate con agarre prono, pasa las piernas entre las manos e intenta quedarte paralelo al suelo.", tipoId = idTipoBarras, dificultadId = idDificultadMedia),
                )

                // Insertar ejercicios y relaciones con músculos
                for (ejercicio in ejercicios) {
                    val ejercicioId = dao.insertEjercicio(ejercicio).toInt()

                    when (ejercicio.nombre) {
                        "Flexiones Isométrico" -> {
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoTriceps
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoPectoral
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoAbdomen
                                )
                            )
                        }

                        "Flexiones Esfinge" -> {
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoTriceps
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoAbdomen
                                )
                            )
                        }

                        "Flexiones T" -> {
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoTriceps
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoPectoral
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoAbdomen
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoHombros
                                )
                            )
                        }

                        "Flexiones Arqueras" -> {
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoTriceps
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoPectoral
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoAbdomen
                                )
                            )
                        }

                        "Flexiones a una Mano" -> {
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoTriceps
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoPectoral
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoAbdomen
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoHombros
                                )
                            )
                        }

                        "Lean Planche" -> {
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoBiceps
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoTriceps
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoAbdomen
                                )
                            )
                        }

                        "Flexiones con Rodilla" -> {
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoBiceps
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoTriceps
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoPectoral
                                )
                            )
                        }

                        "Flexiones" -> {
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoBiceps
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoTriceps
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoPectoral
                                )
                            )
                        }

                        "Flexiones Diamante" -> {
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoBiceps
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoTriceps
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoPectoral
                                )
                            )
                        }

                        "Flexiones Espartanas" -> {
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoTriceps
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoPectoral
                                )
                            )
                        }

                        "Flexiones a Codos" -> {
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoTriceps
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoPectoral
                                )
                            )
                        }

                        "Flexiones Espartanas Explosivas" -> {
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoTriceps
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoPectoral
                                )
                            )
                        }

                        "Crunch Lumbar" -> {
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoHombros
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoEspalda
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoPiernas
                                )
                            )
                        }

                        "Flexiones Pike" -> {
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoBiceps
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoTriceps
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoHombros
                                )
                            )
                        }

                        "Flexiones Superman" -> {
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoTriceps
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoHombros
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoEspalda
                                )
                            )
                        }

                        "Sentadillas" -> {
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoEspalda
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoPiernas
                                )
                            )
                        }

                        "Zancadas" -> {
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoPiernas
                                )
                            )
                        }

                        "Zancada Lateral" -> {
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoPiernas
                                )
                            )
                        }

                        "Sentadilla Profunda" -> {
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoEspalda
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoPiernas
                                )
                            )
                        }

                        "Pistol Squat" -> {
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoPiernas
                                )
                            )
                        }

                        "Pistol Squat Explosiva" -> {
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoPiernas
                                )
                            )
                        }

                        "Burpees" -> {
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoTriceps
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoAbdomen
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoPiernas
                                )
                            )
                        }

                        "Mountain Climbers" -> {
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoTriceps
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoAbdomen
                                )
                            )
                        }

                        "Doble Zancada" -> {
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoAbdomen
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoPiernas
                                )
                            )
                        }

                        "Tijeras en el Suelo" -> {
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoAbdomen
                                )
                            )
                        }

                        "Shoulders Taps" -> {
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoTriceps
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoPectoral
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoAbdomen
                                )
                            )
                        }

                        "Rotación de Cadera" -> {
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoAbdomen
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoPiernas
                                )
                            )
                        }

                        "Rotación de Cintura" -> {
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoAbdomen
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoEspalda
                                )
                            )
                        }

                        "Zancadas Amplias" -> {
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoAbdomen
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoPiernas
                                )
                            )
                        }

                        "Rotación de Hombros" -> {
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoHombros
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoEspalda
                                )
                            )
                        }

                        "Agitar Hombros" -> {
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoHombros
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoEspalda
                                )
                            )
                        }

                        "Rana" -> {
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoTriceps
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoHombros
                                )
                            )
                        }

                        "Tucked Planche" -> {
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoTriceps
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoPectoral
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoHombros
                                )
                            )
                        }

                        "Pseudo-Planche" -> {
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoTriceps
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoPectoral
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoHombros
                                )
                            )
                        }

                        "Straddle Planche" -> {
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoTriceps
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoPectoral
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoHombros
                                )
                            )
                        }

                        "Full Planche" -> {
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoTriceps
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoPectoral
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoHombros
                                )
                            )
                        }

                        "Jumpimg Jacks" -> {
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoHombros
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoPiernas
                                )
                            )
                        }

                        "Skaters" -> {
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoAbdomen
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoPiernas
                                )
                            )
                        }

                        "Dead Hang" -> {
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoBiceps
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoTriceps
                                )
                            )
                        }

                        "Dominadas Supinas Cortas" -> {
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoBiceps
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoEspalda
                                )
                            )
                        }

                        "Dominadas Pronas Cortas" -> {
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoBiceps
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoEspalda
                                )
                            )
                        }

                        "Dominadas Supinas" -> {
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoBiceps
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoHombros
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoEspalda
                                )
                            )
                        }

                        "Dominadas Pronas" -> {
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoBiceps
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoHombros
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoEspalda
                                )
                            )
                        }

                        "Dominadas Arqueras" -> {
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoBiceps
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoHombros
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoEspalda
                                )
                            )
                        }

                        "Headbangers" -> {
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoBiceps
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoAbdomen
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoEspalda
                                )
                            )
                        }

                        "Dominadas en L" -> {
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoBiceps
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoAbdomen
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoEspalda
                                )
                            )
                        }

                        "Muscle-up Asistido" -> {
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoBiceps
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoTriceps
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoPectoral
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoEspalda
                                )
                            )
                        }

                        "Muscle-Up" -> {
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoBiceps
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoTriceps
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoPectoral
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoEspalda
                                )
                            )
                        }

                        "Muscle-up Olímpico" -> {
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoBiceps
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoTriceps
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoPectoral
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoAbdomen
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoEspalda
                                )
                            )
                        }

                        "Muscle-up Estricto" -> {
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoBiceps
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoTriceps
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoPectoral
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoEspalda
                                )
                            )
                        }

                        "Backlever" -> {
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoBiceps
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoTriceps
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoAbdomen
                                )
                            )
                            dao.insertEjercicioMusculoCrossRef(
                                EjercicioMusculoCrossRef(
                                    ejercicioId,
                                    idMusculoHombros
                                )
                            )
                        }

                    }
                }

                for (ejercicio in ejercicios) {
                    val ejercicioId = dao.insertEjercicio(ejercicio).toInt()

                    when (ejercicio.nombre) {
                        "Flexiones Isométrico" -> {
                            dao.insertEjercicioPlanesCrossRef(EjercicioPlanesCrossRef(ejercicioId,idPlanesPlan1))
                        }
                        "Rotación de Cadera" -> {
                            dao.insertEjercicioPlanesCrossRef(EjercicioPlanesCrossRef(ejercicioId,idPlanesPlan1))
                        }
                        "Rotación de Hombros" -> {
                            dao.insertEjercicioPlanesCrossRef(EjercicioPlanesCrossRef(ejercicioId,idPlanesPlan1))
                        }
                        "Rotación de Cintura" -> {
                            dao.insertEjercicioPlanesCrossRef(EjercicioPlanesCrossRef(ejercicioId,idPlanesPlan1))
                        }
                        "Flexiones" -> {
                            dao.insertEjercicioPlanesCrossRef(EjercicioPlanesCrossRef(ejercicioId,idPlanesPlan2))
                        }
                        "Crunch Lumbar" -> {
                            dao.insertEjercicioPlanesCrossRef(EjercicioPlanesCrossRef(ejercicioId,idPlanesPlan2))
                        }
                        "Sentadillas" -> {
                            dao.insertEjercicioPlanesCrossRef(EjercicioPlanesCrossRef(ejercicioId,idPlanesPlan2))
                        }
                        "Burpees" -> {
                            dao.insertEjercicioPlanesCrossRef(EjercicioPlanesCrossRef(ejercicioId,idPlanesPlan2))
                            dao.insertEjercicioPlanesCrossRef(EjercicioPlanesCrossRef(ejercicioId,idPlanesPlan5))
                            dao.insertEjercicioPlanesCrossRef(EjercicioPlanesCrossRef(ejercicioId,idPlanesPlan7))
                        }
                        "Zancadas" -> {
                            dao.insertEjercicioPlanesCrossRef(EjercicioPlanesCrossRef(ejercicioId,idPlanesPlan2))
                        }
                        "Flexiones T" -> {
                            dao.insertEjercicioPlanesCrossRef(EjercicioPlanesCrossRef(ejercicioId,idPlanesPlan3))
                            dao.insertEjercicioPlanesCrossRef(EjercicioPlanesCrossRef(ejercicioId,idPlanesPlan5))
                        }
                        "Lean Planche" -> {
                            dao.insertEjercicioPlanesCrossRef(EjercicioPlanesCrossRef(ejercicioId,idPlanesPlan3))
                        }
                        "Sentadilla Profunda" -> {
                            dao.insertEjercicioPlanesCrossRef(EjercicioPlanesCrossRef(ejercicioId,idPlanesPlan3))
                        }
                        "Tucked Planche" -> {
                            dao.insertEjercicioPlanesCrossRef(EjercicioPlanesCrossRef(ejercicioId,idPlanesPlan3))
                            dao.insertEjercicioPlanesCrossRef(EjercicioPlanesCrossRef(ejercicioId,idPlanesPlan6))
                        }
                        "Headbangers" -> {
                            dao.insertEjercicioPlanesCrossRef(EjercicioPlanesCrossRef(ejercicioId,idPlanesPlan3))
                            dao.insertEjercicioPlanesCrossRef(EjercicioPlanesCrossRef(ejercicioId,idPlanesPlan6))
                            dao.insertEjercicioPlanesCrossRef(EjercicioPlanesCrossRef(ejercicioId,idPlanesPlan8))
                        }
                        "Flexiones Arqueras" -> {
                            dao.insertEjercicioPlanesCrossRef(EjercicioPlanesCrossRef(ejercicioId,idPlanesPlan4))
                        }
                        "Pistol Squat" -> {
                            dao.insertEjercicioPlanesCrossRef(EjercicioPlanesCrossRef(ejercicioId,idPlanesPlan4))
                        }
                        "Muscle-up Estricto" -> {
                            dao.insertEjercicioPlanesCrossRef(EjercicioPlanesCrossRef(ejercicioId,idPlanesPlan4))
                        }
                        "Full Planche" -> {
                            dao.insertEjercicioPlanesCrossRef(EjercicioPlanesCrossRef(ejercicioId,idPlanesPlan4))
                        }
                        "Dominadas Arqueras" -> {
                            dao.insertEjercicioPlanesCrossRef(EjercicioPlanesCrossRef(ejercicioId,idPlanesPlan4))
                        }
                        "Shoulders Taps" -> {
                            dao.insertEjercicioPlanesCrossRef(EjercicioPlanesCrossRef(ejercicioId,idPlanesPlan5))
                            dao.insertEjercicioPlanesCrossRef(EjercicioPlanesCrossRef(ejercicioId,idPlanesPlan7))
                        }
                        "Muscle-up" -> {
                            dao.insertEjercicioPlanesCrossRef(EjercicioPlanesCrossRef(ejercicioId,idPlanesPlan5))
                            dao.insertEjercicioPlanesCrossRef(EjercicioPlanesCrossRef(ejercicioId,idPlanesPlan8))
                        }
                        "Flexiones Pike" -> {
                            dao.insertEjercicioPlanesCrossRef(EjercicioPlanesCrossRef(ejercicioId,idPlanesPlan5))
                            dao.insertEjercicioPlanesCrossRef(EjercicioPlanesCrossRef(ejercicioId,idPlanesPlan6))
                        }
                        "Flexiones Diamante" -> {
                            dao.insertEjercicioPlanesCrossRef(EjercicioPlanesCrossRef(ejercicioId,idPlanesPlan6))
                        }
                        "Muscle-up Asistido" -> {
                            dao.insertEjercicioPlanesCrossRef(EjercicioPlanesCrossRef(ejercicioId,idPlanesPlan6))
                        }
                        "Tijeras en el Suelo" -> {
                            dao.insertEjercicioPlanesCrossRef(EjercicioPlanesCrossRef(ejercicioId,idPlanesPlan7))
                        }
                        "Doble Zancada" -> {
                            dao.insertEjercicioPlanesCrossRef(EjercicioPlanesCrossRef(ejercicioId,idPlanesPlan7))
                        }
                        "Jumpimg Jacks" -> {
                            dao.insertEjercicioPlanesCrossRef(EjercicioPlanesCrossRef(ejercicioId,idPlanesPlan7))
                        }
                        "Dead Hang" -> {
                            dao.insertEjercicioPlanesCrossRef(EjercicioPlanesCrossRef(ejercicioId,idPlanesPlan8))
                        }
                        "Dominadas Supinas Cortas" -> {
                            dao.insertEjercicioPlanesCrossRef(EjercicioPlanesCrossRef(ejercicioId,idPlanesPlan8))
                        }
                        "Dominadas Pronas Cortas" -> {
                            dao.insertEjercicioPlanesCrossRef(EjercicioPlanesCrossRef(ejercicioId,idPlanesPlan8))
                        }

                    }
                }
            }

    }
}
