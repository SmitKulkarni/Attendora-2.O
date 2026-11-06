package com.example

import com.example.data.MockDataProvider
import com.example.db.converter.Converters
import com.example.db.mapper.EntityMappers.toDomain
import com.example.db.mapper.EntityMappers.toEntity
import com.example.model.AttendanceStatus
import com.example.model.SubjectAttendance
import com.example.viewmodel.AttendoraViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class Phase2PersistenceUnitTest {

    private val converters = Converters()

    @Test
    fun testAttendanceStatusConverter() {
        val presentStr = converters.fromAttendanceStatus(AttendanceStatus.PRESENT)
        assertEquals("PRESENT", presentStr)
        assertEquals(AttendanceStatus.PRESENT, converters.toAttendanceStatus(presentStr))

        val absentStr = converters.fromAttendanceStatus(AttendanceStatus.ABSENT)
        assertEquals("ABSENT", absentStr)
        assertEquals(AttendanceStatus.ABSENT, converters.toAttendanceStatus(absentStr))

        val lateStr = converters.fromAttendanceStatus(AttendanceStatus.LATE)
        assertEquals("LATE", lateStr)
        assertEquals(AttendanceStatus.LATE, converters.toAttendanceStatus(lateStr))
    }

    @Test
    fun testSubjectListJsonConverter() {
        val originalSubjects = listOf(
            SubjectAttendance("Artificial Intelligence", "CS-501", 34, 40, 85.0f),
            SubjectAttendance("Cloud Computing", "CS-502", 36, 40, 90.0f)
        )

        val jsonString = converters.fromSubjectList(originalSubjects)
        assertNotNull(jsonString)
        assertTrue(jsonString.contains("Artificial Intelligence"))
        assertTrue(jsonString.contains("CS-501"))

        val deserialized = converters.toSubjectList(jsonString)
        assertEquals(2, deserialized.size)
        assertEquals("Artificial Intelligence", deserialized[0].name)
        assertEquals("CS-501", deserialized[0].code)
        assertEquals(34, deserialized[0].attended)
        assertEquals(40, deserialized[0].total)
        assertEquals(85.0f, deserialized[0].percentage, 0.01f)
    }

    @Test
    fun testStudentEntityMapping() {
        val initialStudents = MockDataProvider.getInitialStudents()
        val originalStudent = initialStudents.first()

        val entity = originalStudent.toEntity()
        assertEquals(originalStudent.id, entity.id)
        assertEquals(originalStudent.name, entity.name)
        assertEquals(originalStudent.rollNo, entity.rollNo)
        assertEquals(originalStudent.status, entity.status)

        val domainStudent = entity.toDomain()
        assertEquals(originalStudent.id, domainStudent.id)
        assertEquals(originalStudent.name, domainStudent.name)
        assertEquals(originalStudent.subjects.size, domainStudent.subjects.size)
    }

    @Test
    fun testClassGroupEntityMapping() {
        val initialClasses = MockDataProvider.getInitialClasses()
        val originalClass = initialClasses.first()

        val entity = originalClass.toEntity()
        assertEquals(originalClass.id, entity.id)
        assertEquals(originalClass.name, entity.name)
        assertEquals(originalClass.code, entity.code)

        val domainClass = entity.toDomain()
        assertEquals(originalClass.id, domainClass.id)
        assertEquals(originalClass.teacherName, domainClass.teacherName)
    }

    @Test
    fun testViewModelFallbackMode() {
        val viewModel = AttendoraViewModel()
        assertNotNull(viewModel.students.value)
        assertTrue(viewModel.students.value.isNotEmpty())

        val initialCount = viewModel.students.value.size
        viewModel.addNewStudent(
            name = "Test Student",
            rollNo = "2026999",
            className = "Class 1",
            department = "Computer Science",
            email = "test@attendora.edu"
        )

        assertEquals(initialCount + 1, viewModel.students.value.size)
        val added = viewModel.students.value.first()
        assertEquals("Test Student", added.name)
        assertEquals("2026999", added.rollNo)
    }
}

