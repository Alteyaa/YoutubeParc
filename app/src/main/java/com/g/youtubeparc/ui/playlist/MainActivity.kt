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
import com.g.youtubeparc.model.PlaylistModel
import com.g.youtubeparc.ui.detail_playlist.DetailPlaylistActivity
import com.g.youtubeparc.utils.InternetHelper
import com.g.youtubeparc.utils.UiHelper
import com.g.youtubeparc.utils.gone
import com.g.youtubeparc.utils.visible
import kotlinx.android.synthetic.main.activity_detail_playlist.recycler_view
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {


    private var viewModel: MainViewModel? = null
    private var adapter: PlaylistAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        initAdapter()
//        fetchPlaylist()
        getDataFromDatabase()
        viewsActions()
    }


    private fun viewsActions() {
        btn_retry.setOnClickListener {
            fetchPlaylist()
        }
    }

    private fun initAdapter() {
        recycler_view.layoutManager = LinearLayoutManager(this)
        adapter = PlaylistAdapter() {item: ItemsItem -> clickItem(item)}
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
        if (InternetHelper().checkInternetConnection(this)) {
            network_container.gone()
            recycler_view.visible()
            val data = viewModel?.getPlaylistData()
            data?.observe(this, Observer<PlaylistModel> {
                val model: PlaylistModel? = data.value
                when {
                    model != null -> {
                        updateDatabasePlaylist(model)
                        updateAdapterData(model)
                    }
                }

            })
        } else {
            showEmptyState()
            UiHelper().showToast(this, getString(R.string.no_internet_connection))
        }
    }

    private fun showEmptyState() {
        network_container.visible()
        recycler_view.gone()
    }

    private fun updateAdapterData(model: PlaylistModel?) {
        val data = model?.items
        adapter?.updateData(data)
    }

    private fun updateDatabasePlaylist(model: PlaylistModel?) {
        model?.let { viewModel?.insertPlaylistData(it) }
    }

    private fun getDataFromDatabase() {
        CoroutineScope(Dispatchers.Main).launch {
            val model = viewModel?.getDataFromDB()
            if (model != null && !model.items.isNullOrEmpty()) {
                updateAdapterData(model)
                fetchNewPlaylistData()
            } else {
                fetchPlaylist()
            }
        }
    }

    private fun fetchNewPlaylistData() {
        val data = viewModel?.getPlaylistData()
        data?.observe(this, Observer<PlaylistModel> {
            val model: PlaylistModel? = data.value
            when {
                model != null -> {
                    updateDatabasePlaylist(model)
                    updateAdapterData(model)
                }
            }
        })
    }

}
