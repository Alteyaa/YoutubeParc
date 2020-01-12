package com.g.youtubeparc.ui.playlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.g.youtubeparc.model.PlaylistModel
import com.g.youtubeparc.repository.MainRepository
import com.g.youtubeparc.utils.App
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {


    val db = App().getInstance().getDatabase()
    fun getPlaylistData() : LiveData<PlaylistModel> {
        return MainRepository.fetchYoutubePlaylistData()
    }

    fun insertPlaylistData(model: PlaylistModel) {
        viewModelScope.launch {
            db.ytVideoDao()?.insertAllPlaylist(model)
        }
    }

    suspend fun getDataFromDB() : PlaylistModel{
        return db.ytVideoDao().getAllPlayList()
    }

}
