package com.irancargocompany.logistics.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.irancargocompany.logistics.R
import com.irancargocompany.logistics.data.database.CargoDatabase
import com.irancargocompany.logistics.data.repository.TripRepository
import com.irancargocompany.logistics.databinding.ActivityMainBinding
import com.irancargocompany.logistics.model.TripStatus
import com.irancargocompany.logistics.model.UserType
import com.irancargocompany.logistics.ui.auth.LoginActivity
import com.irancargocompany.logistics.ui.scanner.QRScannerActivity
import com.irancargocompany.logistics.ui.trip.TripActivity
import com.irancargocompany.logistics.utils.SessionManager
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var tripRepository: TripRepository
    private lateinit var sessionManager: SessionManager
    private lateinit var tripAdapter: TripAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupDatabase()
        setupViews()
        setupRecyclerView()
        loadDashboardData()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.dashboard_title)
    }
    
    private fun setupDatabase() {
        val database = CargoDatabase.getDatabase(this)
        tripRepository = TripRepository(database.tripDao())
        sessionManager = SessionManager(this)
    }
    
    private fun setupViews() {
        val currentUser = sessionManager.getCurrentUser()
        
        binding.welcomeText.text = getString(R.string.welcome_message)
        binding.userTypeText.text = when (currentUser?.userType) {
            UserType.DRIVER -> getString(R.string.user_type_driver)
            UserType.SUPERVISOR -> getString(R.string.user_type_supervisor)
            UserType.MANAGER -> getString(R.string.user_type_manager)
            else -> ""
        }
        
        // Setup FAB for creating trips
        binding.createTripFab.setOnClickListener {
            val intent = Intent(this, TripActivity::class.java)
            startActivity(intent)
        }
        
        // Setup quick action buttons
        binding.scannerButton.setOnClickListener {
            val intent = Intent(this, QRScannerActivity::class.java)
            startActivity(intent)
        }
        
        binding.refreshButton.setOnClickListener {
            loadDashboardData()
        }
    }
    
    private fun setupRecyclerView() {
        tripAdapter = TripAdapter { trip ->
            val intent = Intent(this, TripActivity::class.java)
            intent.putExtra("trip", trip)
            startActivity(intent)
        }
        
        binding.tripsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = tripAdapter
        }
    }
    
    private fun loadDashboardData() {
        val currentUser = sessionManager.getCurrentUser() ?: return
        
        lifecycleScope.launch {
            try {
                // Load trip counts
                val pendingCount = tripRepository.getTripCountByStatus(TripStatus.PENDING)
                val activeCount = tripRepository.getTripCountByStatus(TripStatus.IN_PROGRESS)
                val completedCount = tripRepository.getTripCountByStatus(TripStatus.COMPLETED)
                
                // Update dashboard cards
                binding.pendingTripsCount.text = pendingCount.toString()
                binding.activeTripsCount.text = activeCount.toString()
                binding.completedTripsCount.text = completedCount.toString()
                
                // Load recent trips based on user type
                when (currentUser.userType) {
                    UserType.DRIVER -> {
                        tripRepository.getTripsByDriver(currentUser.id).observe(this@MainActivity) { trips ->
                            tripAdapter.submitList(trips.take(10)) // Show latest 10 trips
                        }
                    }
                    UserType.SUPERVISOR, UserType.MANAGER -> {
                        tripRepository.getAllTrips().observe(this@MainActivity) { trips ->
                            tripAdapter.submitList(trips.take(10)) // Show latest 10 trips
                        }
                    }
                }
                
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_trips -> {
                val intent = Intent(this, TripActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_scanner -> {
                val intent = Intent(this, QRScannerActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_logout -> {
                performLogout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    private fun performLogout() {
        sessionManager.clearSession()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
    
    override fun onResume() {
        super.onResume()
        loadDashboardData()
    }
}