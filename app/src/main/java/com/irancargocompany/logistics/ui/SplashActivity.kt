package com.irancargocompany.logistics.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.irancargocompany.logistics.R
import com.irancargocompany.logistics.ui.auth.LoginActivity
import com.irancargocompany.logistics.ui.main.MainActivity
import com.irancargocompany.logistics.utils.SessionManager

class SplashActivity : AppCompatActivity() {
    
    private lateinit var sessionManager: SessionManager
    
    companion object {
        private const val SPLASH_DELAY = 2000L // 2 seconds
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        
        sessionManager = SessionManager(this)
        
        // Delay for splash screen
        Handler(Looper.getMainLooper()).postDelayed({
            checkUserSession()
        }, SPLASH_DELAY)
    }
    
    private fun checkUserSession() {
        val intent = if (sessionManager.isLoggedIn()) {
            Intent(this, MainActivity::class.java)
        } else {
            Intent(this, LoginActivity::class.java)
        }
        
        startActivity(intent)
        finish()
    }
}