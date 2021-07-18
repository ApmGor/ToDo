package ru.apmgor.todo.repo

import androidx.room.TypeConverter
import java.time.Instant

class TypeTransmogrifier {
    @TypeConverter
    fun fromInstant(date: Instant?): Long? = date?.toEpochMilli()

    @TypeConverter
    fun toInstant(millisSinceEpoch: Long?): Instant? =
        millisSinceEpoch?.let { Instant.ofEpochMilli(it) }
}