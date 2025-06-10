package com.example.citymap.ui.citydetail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.citymap.data.model.City
import com.example.citymap.data.model.Coord

@Composable
fun CityDetailScreen(
    navController: NavController,
    city: City,
    modifier: Modifier = Modifier
) {
    Surface {
        Column(
            modifier = modifier
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Go back",
                tint = Color.Black,
                modifier = Modifier
                    .size(36.dp)
                    .offset(8.dp, 8.dp)
                    .clickable {
                        navController.popBackStack()
                    }
            )

            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "The city of ${city.name} is located in ${city.country}",
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Serif,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "You'll find it here: ${city.coord.lat}, ${city.coord.lon}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.Serif,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

        }
    }
}

@Preview(showBackground = true, name = "CityDetailScreen Preview")
@Composable
fun CityDetailScreenPreview() {
    val mockNavController = rememberNavController()
    val mockCity = City(
        id = 1,
        name = "Springfield",
        country = "USA",
        coord = Coord(lat = 37.2153, lon = -93.2982),
    )
    CityDetailScreen(
        navController = mockNavController,
        city = mockCity
    )
}