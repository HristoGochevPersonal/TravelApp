package com.example.travelapp.presentation.listCities

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.travelapp.databinding.CityPreviewBinding

// The cities recycler view adapter
// Requires 2 interactions, for single tap and long press
class CitiesAdapter(
    private val onSingleTap: Interaction? = null,
    private val onLongPress: Interaction? = null
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // Item difference callback for checking the differences in a newly inserted list and an old one
    private val diffCallback = object : DiffUtil.ItemCallback<CityModel>() {
        override fun areItemsTheSame(oldItem: CityModel, newItem: CityModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CityModel, newItem: CityModel): Boolean {
            return when {
                oldItem.id != newItem.id -> {
                    false
                }
                oldItem.name != newItem.name -> {
                    false
                }
                oldItem.description != newItem.description -> {
                    false
                }
                else -> true
            }
        }
    }

    // Allows for the execution of the item difference callback on a background thread
    private val differ = AsyncListDiffer(this, diffCallback)

    // Standard adapter binding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = CityPreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CityViewHolder(binding, onSingleTap, onLongPress)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CityViewHolder -> {
                holder.bind(differ.currentList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<CityModel>) {
        differ.submitList(list)
    }

    // Cities recycler view item view holder
    class CityViewHolder constructor(
        private var binding: CityPreviewBinding,
        private val onSinglePress: Interaction?,
        private val onLongPress: Interaction?
    ) :
        RecyclerView.ViewHolder(binding.root) {

        // Contains a model it should be associated it
        private lateinit var cityModel: CityModel


        // Function for binding
        fun bind(cityModel: CityModel) {
            this.cityModel = cityModel
            binding.nameTextView.text = cityModel.name
            binding.descriptionTextView.text = cityModel.description
            binding.root.setOnClickListener {
                onSinglePress?.onItemSelected(adapterPosition, cityModel)
            }
            binding.root.setOnLongClickListener {
                onLongPress?.onItemSelected(adapterPosition, cityModel)
                true
            }
        }
    }

    // Interaction for the touch actions
    interface Interaction {
        fun onItemSelected(position: Int, item: CityModel)
    }
}