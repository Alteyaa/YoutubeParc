package com.g.youtubeparc.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.g.youtubeparc.type_converters.PlaylistModelTypeConverter
import com.google.gson.annotations.SerializedName

@Entity(tableName = "detail_playlist")
@TypeConverters(PlaylistModelTypeConverter::class)
data class DetailPlaylistModel(
    @SerializedName("kind")
    val kind: String,
    @SerializedName("pageInfo")
    val pageInfo: PageInfo,
    @SerializedName("etag")
    @PrimaryKey
    val etag: String,
    @SerializedName("items")
    val items: List<ItemsItem>

)