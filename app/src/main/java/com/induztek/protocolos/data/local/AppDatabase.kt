// ============================================================================
// ARCHIVO : data/local/AppDatabase.kt
// CAPA    : Datos locales (Room)
// RESUMEN : Base de datos Room completa con las 7 entidades del modelo de datos:
//           - Equipos, Visitas, Protocolos, Pruebas, Actas, Instrumentos, Usuarios y Códigos.
// ============================================================================

package com.induztek.protocolos.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.induztek.protocolos.model.CodigoRecuperacion

/**
 * Base de datos principal de la aplicación utilizando Room.
 * Define las entidades del sistema y proporciona accesos DAO para operaciones locales.
 */
@Database(
    entities = [
        EquipoEntity::class,
        VisitaEntity::class,
        ProtocoloEntity::class,
        PruebaEntity::class,
        ActaEntity::class,
        InstrumentoEntity::class,
        UsuarioEntity::class,
        CodigoRecuperacion::class
    ],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    /** Retorna el DAO para la gestión de equipos. */
    abstract fun equipoDao(): EquipoDao

    /** Retorna el DAO para la gestión de visitas. */
    abstract fun visitaDao(): VisitaDao

    /** Retorna el DAO para la gestión de protocolos. */
    abstract fun protocoloDao(): ProtocoloDao

    /** Retorna el DAO para la gestión de pruebas técnicas. */
    abstract fun pruebaDao(): PruebaDao

    /** Retorna el DAO para la gestión de actas. */
    abstract fun actaDao(): ActaDao

    /** Retorna el DAO para la gestión de instrumentos. */
    abstract fun instrumentoDao(): InstrumentoDao

    /** Retorna el DAO para la gestión de usuarios y perfiles. */
    abstract fun usuarioDao(): UsuarioDao

    /** Retorna el DAO para la gestión de códigos de recuperación de contraseña. */
    abstract fun codigoRecuperacionDao(): CodigoRecuperacionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Obtiene la instancia única (Singleton) de la base de datos [AppDatabase].
         * @param context Contexto de la aplicación.
         * @return Instancia de [AppDatabase].
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "induztek_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
