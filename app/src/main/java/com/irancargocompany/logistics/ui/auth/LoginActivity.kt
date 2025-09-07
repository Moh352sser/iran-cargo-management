package com.irancargocompany.logistics.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.irancargocompany.logistics.R
import com.irancargocompany.logistics.data.database.CargoDatabase
import com.irancargocompany.logistics.data.repository.UserRepository
import com.irancargocompany.logistics.databinding.ActivityLoginBinding
import com.irancargocompany.logistics.ui.main.MainActivity
import com.irancargocompany.logistics.utils.SessionManager
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityLoginBinding
    private lateinit var userRepository: UserRepository
    private lateinit var sessionManager: SessionManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupDatabase()
        setupViews()
    }
    
    private fun setupDatabase() {
        val database = CargoDatabase.getDatabase(this)
        userRepository = UserRepository(database.userDao())
        sessionManager = SessionManager(this)
    }
    
    private fun setupViews() {
        binding.loginButton.setOnClickListener {
            val accessCode = binding.accessCodeEditText.text.toString().trim()
            if (accessCode.isNotEmpty()) {
                performLogin(accessCode)
            } else {
                binding.accessCodeInputLayout.error = getString(R.string.access_code_hint)
            }
        }
        
        // Clear error when user starts typing
        binding.accessCodeEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.accessCodeInputLayout.error = null
            }
        }
    }
    
    private fun performLogin(accessCode: String) {
        binding.loginButton.isEnabled = false
        binding.accessCodeInputLayout.error = null
        
        lifecycleScope.launch {
            try {
                val user = userRepository.authenticateUser(accessCode)
                if (user != null) {
                    sessionManager.saveUserSession(user)
                    
                    // Show welcome message
                    val welcomeMessage = getString(R.string.welcome_message)
                    Toast.makeText(this@LoginActivity, welcomeMessage, Toast.LENGTH_SHORT).show()
                    
                    // Navigate to main activity
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    binding.accessCodeInputLayout.error = getString(R.string.invalid_access_code)
                }
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, getString(R.string.login_error), Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            } finally {
                binding.loginButton.isEnabled = true
            }
        }
    }
    
    override fun onBackPressed() {
        // Disable back button on login screen
        // User must login to continue
    }
}