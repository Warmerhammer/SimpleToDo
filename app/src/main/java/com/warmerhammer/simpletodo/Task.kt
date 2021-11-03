package com.warmerhammer.simpletodo

import java.util.*

data class Task(
    var title: String,
    var date: String = "TBD",
    var priority: Int = 2
)