package com.induztek.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.induztek.app.data.local.dao.EquipoDao
import com.induztek.app.data.local.dao.PruebaDao
import com.induztek.app.data.local.entity.EquipoEntity
import com.induztek.app.data.local.entity.PruebaEntity

/**
 * Base de datos Room — punto de entrada único a SQLite.
 *
 * Decisiones de diseño:
 * - exportSchema = false → no genera archivo JSON de esquema en /assets.
 *   En producción debería ser true para migraciones controladas.
 * - version = 1 → incrementar y proveer Migration cuando el esquema cambie.
 * - @TypeConverters(Converters::class) → necesario para persistir Map<String,String>
 *   (medicionesJson) ya que Room no soporta tipos complejos nativamente.
 *
 * La instancia se crea como singleton en el módulo Hilt (DatabaseModule),
 * nunca se instancia directamente en ViewModel o composables.
 */
@Database(
    entities = [EquipoEntity::class, PruebaEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(InduztekDatabase.Converters::class)
abstract class InduztekDatabase : RoomDatabase() {

    abstract fun equipoDao(): EquipoDao
    abstract fun pruebaDao(): PruebaDao

    /**
     * TypeConverters para tipos que Room no puede persistir directamente.
     * Map<String,String> ↔ JSON String usando Gson.
     */
    class Converters {
        private val gson = Gson()
        private val mapType = object : TypeToken<Map<String, String>>() {}.type

        @TypeConverter
        fun mapToJson(map: Map<String, String>): String = gson.toJson(map)

        @TypeConverter
        fun jsonToMap(json: String): Map<String, String> =
            gson.fromJson(json, mapType) ?: emptyMap()
    }

    companion object {
        const val DATABASE_NAME = "induztek_db"
    }
}
