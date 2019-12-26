package com.g.youtubeparc.ui.detail_video

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.g.youtubeparc.model.DetailVideoModel
import com.g.youtubeparc.repository.MainRepository

class DetailVideoViewModel : ViewModel() {

    fun getVideoData(id: String) : LiveData<DetailVideoModel>? {
        return MainRepository.fetchVideoData(id)
    }
}