package com.g.youtubeparc.ui.playlist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.g.youtubeparc.R
import com.g.youtubeparc.adapter.PlaylistAdapter
import com.g.youtubeparc.model.ItemsItem
import com.g.youtubeparc.model.PlayListModel
import com.g.youtubeparc.ui.detail_playlist.DetailPlaylistActivity
import kotlinx.android.synthetic.main.activity_detail_playlist.*

class MainActivity : AppCompatActivity() {

    private var viewModel: MainViewModel? = null
    private var adapter: PlaylistAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        initAdapter()
        fetchPlaylist()
    }

    private fun initAdapter() {
        recycler_view.layoutManager = LinearLayoutManager(this)
        adapter = PlaylistAdapter ()
        { item: ItemsItem -> clickItem(item) }
        recycler_view.adapter = adapter

    }

    private fun clickItem(item: ItemsItem) {
        val intent = Intent(this, DetailPlaylistActivity::class.java)
        intent.putExtra("id", item.id)
        intent.putExtra("title", item.snippet.title)
        intent.putExtra("channelTitle", item.snippet.channelId)
        intent.putExtra("etag", item.etag)
        startActivity(intent)
    }

    private fun fetchPlaylist() {
        //TODO check internet
        val data = viewModel?.getPlaylistData()
        data?.observe(this, Observer<PlayListModel> {
            val model: PlayListModel? = data.value
            when {
                model != null -> {
                    updateAdapterData(model)
                }
            }

        })
    }

    private fun updateAdapterData(list: PlayListModel?) {
        val data = list?.items
        adapter?.updateData(data)
    }
}
