package com.g.youtubeparc.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.g.youtubeparc.model.DetailPlaylistModel
import com.g.youtubeparc.model.PlaylistModel


@Dao
interface YoutubeDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun  insertAllPlaylist(items: PlaylistModel)

    @Query("SELECT * FROM play_list")
    suspend fun getAllPlayList(): PlaylistModel

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDetailPlaylistData(items: DetailPlaylistModel)

    @Query("SELECT * FROM detail_playlist")
    suspend fun getDetailPlaylist(): List<DetailPlaylistModel>?

}