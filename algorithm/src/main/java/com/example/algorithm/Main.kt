package com.example.algorithm

fun main() {
    val staff = arrayOf(
        arrayOf("Иван Иванов", "993"),
        arrayOf("Пётр Петров", "994"),
        arrayOf("Алексей Сидоров", "995"),
        arrayOf("Дмитрий Смирнов", "999"),
        arrayOf("Сергей Кузнецов", "1000"),
        arrayOf("Никита Попов", "1001"),
        arrayOf("Андрей Васильев", "1993"),
        arrayOf("Михаил Новиков", "1994"),
        arrayOf("Владимир Фёдоров", "1995"),
        arrayOf("Егор Морозов", "1996"),
        arrayOf("Максим Волков", "2000"),
        arrayOf("Роман Алексеев", "2001"),
        arrayOf("Артём Лебедев", "2002"),
        arrayOf("Кирилл Семёнов", "2005"),
        arrayOf("Олег Егоров", "2006"),
        arrayOf("Иван Петров", "2007"),
        arrayOf("Пётр Иванов", "2008")
    )

    val staffEmployee = staff.map { item ->
        Employee(
            item[0],
            item[1].toInt()
        )
    }.toTypedArray()

    val outputStuff = StaffChoosing.choosingStaffAlgorithm(staffEmployee)

    outputStuff.forEach { day ->
        day?.let {
            val date = it[0]

            if (it.size == 1) {
                println(date)
            } else {
                println("$date ${it[1]}")
                for (i in 2 until it.size) {
                    println("      ${it[i]}")
                }
            }
        }
    }
}