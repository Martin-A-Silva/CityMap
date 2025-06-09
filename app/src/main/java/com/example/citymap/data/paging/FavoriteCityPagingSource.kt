package com.example.citymap.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.citymap.data.model.City
import com.example.citymap.data.local.CityDao
import com.example.citymap.data.local.FavoriteCityDao
import kotlinx.coroutines.flow.first

class FavoriteCityPagingSource(
    private val cityDao: CityDao,
    private val favoriteCityDao: FavoriteCityDao,
    private val prefix: String
) : PagingSource<Int, City>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, City> {
        return try {
            val favoriteIds = favoriteCityDao.getFavoriteIds().first()

            val allMatching = cityDao.searchCitiesBlocking(prefix)
                .filter { it.id in favoriteIds }

            LoadResult.Page(
                data = allMatching,
                prevKey = null,
                nextKey = null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, City>): Int? = null
}

