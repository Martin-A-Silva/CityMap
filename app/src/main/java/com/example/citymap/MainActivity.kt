package com.example.citymap

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.citymap.citydetail.CityDetailScreen
import com.example.citymap.citylist.CityListScreen
import com.example.citymap.data.model.City
import com.example.citymap.data.model.Coord
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
                    startDestination = when (configuration.orientation) {
                        Configuration.ORIENTATION_PORTRAIT -> {
                            "city_list_screen"
                        }

                        else -> {
                            "city_map_screen"
                        }
                    }
                ) {
                    composable("city_list_screen") {
                        CityListScreen(
                            navController,
                            onItemClick = { city ->
                                navController.navigate(city)
                            },
                            onInfoClick = { city ->
                                navController.navigate(city)
                            }
                        )
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
                    composable(
                        "map_screen"/*,
                        arguments = listOf(
                            navArgument("lon") { type = NavType.FloatType },
                            navArgument("lat") { type = NavType.FloatType }
                        )*/
                    ) {

                    }
                    composable("city_map_screen") {

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