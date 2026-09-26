package com.induztek.protocolos

import android.app.Application
import com.induztek.protocolos.data.local.AppDatabase
import com.induztek.protocolos.data.repository.EquipoRepository
import com.induztek.protocolos.data.repository.ProtocoloRepository
import com.induztek.protocolos.data.repository.PruebaRepository

class InduztekApp : Application() {

    val database by lazy { AppDatabase.getDatabase(this) }
    val equipoRepository by lazy { EquipoRepository(database.equipoDao()) }
    val pruebaRepository by lazy { PruebaRepository(database.pruebaDao()) }
    val protocoloRepository by lazy { ProtocoloRepository(database.protocoloDao()) }
}
