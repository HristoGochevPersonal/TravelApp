package com.example.travelapp.presentation.cityWithLandmarks

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.travelapp.presentation.listCities.CityModel
import java.lang.IllegalArgumentException

// Landmarks android view model factory
@Suppress("UNCHECKED_CAST")
class LandmarksViewModelFactory(
    private val cityModel: CityModel,
    private val application: Application
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LandmarksViewModel::class.java)) {
            return LandmarksViewModel(cityModel, application) as T
        }
        throw IllegalArgumentException("ViewModel class not found")
    }
}