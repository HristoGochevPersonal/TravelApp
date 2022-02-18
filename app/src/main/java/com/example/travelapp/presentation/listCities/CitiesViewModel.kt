package com.example.travelapp.presentation.listCities

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import  androidx.lifecycle.viewModelScope
import com.example.travelapp.core.entity.City
import com.example.travelapp.core.repository.DatabaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// Cities list android view model
class CitiesViewModel(application: Application) : AndroidViewModel(application) {
    // Contains a reference to the database repository for the whole application
    private var databaseRepository = DatabaseRepository(application.applicationContext)

    // State flow for the cities recycler view
    private val _citiesStateFlow = MutableStateFlow<List<CityModel>>(listOf())
    val citiesStateFlow = _citiesStateFlow.asStateFlow()

    // Shared flows for signaling of a success deletion or creation of a city
    private val _deletionSharedFlow = MutableSharedFlow<Pair<Boolean, CityModel>>()
    val deletionSharedFlow = _deletionSharedFlow.asSharedFlow()

    private val _creationSharedFlow = MutableSharedFlow<Pair<Boolean, CityModel>>()
    val creationSharedFlow = _creationSharedFlow.asSharedFlow()

    // Refreshes the cities state flow with info from the repository
    fun refreshCities() {
        viewModelScope.launch(Dispatchers.IO) {
            val cities = databaseRepository.citiesFetch().map {
                CityModel(it.cityId, it.name, it.description)
            }
            _citiesStateFlow.emit(cities)
        }
    }

    // Deletes a city from the repository and then signals to the observers of
    // the deletion shared flow whether the deletion was successful
    fun deleteCity(cityModel: CityModel) {
        viewModelScope.launch(Dispatchers.IO) {
            val deleted = databaseRepository.cityDelete(cityModel.id)
            if (deleted > 0) _deletionSharedFlow.emit(Pair(true, cityModel))
            else _deletionSharedFlow.emit(Pair(false, cityModel))
        }
    }

    // Creates a city, inserts it in the repository and then signals to the observers of
    // the creation shared flow whether the creation was successful
    fun createCity(cityModel: CityModel) {
        viewModelScope.launch(Dispatchers.IO) {
            val city = City(-1, cityModel.name, cityModel.description)
            val creation = databaseRepository.cityInsert(city)
            if (creation > 0) _creationSharedFlow.emit(Pair(true, cityModel))
            else _creationSharedFlow.emit(Pair(false, cityModel))
        }
    }
}