// ============================================================================
// ARCHIVO : data/local/AppDatabase.kt
// CAPA    : Datos locales (Room)
// RESUMEN : la base de datos SQLite de la app. Aquí se declaran las tablas (entities) y los DAOs disponibles.
//           Room genera el código real; tú solo describes qué quieres.
// ============================================================================

package com.induztek.protocolos.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.induztek.protocolos.model.CodigoRecuperacion

// @Database: registra las tablas de esta base de datos.
@Database(
    // Lista de tablas: equipos, pruebas, protocolos, usuarios y codigos_recuperacion.
    entities = [EquipoEntity::class, PruebaEntity::class, ProtocoloEntity::class, UsuarioEntity::class, CodigoRecuperacion::class],
    // version: número de versión del esquema. Si cambias las tablas, hay que subirlo.
    version = 2,
    // No exporta el esquema a un archivo JSON.
    exportSchema = false
)
// 'abstract' = Room escribe la clase concreta por ti. Extiende RoomDatabase.
abstract class AppDatabase : RoomDatabase() {

    // Un método por cada DAO: es la 'puerta' para consultar cada tabla.
    abstract fun equipoDao(): EquipoDao
    abstract fun pruebaDao(): PruebaDao
    abstract fun protocoloDao(): ProtocoloDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun codigoRecuperacionDao(): CodigoRecuperacionDao

    // companion object: aquí vive la función que entrega la ÚNICA instancia de la BD (patrón Singleton).
    companion object {
        // @Volatile: los cambios en INSTANCE son visibles de inmediato para todos los hilos.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Si ya existe la BD la devuelve; si no, la crea. 'synchronized' evita que dos hilos la creen a la vez.
        // Room.databaseBuilder(...) usa el nombre de archivo 'induztek_database'.
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "induztek_database"
                )
                    // Si cambia la versión y no hay migración escrita, BORRA y recrea la base. Cómodo en desarrollo;
                    //   en producción perdería datos.
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
