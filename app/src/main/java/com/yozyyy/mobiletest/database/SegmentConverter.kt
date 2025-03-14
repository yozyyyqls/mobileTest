package com.yozyyy.mobiletest.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yozyyy.mobiletest.models.Segment

class SegmentConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromSegmentList(segments: List<Segment>): String = gson.toJson(segments)

    @TypeConverter
    fun toSegmentList(json: String): List<Segment> {
        val listType = object : TypeToken<List<Segment>>() {}.type
        return gson.fromJson(json, listType)
    }
}