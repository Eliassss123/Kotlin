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
import androidx.navigation.compose.rememberNavController
import com.induztek.protocolos.ui.navigation.NavGraph
import com.induztek.protocolos.ui.theme.InduztekTheme
import com.induztek.protocolos.viewmodel.EquipoViewModel
import com.induztek.protocolos.viewmodel.LoginViewModel
import com.induztek.protocolos.viewmodel.ProtocoloViewModel
import com.induztek.protocolos.viewmodel.PruebaViewModel

class MainActivity : ComponentActivity() {

    private val loginViewModel: LoginViewModel by viewModels()

    private val equipoViewModel: EquipoViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val app = application as InduztekApp
                @Suppress("UNCHECKED_CAST")
                return EquipoViewModel(app.equipoRepository, app.pruebaRepository) as T
            }
        }
    }

    private val pruebaViewModel: PruebaViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val app = application as InduztekApp
                @Suppress("UNCHECKED_CAST")
                return PruebaViewModel(app.equipoRepository, app.pruebaRepository, app.protocoloRepository) as T
            }
        }
    }

    private val protocoloViewModel: ProtocoloViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val app = application as InduztekApp
                @Suppress("UNCHECKED_CAST")
                return ProtocoloViewModel(app.protocoloRepository) as T
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
                    val navController = rememberNavController()
                    NavGraph(
                        navController = navController,
                        loginViewModel = loginViewModel,
                        equipoViewModel = equipoViewModel,
                        pruebaViewModel = pruebaViewModel,
                        protocoloViewModel = protocoloViewModel
                    )
                }
            }
        }
    }
}
