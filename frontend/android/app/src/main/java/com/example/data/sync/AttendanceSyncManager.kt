package com.example.data.sync

import com.example.data.repository.AttendanceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AttendanceSyncManager(
    private val repository: AttendanceRepository,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {

    fun triggerSync(onComplete: ((Int) -> Unit)? = null) {
        scope.launch {
            val count = repository.processSyncQueue()
            withContext(Dispatchers.Main) {
                onComplete?.invoke(count)
            }
        }
    }
}

