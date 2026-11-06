package com.example.db.converter

import androidx.room.TypeConverter
import com.example.model.AttendanceStatus
import com.example.model.SubjectAttendance
import org.json.JSONArray
import org.json.JSONObject
import java.util.Date

class Converters {

    @TypeConverter
    fun fromAttendanceStatus(status: AttendanceStatus?): String? {
        return status?.name
    }

    @TypeConverter
    fun toAttendanceStatus(value: String?): AttendanceStatus? {
        return value?.let {
            try {
                AttendanceStatus.valueOf(it)
            } catch (e: IllegalArgumentException) {
                AttendanceStatus.PRESENT
            }
        }
    }

    @TypeConverter
    fun fromSubjectList(subjects: List<SubjectAttendance>?): String {
        if (subjects.isNullOrEmpty()) return "[]"
        val jsonArray = JSONArray()
        for (subject in subjects) {
            val jsonObject = JSONObject()
            jsonObject.put("name", subject.name)
            jsonObject.put("code", subject.code)
            jsonObject.put("attended", subject.attended)
            jsonObject.put("total", subject.total)
            jsonObject.put("percentage", subject.percentage.toDouble())
            jsonArray.put(jsonObject)
        }
        return jsonArray.toString()
    }

    @TypeConverter
    fun toSubjectList(jsonStr: String?): List<SubjectAttendance> {
        if (jsonStr.isNullOrBlank()) return emptyList()
        val list = mutableListOf<SubjectAttendance>()
        try {
            val jsonArray = JSONArray(jsonStr)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    SubjectAttendance(
                        name = obj.optString("name", ""),
                        code = obj.optString("code", ""),
                        attended = obj.optInt("attended", 0),
                        total = obj.optInt("total", 0),
                        percentage = obj.optDouble("percentage", 0.0).toFloat()
                    )
                )
            }
        } catch (e: Exception) {
            // Return empty list on parse error
        }
        return list
    }

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}

