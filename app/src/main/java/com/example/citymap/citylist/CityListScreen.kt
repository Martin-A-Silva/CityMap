package com.example.citymap.citylist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.citymap.data.model.City

@Composable
fun CityListScreen(
    navController: NavController,
    onItemClick: (City) -> Unit,
    onInfoClick: (City) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CityListViewModel = hiltViewModel()
) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            Spacer(modifier = Modifier.height(24.dp))

            SearchBar(
                hint = "Search city by name", modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                viewModel.updatePrefix(it)
            }
            CityList(navController, onItemClick, onInfoClick)
        }
    }
}

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    hint: String = "Search...",
    onSearch: (String) -> Unit = {}
) {
    var text by remember { mutableStateOf("") }
    var isHintDisplayed by remember { mutableStateOf(hint != "") }
    Box(modifier = modifier) {
        BasicTextField(
            value = text,
            onValueChange = {
                text = it
                onSearch(it)
            },
            maxLines = 1,
            singleLine = true,
            textStyle = TextStyle(color = Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(5.dp, CircleShape)
                .background(Color.White, CircleShape)
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .onFocusChanged {
                    isHintDisplayed = it.isFocused.not()
                }
        )
        if (isHintDisplayed) {
            Text(
                text = hint,
                color = Color.LightGray,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
            )
        }
    }
}

@Composable
fun CityList(
    navController: NavController,
    onItemClick: (City) -> Unit,
    onInfoClick: (City) -> Unit,
    viewModel: CityListViewModel = hiltViewModel()
) {
    //val cityList by remember { viewModel.cities }
    val isLoading by remember { viewModel.isLoading }
    val loadError by remember { viewModel.loadError }
    val isSearching by remember { viewModel.isSearching }
    LaunchedEffect(Unit) {
        viewModel.loadCitiesFromNetwork()
    }
    val cities = viewModel.cityPagingData.collectAsLazyPagingItems()

    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        items(cities.itemCount) { index ->
            val city = cities[index]
            city?.let {
                CityEntry(
                    entry = it,
                    onItemClick = onItemClick,
                    onInfoClick = onInfoClick
                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
        }

        cities.apply {
            when {
                loadState.append is LoadState.Loading -> {
                    item { CircularProgressIndicator() }
                }

                loadState.refresh is LoadState.Loading -> {
                    item { CircularProgressIndicator() }
                }

                loadState.append is LoadState.Error -> {
                    item { Text("Error loading more cities") }
                }
            }
        }
    }
}


@Composable
fun CityEntry(
    entry: City,
    onItemClick: (City) -> Unit,
    onInfoClick: (City) -> Unit,
) {
    var isToggled by rememberSaveable { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onItemClick(entry)
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "${entry.name}, ${entry.country}",
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(16.dp))
            IconButton(
                onClick = { onInfoClick.invoke(entry) }
            ) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = "More info"
                )
            }
            IconButton(
                onClick = { isToggled = !isToggled }
            ) {
                Icon(
                    if (isToggled) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isToggled) "Remove from favorites" else "Add to favorites"
                )
            }
        }
        Row {
            Text("Lat: ${entry.coord.lon} - Lon: ${entry.coord.lat}")
        }
    }
}
