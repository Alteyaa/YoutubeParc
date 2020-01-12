package com.g.youtubeparc.type_converters

import androidx.room.TypeConverter
import com.g.youtubeparc.model.ContentDetails
import com.g.youtubeparc.model.Snippet
import com.google.gson.Gson

class ItemsItemTypeConverter {
    var gson = Gson()
    @TypeConverter
    public fun toSnippet(data: Snippet): String = gson.toJson(data)
    @TypeConverter
    public fun fromSnippet(value: String): Snippet = gson.fromJson(value, Snippet::class.java)
    @TypeConverter
    public fun toContentDetails(data: ContentDetails): String = gson.toJson(data)
    @TypeConverter
    public fun fromContentDetails(value: String): ContentDetails = gson.fromJson(value, ContentDetails::class.java)

}