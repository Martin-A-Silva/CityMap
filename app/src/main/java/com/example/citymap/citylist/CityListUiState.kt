package com.example.citymap.citylist

import com.example.citymap.data.remote.response.CityApiModel

sealed class CityListUiState {

    object ShowLoadingUiState : CityListUiState()

    data class ShowCityListUiState(
        val cities: List<CityApiModel>
    ) : CityListUiState()

    data class ShowErrorUiSate(val error: String) : CityListUiState()
}