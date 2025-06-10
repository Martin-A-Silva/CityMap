package com.example.citymap

import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.citymap.data.model.City
import com.example.citymap.data.model.Coord
import com.example.citymap.data.repository.CityRepository
import com.example.citymap.ui.citylist.CityListViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class CityListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: CityRepository
    private lateinit var viewModel: CityListViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        viewModel = CityListViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given list of cities when prefix updated then return list of cities`() = runTest {
        // Given
        val expectedCities = listOf(
            City(1, "London", "UK", Coord(0.0, 0.0)),
            City(2, "Lisbon", "PT", Coord(0.0, 0.0))
        )
        coEvery {
            repository.getCities("L", false)
        } returns flowOf(PagingData.from(expectedCities))

        // When
        viewModel.updatePrefix("L")

        val result = viewModel.cityPagingData.first()
        val actualCities = extractCities(result)

        // Then
        assertEquals(2, actualCities.size)
        assertTrue(actualCities.any { it.name == "London" })
        coVerify { repository.getCities("L", false) }
    }

    @Test
    fun `Given list of cities when prefix updated then return list of filtered cities`() = runTest {
        // Given
        val allCities = listOf(
            City(1, "London", "UK", Coord(0.0, 0.0)),
            City(2, "Lisbon", "PT", Coord(0.0, 0.0)),
            City(3, "Buenos Aires", "AR", Coord(0.0, 0.0))
        )

        val filteredCities = allCities.filter { it.name.startsWith("L") }

        coEvery {
            repository.getCities("L", false)
        } returns flowOf(PagingData.from(filteredCities))

        // When
        viewModel.updatePrefix("L")
        val result = viewModel.cityPagingData.first()
        val actualCities = extractCities(result)

        // Then
        assertEquals(2, actualCities.size)
        assertTrue(actualCities.any { it.name == "London" })
        assertTrue(actualCities.any { it.name == "Lisbon" })
        assertTrue(actualCities.none { it.name == "Buenos Aires" })

        coVerify { repository.getCities("L", false) }
    }

    @Test
    fun `Given list of cities when prefix updated with invalid string then return empty list`() =
        runTest {
            // Given
            val allCities = listOf(
                City(1, "London", "UK", Coord(0.0, 0.0)),
                City(2, "Lisbon", "PT", Coord(0.0, 0.0)),
                City(3, "Buenos Aires", "AR", Coord(0.0, 0.0))
            )

            val filteredCities = allCities.filter { it.name.startsWith("#@") }

            coEvery {
                repository.getCities("#@", false)
            } returns flowOf(PagingData.from(filteredCities))

            // When
            viewModel.updatePrefix("#@")
            val result = viewModel.cityPagingData.first()
            val actualCities = extractCities(result)

            // Then
            assertEquals(0, actualCities.size)
            assertTrue(actualCities.none { it.name == "London" })
            assertTrue(actualCities.none { it.name == "Lisbon" })
            assertTrue(actualCities.none { it.name == "Buenos Aires" })

            coVerify { repository.getCities("#@", false) }
        }


    @Test
    fun `Given list of cities when toggleFavoritesOnly called then return list of favorite cities`() =
        runTest {
            // Given
            val allCities = listOf(
                City(1, "London", "UK", Coord(0.0, 0.0), true),
                City(2, "Lisbon", "PT", Coord(0.0, 0.0), false),
                City(3, "Buenos Aires", "AR", Coord(0.0, 0.0), false)
            )

            val filteredCities = allCities.filter { it.isFavorite }

            coEvery {
                repository.getCities("", true)
            } returns flowOf(PagingData.from(filteredCities))

            // When
            viewModel.toggleFavoritesOnly()
            val result = viewModel.cityPagingData.first()
            val actualCities = extractCities(result)

            // Then
            assertEquals(1, actualCities.size)
            assertTrue(actualCities.any { it.name == "London" })
            assertTrue(actualCities.none { it.name == "Lisbon" })
            assertTrue(actualCities.none { it.name == "Buenos Aires" })
            coVerify { repository.getCities("", true) }
        }

    private suspend fun TestScope.extractCities(pagingData: PagingData<City>): List<City> {
        val differ = AsyncPagingDataDiffer(
            diffCallback = object : DiffUtil.ItemCallback<City>() {
                override fun areItemsTheSame(oldItem: City, newItem: City) =
                    oldItem.id == newItem.id

                override fun areContentsTheSame(oldItem: City, newItem: City) = oldItem == newItem
            },
            updateCallback = NoopListCallback(),
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(pagingData)
        advanceUntilIdle()
        return differ.snapshot().items
    }

    private class NoopListCallback : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }
}
