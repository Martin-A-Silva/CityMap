package com.example.citymap.map

import androidx.lifecycle.ViewModel
import com.example.citymap.data.model.Coord
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SharedMapViewModel @Inject constructor() : ViewModel() {
    private val _selectedCoord = MutableStateFlow(Coord(0.0,0.0))
    val selectedCoord: StateFlow<Coord> = _selectedCoord

    fun selectCoord(coord: Coord) {
        _selectedCoord.value = coord
    }
}