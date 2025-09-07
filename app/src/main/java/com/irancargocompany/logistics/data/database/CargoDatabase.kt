package com.irancargocompany.logistics.data.database

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.irancargocompany.logistics.model.*

@Database(
    entities = [
        Trip::class,
        User::class,
        LocationTracking::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CargoDatabase : RoomDatabase() {
    
    abstract fun tripDao(): TripDao
    abstract fun userDao(): UserDao
    abstract fun locationTrackingDao(): LocationTrackingDao
    
    companion object {
        @Volatile
        private var INSTANCE: CargoDatabase? = null
        
        fun getDatabase(context: Context): CargoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CargoDatabase::class.java,
                    "cargo_database"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
        
        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Insert default users
                insertDefaultUsers(db)
            }
            
            private fun insertDefaultUsers(db: SupportSQLiteDatabase) {
                // Insert default driver
                db.execSQL("""
                    INSERT INTO users (id, accessCode, userType, name, lastLogin, isActive, createdAt)
                    VALUES ('DR001', 'DR001', 'DRIVER', 'راننده پیش‌فرض', NULL, 1, ${System.currentTimeMillis()})
                """)
                
                // Insert default supervisor
                db.execSQL("""
                    INSERT INTO users (id, accessCode, userType, name, lastLogin, isActive, createdAt)
                    VALUES ('SP001', 'SP001', 'SUPERVISOR', 'سرپرست پیش‌فرض', NULL, 1, ${System.currentTimeMillis()})
                """)
            }
        }
    }
}

class Converters {
    @TypeConverter
    fun fromTripStatus(status: TripStatus): String {
        return status.name
    }
    
    @TypeConverter
    fun toTripStatus(status: String): TripStatus {
        return TripStatus.valueOf(status)
    }
    
    @TypeConverter
    fun fromUserType(userType: UserType): String {
        return userType.name
    }
    
    @TypeConverter
    fun toUserType(userType: String): UserType {
        return UserType.valueOf(userType)
    }
}