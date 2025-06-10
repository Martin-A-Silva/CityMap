package com.example.citymap

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.citymap.data.model.City
import com.example.citymap.data.model.Coord
import com.example.citymap.ui.citydetail.CityDetailScreen
import com.example.citymap.ui.citylist.CityListScreen
import com.example.citymap.ui.map.MapScreen
import com.example.citymap.ui.map.SharedMapViewModel
import com.example.citymap.ui.theme.CityMapTheme
import com.example.citymap.util.parcelableType
import dagger.hilt.android.AndroidEntryPoint
import kotlin.reflect.typeOf

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CityMapTheme {
                val configuration = LocalConfiguration.current
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "city_list_screen"
                ) {
                    composable("city_list_screen") {
                        val sharedMapViewModel: SharedMapViewModel = hiltViewModel()
                        val selectedCoord by sharedMapViewModel.selectedCoord.collectAsState()

                        Row {
                            CityListScreen(
                                onItemClick = { coord ->
                                    when (configuration.orientation) {
                                        Configuration.ORIENTATION_PORTRAIT -> {
                                            navController.navigate(coord)
                                        }

                                        else -> {
                                            sharedMapViewModel.selectCoord(coord)
                                        }
                                    }
                                },
                                onInfoClick = { city ->
                                    navController.navigate(city)
                                },
                                modifier = when (configuration.orientation) {
                                    Configuration.ORIENTATION_PORTRAIT -> Modifier.fillMaxSize()
                                    else -> Modifier.weight(1f)
                                }
                            )
                            if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                                MapScreen(
                                    coord = selectedCoord,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                    composable<City>(
                        typeMap = mapOf(
                            typeOf<City>() to parcelableType<City>(),
                            typeOf<Coord>() to parcelableType<Coord>()
                        )
                    ) { backStackEntry ->
                        val cityDetail = backStackEntry.toRoute<City>()
                        CityDetailScreen(
                            navController = navController,
                            city = cityDetail
                        )
                    }
                    composable<Coord>(
                        typeMap = mapOf(
                            typeOf<Coord>() to parcelableType<Coord>()
                        )
                    ) { backStackEntry ->
                        val cityCoord = backStackEntry.toRoute<Coord>()
                        MapScreen(
                            coord = cityCoord
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CityMapTheme {
        Greeting("Android")
    }
}