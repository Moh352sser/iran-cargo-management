package com.irancargocompany.logistics.data.repository

import androidx.lifecycle.LiveData
import com.irancargocompany.logistics.data.database.UserDao
import com.irancargocompany.logistics.model.User
import com.irancargocompany.logistics.model.UserType

class UserRepository(private val userDao: UserDao) {
    
    fun getAllUsers(): LiveData<List<User>> = userDao.getAllUsers()
    
    fun getUsersByType(userType: UserType): LiveData<List<User>> = 
        userDao.getUsersByType(userType)
    
    suspend fun getUserById(userId: String): User? = 
        userDao.getUserById(userId)
    
    suspend fun getUserByAccessCode(accessCode: String): User? = 
        userDao.getUserByAccessCode(accessCode)
    
    suspend fun insertUser(user: User): Long = 
        userDao.insertUser(user)
    
    suspend fun updateUser(user: User) = 
        userDao.updateUser(user)
    
    suspend fun deleteUser(user: User) = 
        userDao.deleteUser(user)
    
    suspend fun updateLastLogin(userId: String, lastLogin: Long) = 
        userDao.updateLastLogin(userId, lastLogin)
    
    suspend fun updateUserStatus(userId: String, isActive: Boolean) = 
        userDao.updateUserStatus(userId, isActive)
    
    suspend fun getActiveUserCountByType(userType: UserType): Int = 
        userDao.getActiveUserCountByType(userType)
    
    suspend fun authenticateUser(accessCode: String): User? {
        val user = getUserByAccessCode(accessCode)
        if (user != null) {
            updateLastLogin(user.id, System.currentTimeMillis())
        }
        return user
    }
}