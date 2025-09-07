package com.irancargocompany.logistics.ui.scanner

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.zxing.ResultPoint
import com.irancargocompany.logistics.R
import com.irancargocompany.logistics.data.database.CargoDatabase
import com.irancargocompany.logistics.data.repository.TripRepository
import com.irancargocompany.logistics.databinding.ActivityQrScannerBinding
import com.irancargocompany.logistics.ui.trip.TripActivity
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import kotlinx.coroutines.launch

class QRScannerActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityQrScannerBinding
    private lateinit var tripRepository: TripRepository
    
    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupDatabase()
        
        if (checkCameraPermission()) {
            startScanning()
        } else {
            requestCameraPermission()
        }
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.scanner_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    
    private fun setupDatabase() {
        val database = CargoDatabase.getDatabase(this)
        tripRepository = TripRepository(database.tripDao())
    }
    
    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }
    
    private fun startScanning() {
        binding.scannerView.decodeContinuous(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult?) {
                result?.let { barcodeResult ->
                    handleScannedResult(barcodeResult.text)
                }
            }
            
            override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {
                // Handle possible result points if needed
            }
        })
        
        binding.scannerView.resume()
    }
    
    private fun handleScannedResult(scannedText: String) {
        binding.scannerView.pause()
        
        lifecycleScope.launch {
            try {
                // Try to find trip by QR code
                val trip = tripRepository.getTripByQrCode(scannedText)
                
                if (trip != null) {
                    Toast.makeText(
                        this@QRScannerActivity,
                        getString(R.string.qr_code_scanned),
                        Toast.LENGTH_SHORT
                    ).show()
                    
                    // Navigate to trip details
                    val intent = Intent(this@QRScannerActivity, TripActivity::class.java)
                    intent.putExtra("trip", trip)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        this@QRScannerActivity,
                        getString(R.string.invalid_qr_code),
                        Toast.LENGTH_SHORT
                    ).show()
                    
                    // Resume scanning after a delay
                    binding.scannerView.postDelayed({
                        binding.scannerView.resume()
                    }, 2000)
                }
                
            } catch (e: Exception) {
                Toast.makeText(
                    this@QRScannerActivity,
                    getString(R.string.error),
                    Toast.LENGTH_SHORT
                ).show()
                e.printStackTrace()
                
                // Resume scanning
                binding.scannerView.resume()
            }
        }
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startScanning()
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.camera_permission_required),
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        if (checkCameraPermission()) {
            binding.scannerView.resume()
        }
    }
    
    override fun onPause() {
        super.onPause()
        binding.scannerView.pause()
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}