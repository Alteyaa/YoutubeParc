package com.g.youtubeparc.ui.detail_video

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.util.SparseArray
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YtFile
import com.g.youtubeparc.R
import com.g.youtubeparc.adapter.DetailPlaylistAdapter
import com.g.youtubeparc.adapter.DownloadDialogAdapter
import com.g.youtubeparc.model.DetailVideoModel
import com.g.youtubeparc.model.YtVideo
import com.g.youtubeparc.utils.CallBacks
import com.g.youtubeparc.utils.DownloadMaster
import com.g.youtubeparc.utils.PlayerManager
import com.google.android.exoplayer2.Player
import kotlinx.android.synthetic.main.activity_detail_playlist.tv_description
import kotlinx.android.synthetic.main.activity_detail_playlist.tv_title
import kotlinx.android.synthetic.main.activity_detail_video.*


class DetailVideoActivity : AppCompatActivity(), CallBacks.playerCallBack {


    private var viewModel: DetailVideoViewModel? = null
    private var adapter: DetailPlaylistAdapter? = null

    private var videoId: String? = null
    private var playlistId: String? = null
    private var writePermission = false
    private var selectedVideoQuality: String? = null
    private var selectedVideoExt: String? = null
    private var fileVideo: YtVideo? = null
    private var fileName: String? = null

    private val ITAG_FOR_AUDIO = 140

    private lateinit var player: Player
    private lateinit var playerManager: PlayerManager

    private lateinit var dialogDownloadButton: Button
    private lateinit var dialogRecyclerView: RecyclerView

    private lateinit var dialogAdapter: DownloadDialogAdapter

    private var formatsToShowList: MutableList<YtVideo>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_video)
        viewModel = ViewModelProviders.of(this).get(DetailVideoViewModel::class.java)
        formatsToShowList = ArrayList()
        playerManager = PlayerManager.getSharedInstance(this)
        player = playerManager.playerView.player
        setupViews()
        getExtra()
        fetchDetailVideo()
    }

    private fun setupViews() {
        btn_download.setOnClickListener {
            checkRequestPermission()
            showDownloadDialog()
        }
    }

    private fun showDownloadDialog() {
        val builder = AlertDialog.Builder(this, R.style.DownloadDialog)

        val view = layoutInflater.inflate(R.layout.alert_download_dialog, null)
        builder.setView(view)
        dialogDownloadButton = view.findViewById(R.id.btn_alert_download)
        dialogRecyclerView = view.findViewById(R.id.alert_recycler_view)

        initDialogAdapter()
        dialogAdapter.updateData(formatsToShowList)
        val alert = builder.create()
        alert.show()
        downloadAction(alert)

    }

    private fun downloadAction(builder: AlertDialog) {
        dialogDownloadButton.setOnClickListener {
            var downloadName = fileName!!
            downloadName = downloadName.replace("[\\\\><\"|*?%:#/]".toRegex(), "")
            var downloadIds = ""

            try {
                if (fileVideo?.videoFile != null) {
                    downloadIds += DownloadMaster().downloadFile(
                        this,
                        fileVideo?.videoFile!!.url,
                        downloadName + "." + fileVideo?.videoFile!!.format.ext,
                        downloadName + "." + fileVideo?.videoFile!!.format.ext

                    )
                    downloadIds += "-"
                }
                if (fileVideo?.audioFile != null) {
                    downloadIds += DownloadMaster().downloadFile(
                        this,
                        fileVideo?.videoFile!!.url,
                        downloadName + "." + fileVideo?.videoFile!!.format.ext,
                        downloadName + "." + fileVideo?.videoFile!!.format.ext
                    )
                }

            } catch (e: Exception) {

            }
            builder.dismiss()
        }
    }


    private fun initDialogAdapter() {
        dialogAdapter = DownloadDialogAdapter { item: YtVideo -> downloadClickItem(item)}
        dialogRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
        dialogRecyclerView.adapter = dialogAdapter
    }

    private fun downloadClickItem(item: YtVideo) {
        selectedVideoQuality = item.videoFile?.url
        selectedVideoExt = item.videoFile?.format?.ext
        fileVideo = item

    }


    private fun checkRequestPermission() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),1024)
        }else{
            writePermission = true
        }
    }

    private fun getExtra() {
        videoId = intent?.getStringExtra("videoId")
        playlistId = intent?.getStringExtra("playlistId")
    }

    private fun fetchDetailVideo() {
        val data = videoId?.let { viewModel?.getVideoData(it) }
        data?.observe(this, Observer<DetailVideoModel> {
            val model: DetailVideoModel? = data.value
            when {
                model != null -> {
                    setData(model)
                }
            }
        })
    }

    private fun setData(model: DetailVideoModel) {
        tv_title.text = model.items?.get(0)?.snippet?.title
        tv_description.text = model.items?.get(0)?.snippet?.description
        val link = model.items?.get(0)?.id.toString()
        actualLink(link)

    }

    @SuppressLint("StaticFieldLeak")
    private fun actualLink(link: String) {
        object : YouTubeExtractor(this) {
            public override fun onExtractionComplete(
                ytFiles: SparseArray<YtFile>?,
                vMeta: VideoMeta
            ) {

                formatsToShowList = java.util.ArrayList<YtVideo>()
                var i = 0
                var itag: Int
                if (ytFiles != null) {
                    while (i < ytFiles.size()) {
                        itag = ytFiles.keyAt(i)
                        val ytFile = ytFiles.get(itag)

                        if (ytFile.format.height == -1 || ytFile.format.height >= 360) {
                            addFormatToList(ytFile, ytFiles)
                        }
                        i++
                    }
                }
                (formatsToShowList as java.util.ArrayList<YtVideo>).sortWith(Comparator { lhs, rhs ->
                    lhs.height - rhs.height
                })

                val yotutubeUrl =
                    (formatsToShowList as java.util.ArrayList<YtVideo>)[(formatsToShowList as ArrayList<YtVideo>).lastIndex -1 ]
                yotutubeUrl.videoFile?.url?.let { playVideo(it) }
            }
        }.extract(link, true, true)
    }


    private fun addFormatToList(ytFile: YtFile, ytFiles: SparseArray<YtFile>) {
        val height = ytFile.format.height
        if (height != -1) {
            for (frVideo in this.formatsToShowList!!) {
                if (frVideo.height == height && (frVideo.videoFile == null || frVideo.videoFile!!.format.fps == ytFile.format.fps)) {
                    return
                }
            }
        }
        val frVideo = YtVideo()
        frVideo.height = height
        if (ytFile.format.isDashContainer) {
            if (height > 0) {
                frVideo.videoFile = ytFile
                frVideo.audioFile = ytFiles.get(ITAG_FOR_AUDIO)
            } else {
                frVideo.audioFile = ytFile
            }
        } else {
            frVideo.videoFile = ytFile
        }
        formatsToShowList!!.add(frVideo)
    }

    private fun playVideo(url: String) {
        player_view?.player = player
        PlayerManager.getSharedInstance(this).playStream(url)
        PlayerManager.getSharedInstance(this).setPlayerListener(this)
    }

    override fun onPlayingEnd() {
    }

    override fun onItemClickOnItem(albumId: Int) {

    }
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig != null) {
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                player_view.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                player_view.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT

            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                player_view.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                player_view.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT

            }
        }
    }
}