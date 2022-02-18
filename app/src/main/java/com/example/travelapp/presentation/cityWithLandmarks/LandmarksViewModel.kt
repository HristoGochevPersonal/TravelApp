package com.example.travelapp.presentation.cityWithLandmarks

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelapp.core.entity.City
import com.example.travelapp.core.entity.Landmark
import com.example.travelapp.core.repository.DatabaseRepository
import com.example.travelapp.presentation.listCities.CityModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// City landmarks list android view model
// Accepts a city model
class LandmarksViewModel(private val cityModel: CityModel, application: Application) :
    AndroidViewModel(application) {

    // Contains a reference to the database repository for the whole application
    private var databaseRepository = DatabaseRepository(application.applicationContext)

    // State flow for the landmarks recycler view
    private val _landmarksStateFlow = MutableStateFlow<List<LandmarkModel>>(listOf())
    val landmarksStateFlow = _landmarksStateFlow.asStateFlow()

    // Shared flows for signaling of a success deletion or creation of a landmark
    private val _deletionSharedFlow = MutableSharedFlow<Pair<Boolean, LandmarkModel>>()
    val deletionSharedFlow = _deletionSharedFlow.asSharedFlow()

    private val _creationSharedFlow = MutableSharedFlow<Pair<Boolean, LandmarkModel>>()
    val creationSharedFlow = _creationSharedFlow.asSharedFlow()

    // Refreshes the landmarks state flow with info from the repository
    fun refreshLandmarks() {
        viewModelScope.launch(Dispatchers.IO) {
            val landmarks = databaseRepository.landmarksFetch(cityModel.id).map {
                LandmarkModel(it.landmarkId, it.name, it.description, cityModel.id)
            }
            _landmarksStateFlow.emit(landmarks)
        }
    }

    // Deletes a landmark from the repository associated with a city
    // and then signals to the observers of the deletion shared flow
    // whether the deletion was successful
    fun deleteLandmark(landmarkModel: LandmarkModel) {
        viewModelScope.launch(Dispatchers.IO) {
            val deleted = databaseRepository.landmarkDelete(landmarkModel.id)
            if (deleted > 0) _deletionSharedFlow.emit(Pair(true, landmarkModel))
            else _deletionSharedFlow.emit(Pair(false, landmarkModel))
        }
    }

    // Creates a landmark associated with a city,
    // Inserts it in the repository and then signals to the observers of
    // the creation shared flow whether the creation was successful
    fun createLandmark(landmarkModel: LandmarkModel) {
        viewModelScope.launch(Dispatchers.IO) {
            val landmark = Landmark(-1, landmarkModel.name, landmarkModel.description, cityModel.id)
            val creation = databaseRepository.landMarkInsert(landmark)
            if (creation > 0) _creationSharedFlow.emit(Pair(true, landmarkModel))
            else _creationSharedFlow.emit(Pair(false, landmarkModel))
        }
    }
}