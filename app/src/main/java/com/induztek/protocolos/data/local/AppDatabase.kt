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

    abstract fun equipoDao(): EquipoDao
    abstract fun visitaDao(): VisitaDao
    abstract fun protocoloDao(): ProtocoloDao
    abstract fun pruebaDao(): PruebaDao
    abstract fun actaDao(): ActaDao
    abstract fun instrumentoDao(): InstrumentoDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun codigoRecuperacionDao(): CodigoRecuperacionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

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
