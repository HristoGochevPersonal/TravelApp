package com.example.travelapp.presentation.cityWithLandmarks

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.travelapp.databinding.DialogAddCityBinding
import com.example.travelapp.databinding.DialogAddLandmarkBinding
import com.example.travelapp.presentation.listCities.CityModel

// Add city dialog
// with a city model
// and a callback that executes whenever the user creates a new landmark
class AddLandmarkDialog(
    private val cityModel: CityModel,
    private val createNewLandmark: (item: LandmarkModel) -> Unit
) : DialogFragment() {
    private var _binding: DialogAddLandmarkBinding? = null
    private val binding get() = _binding!!

    // Standard view-binding
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        // Ensuring the rounded edges
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddLandmarkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Setting up observers for the buttons
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val headerLabel = "Add new ${cityModel.name} landmark"

        binding.landmarkHeader.text = headerLabel

        binding.closeButton.setOnClickListener {
            dismiss()
        }
        binding.cancelButton.setOnClickListener {
            dismiss()
        }
        binding.createButton.setOnClickListener {
            dismiss()
            // Creating a new landmark model with the info
            val name = binding.inputName.text.toString()
            val description = binding.inputDescription.text.toString()
            val landMarkModel = LandmarkModel(-1, name, description, cityModel.id)
            // Executing the callback
            createNewLandmark(landMarkModel)
        }
    }
}
