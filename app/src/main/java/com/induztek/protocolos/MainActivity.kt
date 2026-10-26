package com.induztek.protocolos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.induztek.protocolos.navigation.AppNavegacion
import com.induztek.protocolos.ui.theme.InduztekTheme
import com.induztek.protocolos.viewmodel.*

class MainActivity : ComponentActivity() {

    private val loginViewModel: LoginViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val app = application as? InduztekApp
                    ?: (applicationContext as? InduztekApp)
                    ?: error("Application is not InduztekApp")
                @Suppress("UNCHECKED_CAST")
                return LoginViewModel(app.usuarioRepository, app.sessionManager) as T
            }
        }
    }

    private val equipoViewModel: EquipoViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val app = application as? InduztekApp
                    ?: (applicationContext as? InduztekApp)
                    ?: error("Application is not InduztekApp")
                @Suppress("UNCHECKED_CAST")
                return EquipoViewModel(app.equipoRepository, app.pruebaRepository) as T
            }
        }
    }

    private val pruebaViewModel: PruebaViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val app = application as? InduztekApp
                    ?: (applicationContext as? InduztekApp)
                    ?: error("Application is not InduztekApp")
                @Suppress("UNCHECKED_CAST")
                return PruebaViewModel(app.equipoRepository, app.pruebaRepository, app.protocoloRepository) as T
            }
        }
    }

    private val protocoloViewModel: ProtocoloViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val app = application as? InduztekApp
                    ?: (applicationContext as? InduztekApp)
                    ?: error("Application is not InduztekApp")
                @Suppress("UNCHECKED_CAST")
                return ProtocoloViewModel(
                    app.protocoloRepository,
                    app.pruebaRepository,
                    app.instrumentoRepository,
                    app.equipoRepository
                ) as T
            }
        }
    }

    private val visitaViewModel: VisitaViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val app = application as? InduztekApp
                    ?: (applicationContext as? InduztekApp)
                    ?: error("Application is not InduztekApp")
                @Suppress("UNCHECKED_CAST")
                return VisitaViewModel(
                    app.visitaRepository,
                    app.protocoloRepository,
                    app.pruebaRepository,
                    app.equipoRepository,
                    app.actaRepository
                ) as T
            }
        }
    }

    private val adminViewModel: AdminViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val app = application as? InduztekApp
                    ?: (applicationContext as? InduztekApp)
                    ?: error("Application is not InduztekApp")
                @Suppress("UNCHECKED_CAST")
                return AdminViewModel(
                    app.instrumentoRepository,
                    app.usuarioRepository
                ) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InduztekTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    
                    
                    AppNavegacion(
                        loginViewModel = loginViewModel,
                        equipoViewModel = equipoViewModel,
                        pruebaViewModel = pruebaViewModel,
                        protocoloViewModel = protocoloViewModel,
                        visitaViewModel = visitaViewModel,
                        adminViewModel = adminViewModel
                    )
                }
            }
        }
    }
}
