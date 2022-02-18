package com.example.travelapp.presentation.cityWithLandmarks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.travelapp.databinding.LandmarkPreviewBinding

// The landmarks recycler view adapter
// Requires 2 interactions, for single tap and long press
class LandmarksAdapter(
    private val onSinglePress: Interaction? = null,
    private val onLongPress: Interaction? = null
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // Item difference callback for checking the differences in a newly inserted list and an old one
    private val diffCallback = object : DiffUtil.ItemCallback<LandmarkModel>() {
        override fun areItemsTheSame(oldItem: LandmarkModel, newItem: LandmarkModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: LandmarkModel, newItem: LandmarkModel): Boolean {
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
                oldItem.cityId != newItem.cityId -> {
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
        val binding = LandmarkPreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LandmarkViewHolder(binding, onSinglePress, onLongPress)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is LandmarkViewHolder -> {
                holder.bind(differ.currentList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<LandmarkModel>) {
        differ.submitList(list)
    }

    // Landmarks recycler view item view holder
    class LandmarkViewHolder constructor(
        private var binding: LandmarkPreviewBinding,
        private val onSinglePress: Interaction?,
        private val onLongPress: Interaction?
    ) :
        RecyclerView.ViewHolder(binding.root) {

        // Contains a model it should be associated it
        private lateinit var landmarkModel: LandmarkModel

        // Function for binding
        fun bind(landmarkModel: LandmarkModel) {
            this.landmarkModel = landmarkModel
            binding.landmarkNameTextView.text = landmarkModel.name
            binding.landmarkDescriptionTextView.text = landmarkModel.description
            binding.root.setOnClickListener {
                onSinglePress?.onItemSelected(adapterPosition, landmarkModel)
            }
            binding.root.setOnLongClickListener {
                onLongPress?.onItemSelected(adapterPosition, landmarkModel)
                true
            }
        }
    }

    // Interaction for the touch actions
    interface Interaction {
        fun onItemSelected(position: Int, item: LandmarkModel)
    }
}