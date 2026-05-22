package com.example.algorithm

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import kotlin.collections.forEachIndexed

object StaffChoosing {
    fun choosingStaffAlgorithm(
        employees: Array<Employee>,
        today: LocalDate = LocalDate.now(),
        step: Int = 1000,
        jubileeStartWith: Int = 1000,
    ): Array<Array<String>?> {
        val previousMonday = today.with(TemporalAdjusters.previous(DayOfWeek.MONDAY))
        val output: Array<Array<String>?> = arrayOfNulls(7)

        output.forEachIndexed { index, _ ->
            val date = previousMonday.plusDays(index.toLong())
            val diffDays = previousMonday.until(date, ChronoUnit.DAYS)
            val employeesJubilee = employees.filter { employee ->
                (employee.days + diffDays) >= jubileeStartWith &&
                        (employee.days + diffDays) % step == 0L
            }.map { item ->
                Employee(
                    name = item.name,
                    days = (item.days + diffDays).toInt()
                )
            }

            output[index] = arrayOf(
                date.format(DateTimeFormatter.ofPattern("dd.MM")),
                *employeesJubilee.map {
                    "${it.name} – ${it.days} дней"
                }.toTypedArray()
            )
        }

        return output
    }
}

data class Employee(
    val name: String,
    val days: Int
)

//[Employee("Иван Петрович", 1001), Employee("Иван Иванович", 2002), Employee("Степан Андреевич", 989),]



