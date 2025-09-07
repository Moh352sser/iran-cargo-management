package com.irancargocompany.logistics.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.irancargocompany.logistics.R
import com.irancargocompany.logistics.databinding.ItemTripBinding
import com.irancargocompany.logistics.model.Trip
import com.irancargocompany.logistics.model.TripStatus
import java.text.SimpleDateFormat
import java.util.*

class TripAdapter(
    private val onTripClick: (Trip) -> Unit
) : ListAdapter<Trip, TripAdapter.TripViewHolder>(TripDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val binding = ItemTripBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TripViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class TripViewHolder(
        private val binding: ItemTripBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        
        fun bind(trip: Trip) {
            binding.apply {
                tripIdText.text = trip.id
                originText.text = trip.origin
                destinationText.text = trip.destination
                cargoTypeText.text = trip.cargoType
                cargoWeightText.text = "${trip.cargoWeight} ${root.context.getString(R.string.kg)}"
                driverNameText.text = trip.driverName
                vehicleNumberText.text = trip.vehicleNumber
                departureTimeText.text = dateFormat.format(Date(trip.departureTime))
                
                // Set status with appropriate color
                statusText.text = when (trip.status) {
                    TripStatus.PENDING -> root.context.getString(R.string.status_pending)
                    TripStatus.IN_PROGRESS -> root.context.getString(R.string.status_in_progress)
                    TripStatus.COMPLETED -> root.context.getString(R.string.status_completed)
                    TripStatus.CANCELLED -> root.context.getString(R.string.status_cancelled)
                }
                
                val statusColor = when (trip.status) {
                    TripStatus.PENDING -> ContextCompat.getColor(root.context, R.color.status_pending)
                    TripStatus.IN_PROGRESS -> ContextCompat.getColor(root.context, R.color.status_in_progress)
                    TripStatus.COMPLETED -> ContextCompat.getColor(root.context, R.color.status_completed)
                    TripStatus.CANCELLED -> ContextCompat.getColor(root.context, R.color.status_cancelled)
                }
                
                statusText.setTextColor(statusColor)
                statusIndicator.setBackgroundColor(statusColor)
                
                // Set click listener
                root.setOnClickListener {
                    onTripClick(trip)
                }
            }
        }
    }
    
    private class TripDiffCallback : DiffUtil.ItemCallback<Trip>() {
        override fun areItemsTheSame(oldItem: Trip, newItem: Trip): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: Trip, newItem: Trip): Boolean {
            return oldItem == newItem
        }
    }
}