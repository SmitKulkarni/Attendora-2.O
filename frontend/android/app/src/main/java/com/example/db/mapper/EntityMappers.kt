package com.example.db.mapper

import com.example.db.converter.Converters
import com.example.db.entity.ClassGroupEntity
import com.example.db.entity.StudentEntity
import com.example.model.ClassGroup
import com.example.model.Student

object EntityMappers {

    private val converters = Converters()

    fun StudentEntity.toDomain(): Student {
        return Student(
            id = id,
            name = name,
            rollNo = rollNo,
            className = className,
            status = status,
            checkInTime = checkInTime,
            avatarBgColor = avatarBgColor,
            department = department,
            email = email,
            overallPercentage = overallPercentage,
            subjects = converters.toSubjectList(subjectsJson)
        )
    }

    fun Student.toEntity(): StudentEntity {
        return StudentEntity(
            id = id,
            name = name,
            rollNo = rollNo,
            className = className,
            status = status,
            checkInTime = checkInTime,
            avatarBgColor = avatarBgColor,
            department = department,
            email = email,
            overallPercentage = overallPercentage,
            subjectsJson = converters.fromSubjectList(subjects)
        )
    }

    fun ClassGroupEntity.toDomain(): ClassGroup {
        return ClassGroup(
            id = id,
            name = name,
            code = code,
            department = department,
            teacherName = teacherName,
            enrolledCount = enrolledCount,
            scheduleTime = scheduleTime,
            courseName = courseName,
            academicTerm = academicTerm,
            roomLocation = roomLocation
        )
    }

    fun ClassGroup.toEntity(): ClassGroupEntity {
        return ClassGroupEntity(
            id = id,
            name = name,
            code = code,
            department = department,
            teacherName = teacherName,
            enrolledCount = enrolledCount,
            scheduleTime = scheduleTime,
            courseName = courseName,
            academicTerm = academicTerm,
            roomLocation = roomLocation
        )
    }
}

