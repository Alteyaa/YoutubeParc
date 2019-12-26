package com.g.youtubeparc.ui.detail_playlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.g.youtubeparc.model.DetailPlaylistModel
import com.g.youtubeparc.repository.MainRepository

class DetailPlaylistViewModel : ViewModel() {


    fun fetchDetailPlaylistData(id: String): LiveData<DetailPlaylistModel>? {
        return MainRepository.fetchYoutubeDetailPlaylistData(id)

    }
}