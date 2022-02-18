package com.example.travelapp.presentation.listCities

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.travelapp.databinding.DialogAddCityBinding

// Add city dialog with a callback that executes whenever the user creates a new city
class AddCityDialog(private val createNewCity: (item: CityModel) -> Unit) : DialogFragment() {
    private var _binding: DialogAddCityBinding? = null
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
        _binding = DialogAddCityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Setting up observers for the buttons
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.closeButton.setOnClickListener {
            dismiss()
        }
        binding.cancelButton.setOnClickListener {
            dismiss()
        }
        binding.createButton.setOnClickListener {
            dismiss()
            // Creating a new city model with the info
            val name = binding.inputName.text.toString()
            val description = binding.inputDescription.text.toString()
            val cityModel = CityModel(-1, name, description)
            // Executing the callback
            createNewCity(cityModel)
        }
    }
}
