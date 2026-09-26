package com.induztek.app.di

import android.content.Context
import androidx.room.Room
import com.induztek.app.data.local.DatabaseCallback
import com.induztek.app.data.local.InduztekDatabase
import com.induztek.app.data.local.dao.EquipoDao
import com.induztek.app.data.local.dao.PruebaDao
import com.induztek.app.data.repository.EquipoRepositoryImpl
import com.induztek.app.data.repository.PruebaRepositoryImpl
import com.induztek.app.domain.repository.EquipoRepository
import com.induztek.app.domain.repository.PruebaRepository
import javax.inject.Provider
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo Hilt para el grafo de dependencias de la capa data.
 *
 * Decisiones de diseño:
 * - @InstallIn(SingletonComponent) → Room y repositorios tienen ciclo de vida de la App.
 * - @Provides para Room (requiere parámetros en la construcción).
 * - @Binds para repositorios (Hilt solo necesita saber qué implementación usar
 *   para cada interfaz — sin instanciar manualmente).
 *
 * El ViewModel recibe PruebaRepository (interfaz) → Hilt inyecta PruebaRepositoryImpl.
 * Para tests se puede proveer un FakePruebaRepository en un módulo de test.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        equipoDaoProvider: Provider<EquipoDao>,
        pruebaDaoProvider: Provider<PruebaDao>
    ): InduztekDatabase =
        Room.databaseBuilder(
            context,
            InduztekDatabase::class.java,
            InduztekDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()   // ⚠ solo para desarrollo — usar Migration en producción
            .addCallback(DatabaseCallback(equipoDaoProvider, pruebaDaoProvider))
            .build()

    @Provides
    fun provideEquipoDao(db: InduztekDatabase): EquipoDao = db.equipoDao()

    @Provides
    fun providePruebaDao(db: InduztekDatabase): PruebaDao = db.pruebaDao()

    @Provides
    @Singleton
    fun providePasswordPolicy(): com.induztek.app.domain.policy.PasswordPolicy =
        com.induztek.app.domain.policy.PasswordPolicy()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindEquipoRepository(impl: EquipoRepositoryImpl): EquipoRepository

    @Binds
    @Singleton
    abstract fun bindPruebaRepository(impl: PruebaRepositoryImpl): PruebaRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: com.induztek.app.data.repository.AuthRepositoryImpl): com.induztek.app.domain.repository.AuthRepository
}
