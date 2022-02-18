package com.example.travelapp.presentation.listCities

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelapp.R
import com.example.travelapp.databinding.FragmentCitiesBinding
import com.example.travelapp.presentation.cityWithLandmarks.LandmarkModel
import com.example.travelapp.presentation.cityWithLandmarks.LandmarksFragment
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.coroutines.flow.collectLatest


class CitiesFragment : Fragment() {
    private var _binding: FragmentCitiesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CitiesViewModel by viewModels()
    private lateinit var citiesAdapter: CitiesAdapter

    // Standard view-binding setup
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCitiesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initializing the recycler view
        initRecyclerView()
        // Initializing the view model observers
        initViewModelObservers()

        // When the city add button is pressed
        binding.cityAddButton.setOnClickListener {
            // A city creation dialog will be opened
            openCreateCityDialog()
        }

        // Refreshes the cities recycler view on creation of the fragment
        viewModel.refreshCities()
    }

    private fun initRecyclerView() {
        // High order function that executes whenever
        // the user single taps on an item in the recycler view
        val onSingleTap = object : CitiesAdapter.Interaction {
            override fun onItemSelected(position: Int, item: CityModel) {
                // Creates a new landmarks fragment
                val landmarksFragment = LandmarksFragment()
                // Sends it information about the city it should refer to
                val gson = Gson()
                val json = gson.toJson(item)
                val bundle = Bundle()
                bundle.putString("cityModel", json)
                landmarksFragment.arguments = bundle

                // Replaces the current fragment with the landmarks fragment we just created
                parentFragmentManager.beginTransaction().apply {
                    setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    replace(R.id.frame_layout, landmarksFragment)
                    commit()
                }
            }
        }
        // High order function that executes whenever
        // the user long presses on an item in the recycler view
        val onLongPress = object : CitiesAdapter.Interaction {
            override fun onItemSelected(position: Int, item: CityModel) {
                // Displays the delete city confirmation dialog
                deleteCity(item)
            }
        }

        // Creating the cities adapter with the given interactions
        citiesAdapter = CitiesAdapter(onSingleTap, onLongPress)
        // Setting it up for the recycler view
        binding.citiesRecyclerView.apply {
            this.layoutManager = LinearLayoutManager(this.context)
            this.adapter = citiesAdapter
        }
    }

    // Setting up the view model observers
    private fun initViewModelObservers() {
        // Observing the cities state flow for updates
        lifecycleScope.launchWhenStarted {
            viewModel.citiesStateFlow.collectLatest {
                // And updates the cities adapter
                citiesAdapter.submitList(it)
            }
        }
        // Observing the creating shared flow
        lifecycleScope.launchWhenStarted {
            viewModel.creationSharedFlow.collectLatest {
                // Displaying a snackbar whenever an update occurs
                val text = if (it.first) "Successfully created ${it.second.name}"
                else "Could not create ${it.second.name}"

                Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()

                // Refreshing the recycler view with the new data
                viewModel.refreshCities()
            }
        }
        // Observing the deletion shared flow
        lifecycleScope.launchWhenStarted {
            viewModel.deletionSharedFlow.collectLatest {
                // Displaying a snackbar whenever an update occurs
                val text = if (it.first) "Successfully deleted ${it.second.name}"
                else "Could not delete ${it.second.name}"

                Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()

                // Refreshing the recycler view with the new data
                viewModel.refreshCities()
            }
        }
    }

    // Opens a new create city dialog
    private fun openCreateCityDialog() {
        // High order function that executes whenever
        // the user clicks the create button on the dialog
        val onCreateClicked = { cityModel: CityModel ->
            viewModel.createCity(cityModel)
        }

        // Create the dialog and show it
        val addCityDialog = AddCityDialog(onCreateClicked)
        addCityDialog.show(requireActivity().supportFragmentManager, "AddCityDialog")
    }

    // Displays the delete city confirmation dialog
    private fun deleteCity(cityModel: CityModel) {
        val builder = AlertDialog.Builder(requireActivity()).apply {
            setTitle("Confirm")
            setMessage("Delete ${cityModel.name}?")
            setCancelable(false)
            setPositiveButton("Yes") { dialogInterface, _ ->
                dialogInterface.dismiss()
                viewModel.deleteCity(cityModel)
            }
            setNegativeButton("No") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
        }
        builder.show()
    }
}