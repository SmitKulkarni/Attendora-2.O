package com.example.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.MockDataProvider
import com.example.db.converter.Converters
import com.example.db.dao.AttendanceDao
import com.example.db.dao.ClassDao
import com.example.db.dao.StudentDao
import com.example.db.dao.SyncQueueDao
import com.example.db.entity.AttendanceRecordEntity
import com.example.db.entity.AttendanceSessionEntity
import com.example.db.entity.ClassGroupEntity
import com.example.db.entity.StudentEntity
import com.example.db.entity.SyncQueueEntity
import com.example.db.mapper.EntityMappers.toEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        StudentEntity::class,
        ClassGroupEntity::class,
        AttendanceSessionEntity::class,
        AttendanceRecordEntity::class,
        SyncQueueEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AttendoraDatabase : RoomDatabase() {

    abstract fun studentDao(): StudentDao
    abstract fun classDao(): ClassDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun syncQueueDao(): SyncQueueDao

    companion object {
        @Volatile
        private var INSTANCE: AttendoraDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)): AttendoraDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AttendoraDatabase::class.java,
                    "attendora_database.db"
                )
                    .addCallback(AttendoraDatabaseCallback(scope))
                    .fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class AttendoraDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialDatabase(database)
                    }
                }
            }
        }

        suspend fun populateInitialDatabase(database: AttendoraDatabase) {
            val studentDao = database.studentDao()
            val classDao = database.classDao()

            if (studentDao.getCount() == 0) {
                val initialStudents = MockDataProvider.getInitialStudents().map { it.toEntity() }
                studentDao.insertStudents(initialStudents)
            }

            if (classDao.getCount() == 0) {
                val initialClasses = MockDataProvider.getInitialClasses().map { it.toEntity() }
                classDao.insertClasses(initialClasses)
            }
        }
    }
}

