package com.example.travelapp.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.travelapp.R
import com.example.travelapp.databinding.ActivityMainBinding
import com.example.travelapp.presentation.listCities.CitiesFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initializes the activity with a fragment containing the cities
        placeCitiesFragment()
    }

    // Places a cities fragment in the main activity fragment frame
    private fun placeCitiesFragment() {
        val citiesFragment = CitiesFragment()
        supportFragmentManager.beginTransaction().apply {
            setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            replace(R.id.frame_layout, citiesFragment, "Cities_Fragment")
            commit()
        }
    }


    override fun onBackPressed() {
        // Get whether the cities fragment is currently shown
        val citiesFragmentIsDisplayed =
            supportFragmentManager.fragments.any { it.tag == "Cities_Fragment" }

        // If it is not replace the current fragment with the cities fragment on back press
        if (!citiesFragmentIsDisplayed) {
            placeCitiesFragment()
        } else { // If it is simply navigate back
            super.onBackPressed()
        }
    }
}