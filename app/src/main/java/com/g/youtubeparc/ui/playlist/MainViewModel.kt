package com.g.youtubeparc.ui.playlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.g.youtubeparc.model.PlayListModel
import com.g.youtubeparc.repository.MainRepository

class MainViewModel: ViewModel() {

    fun getPlaylistData(): LiveData<PlayListModel> {
        return MainRepository.fetchYoutubePlaylistData()
    }
}
