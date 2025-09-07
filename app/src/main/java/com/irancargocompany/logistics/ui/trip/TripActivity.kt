package com.irancargocompany.logistics.ui.trip

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.irancargocompany.logistics.R
import com.irancargocompany.logistics.data.database.CargoDatabase
import com.irancargocompany.logistics.data.repository.TripRepository
import com.irancargocompany.logistics.databinding.ActivityTripBinding
import com.irancargocompany.logistics.model.Trip
import com.irancargocompany.logistics.model.TripStatus
import com.irancargocompany.logistics.utils.SessionManager
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class TripActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityTripBinding
    private lateinit var tripRepository: TripRepository
    private lateinit var sessionManager: SessionManager
    
    private var currentTrip: Trip? = null
    private var selectedDepartureTime: Long = 0L
    private var selectedArrivalTime: Long = 0L
    
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTripBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupDatabase()
        setupViews()
        
        // Check if editing existing trip
        currentTrip = intent.getParcelableExtra("trip")
        if (currentTrip != null) {
            populateFields(currentTrip!!)
            supportActionBar?.title = getString(R.string.trip_details)
        } else {
            supportActionBar?.title = getString(R.string.create_trip)
        }
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    
    private fun setupDatabase() {
        val database = CargoDatabase.getDatabase(this)
        tripRepository = TripRepository(database.tripDao())
        sessionManager = SessionManager(this)
    }
    
    private fun setupViews() {
        val currentUser = sessionManager.getCurrentUser()
        
        // Pre-fill driver name if current user is a driver
        if (currentUser?.userType == com.irancargocompany.logistics.model.UserType.DRIVER) {
            binding.driverNameEditText.setText(currentUser.name ?: "")
        }
        
        // Setup date and time pickers
        binding.departureTimeButton.setOnClickListener {
            showDateTimePicker { timestamp ->
                selectedDepartureTime = timestamp
                binding.departureTimeText.text = formatDateTime(timestamp)
            }
        }
        
        binding.arrivalTimeButton.setOnClickListener {
            showDateTimePicker { timestamp ->
                selectedArrivalTime = timestamp
                binding.arrivalTimeText.text = formatDateTime(timestamp)
            }
        }
        
        // Setup save button
        binding.saveButton.setOnClickListener {
            if (validateForm()) {
                saveTrip()
            }
        }
        
        // Setup status spinner
        setupStatusSpinner()
    }
    
    private fun setupStatusSpinner() {
        // Status spinner setup would go here
        // For now, we'll use a simple approach
    }
    
    private fun populateFields(trip: Trip) {
        binding.apply {
            originEditText.setText(trip.origin)
            destinationEditText.setText(trip.destination)
            cargoTypeEditText.setText(trip.cargoType)
            cargoWeightEditText.setText(trip.cargoWeight.toString())
            driverNameEditText.setText(trip.driverName)
            vehicleNumberEditText.setText(trip.vehicleNumber)
            notesEditText.setText(trip.notes)
            
            selectedDepartureTime = trip.departureTime
            departureTimeText.text = formatDateTime(trip.departureTime)
            
            trip.arrivalTime?.let {
                selectedArrivalTime = it
                arrivalTimeText.text = formatDateTime(it)
            }
        }
    }
    
    private fun validateForm(): Boolean {
        var isValid = true
        
        if (binding.originEditText.text.toString().trim().isEmpty()) {
            binding.originInputLayout.error = getString(R.string.error)
            isValid = false
        } else {
            binding.originInputLayout.error = null
        }
        
        if (binding.destinationEditText.text.toString().trim().isEmpty()) {
            binding.destinationInputLayout.error = getString(R.string.error)
            isValid = false
        } else {
            binding.destinationInputLayout.error = null
        }
        
        if (binding.cargoTypeEditText.text.toString().trim().isEmpty()) {
            binding.cargoTypeInputLayout.error = getString(R.string.error)
            isValid = false
        } else {
            binding.cargoTypeInputLayout.error = null
        }
        
        if (binding.driverNameEditText.text.toString().trim().isEmpty()) {
            binding.driverNameInputLayout.error = getString(R.string.error)
            isValid = false
        } else {
            binding.driverNameInputLayout.error = null
        }
        
        if (selectedDepartureTime == 0L) {
            Toast.makeText(this, "Please select departure time", Toast.LENGTH_SHORT).show()
            isValid = false
        }
        
        return isValid
    }
    
    private fun saveTrip() {
        val currentUser = sessionManager.getCurrentUser() ?: return
        
        lifecycleScope.launch {
            try {
                val trip = if (currentTrip != null) {
                    currentTrip!!.copy(
                        origin = binding.originEditText.text.toString().trim(),
                        destination = binding.destinationEditText.text.toString().trim(),
                        cargoType = binding.cargoTypeEditText.text.toString().trim(),
                        cargoWeight = binding.cargoWeightEditText.text.toString().toDoubleOrNull() ?: 0.0,
                        driverName = binding.driverNameEditText.text.toString().trim(),
                        vehicleNumber = binding.vehicleNumberEditText.text.toString().trim(),
                        departureTime = selectedDepartureTime,
                        arrivalTime = if (selectedArrivalTime > 0) selectedArrivalTime else null,
                        notes = binding.notesEditText.text.toString().trim().ifEmpty { null },
                        updatedAt = System.currentTimeMillis()
                    )
                } else {
                    Trip(
                        origin = binding.originEditText.text.toString().trim(),
                        destination = binding.destinationEditText.text.toString().trim(),
                        cargoType = binding.cargoTypeEditText.text.toString().trim(),
                        cargoWeight = binding.cargoWeightEditText.text.toString().toDoubleOrNull() ?: 0.0,
                        driverName = binding.driverNameEditText.text.toString().trim(),
                        vehicleNumber = binding.vehicleNumberEditText.text.toString().trim(),
                        departureTime = selectedDepartureTime,
                        arrivalTime = if (selectedArrivalTime > 0) selectedArrivalTime else null,
                        status = TripStatus.PENDING,
                        driverId = currentUser.id,
                        supervisorId = if (currentUser.userType == com.irancargocompany.logistics.model.UserType.SUPERVISOR) currentUser.id else null,
                        notes = binding.notesEditText.text.toString().trim().ifEmpty { null },
                        qrCode = UUID.randomUUID().toString()
                    )
                }
                
                if (currentTrip != null) {
                    tripRepository.updateTrip(trip)
                    Toast.makeText(this@TripActivity, getString(R.string.trip_updated), Toast.LENGTH_SHORT).show()
                } else {
                    tripRepository.insertTrip(trip)
                    Toast.makeText(this@TripActivity, getString(R.string.trip_created), Toast.LENGTH_SHORT).show()
                }
                
                finish()
                
            } catch (e: Exception) {
                Toast.makeText(this@TripActivity, getString(R.string.error), Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }
    
    private fun showDateTimePicker(onDateTimeSelected: (Long) -> Unit) {
        val calendar = Calendar.getInstance()
        
        val datePickerDialog = DatePickerDialog.newInstance(
            { _, year, monthOfYear, dayOfMonth ->
                calendar.set(year, monthOfYear, dayOfMonth)
                
                val timePickerDialog = TimePickerDialog.newInstance(
                    { _, hourOfDay, minute, _ ->
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)
                        onDateTimeSelected(calendar.timeInMillis)
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                )
                timePickerDialog.show(supportFragmentManager, "TimePickerDialog")
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        
        datePickerDialog.show(supportFragmentManager, "DatePickerDialog")
    }
    
    private fun formatDateTime(timestamp: Long): String {
        val date = Date(timestamp)
        return "${dateFormat.format(date)} ${timeFormat.format(date)}"
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}