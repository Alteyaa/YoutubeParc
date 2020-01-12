package com.g.youtubeparc.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.g.youtubeparc.model.DetailPlaylistModel
import com.g.youtubeparc.model.PlaylistModel


@Database(entities = [
    PlaylistModel::class,
    DetailPlaylistModel::class],
    version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ytVideoDao(): YoutubeDao
}