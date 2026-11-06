package com.example

import android.app.Application
import com.example.data.repository.AttendanceRepository
import com.example.data.sync.AttendanceSyncManager
import com.example.db.AttendoraDatabase
import com.example.network.AttendoraApiService
import com.example.network.NetworkClient

class AttendoraApplication : Application() {

    val database: AttendoraDatabase by lazy {
        AttendoraDatabase.getDatabase(this)
    }

    val apiService: AttendoraApiService by lazy {
        NetworkClient.createService()
    }

    val repository: AttendanceRepository by lazy {
        AttendanceRepository.getInstance(database, apiService)
    }

    val syncManager: AttendanceSyncManager by lazy {
        AttendanceSyncManager(repository)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: AttendoraApplication
            private set
    }
}

