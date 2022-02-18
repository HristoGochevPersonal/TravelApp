package com.example.travelapp.presentation.cityWithLandmarks

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelapp.databinding.FragmentLandmarksBinding
import com.example.travelapp.presentation.listCities.CityModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.coroutines.flow.collectLatest

class LandmarksFragment : Fragment() {
    private var _binding: FragmentLandmarksBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: LandmarksViewModel
    private lateinit var landmarksAdapter: LandmarksAdapter

    // Standard view-binding setup
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLandmarksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Gets the associated city model
        val json = requireArguments().getString("cityModel")
        // If no city model is associated with it
        if (json == null) {
            // Navigate back and stop execution of the current landmarks fragment
            requireActivity().onBackPressed()
            return
        }

        val gson = Gson()
        val cityModel = gson.fromJson(json, CityModel::class.java)

        // Creates the view model factory for the view model
        val viewModelFactory = LandmarksViewModelFactory(
            cityModel,
            requireActivity().application
        )

        // Creates the view model
        viewModel = ViewModelProvider(this, viewModelFactory)[LandmarksViewModel::class.java]

        // Sets the correct header based on the associated city model
        val headerText = "${cityModel.name} landmarks"
        binding.landmarksTextView.text = headerText

        // Initializing the recycler view
        initRecyclerView()
        // Initializing the view model observers
        initViewModelObservers()

        // When the landmark add button is pressed
        binding.landmarkAddButton.setOnClickListener {
            // A landmark creation dialog will be opened
            openCreateLandmarkDialog(cityModel)
        }

        // Refreshes the landmark recycler view on creation of the fragment
        viewModel.refreshLandmarks()
    }

    private fun initRecyclerView() {
        // High order function that executes whenever
        // the user single taps on an item in the recycler view
        val onSingleTap = object : LandmarksAdapter.Interaction {
            override fun onItemSelected(position: Int, item: LandmarkModel) {
                // Displays a simple snackbar that an item was selected
                Snackbar.make(
                    binding.root,
                    "You chose ${item.name}",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
        // High order function that executes whenever
        // the user long presses on an item in the recycler view
        val onLongPress = object : LandmarksAdapter.Interaction {
            override fun onItemSelected(position: Int, item: LandmarkModel) {
                // Displays the delete landmark confirmation dialog
                deleteLandmark(item)
            }
        }

        // Creating the landmarks adapter with the given interactions
        landmarksAdapter = LandmarksAdapter(onSingleTap, onLongPress)
        // Setting it up for the recycler view
        binding.landmarksRecyclerView.apply {
            this.layoutManager = LinearLayoutManager(this.context)
            this.adapter = landmarksAdapter
        }
    }

    // Setting up the view model observers
    private fun initViewModelObservers() {
        // Observing the landmarks state flow for updates
        lifecycleScope.launchWhenStarted {
            viewModel.landmarksStateFlow.collectLatest {
                // And updates the landmarks adapter
                landmarksAdapter.submitList(it)
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
                viewModel.refreshLandmarks()
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.deletionSharedFlow.collectLatest {
                // Displaying a snackbar whenever an update occurs
                val text = if (it.first) "Successfully deleted ${it.second.name}"
                else "Could not delete ${it.second.name}"

                Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()

                // Refreshing the recycler view with the new data
                viewModel.refreshLandmarks()
            }
        }
    }

    // Opens a new create landmark dialog for the associated city
    private fun openCreateLandmarkDialog(cityModel: CityModel) {
        // High order function that executes whenever
        // the user clicks the create button on the dialog
        val onCreateClicked = { landmarkModel: LandmarkModel ->
            viewModel.createLandmark(landmarkModel)
        }
        // Create the dialog and show it
        val addLandmarkDialog = AddLandmarkDialog(cityModel, onCreateClicked)
        addLandmarkDialog.show(requireActivity().supportFragmentManager, "AddLandmarkDialog")
    }

    // Displays the delete landmark confirmation dialog
    private fun deleteLandmark(landmarkModel: LandmarkModel) {
        val builder = AlertDialog.Builder(requireActivity()).apply {
            setTitle("Confirm")
            setMessage("Delete ${landmarkModel.name}?")
            setCancelable(false)
            setPositiveButton("Yes") { dialogInterface, _ ->
                dialogInterface.dismiss()
                viewModel.deleteLandmark(landmarkModel)
            }
            setNegativeButton("No") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
        }
        builder.show()
    }
}