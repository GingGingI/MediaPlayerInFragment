package com.example.ginggingi.mediaplayerinfragment.Fragments

import android.content.res.Configuration
import android.support.v4.app.Fragment
import android.os.Bundle
import android.view.*

import android.widget.*
import com.example.ginggingi.mediaplayerinfragment.Interfaces.FragmentCallback

import com.example.ginggingi.mediaplayerinfragment.R
import com.example.ginggingi.mediaplayerinfragment.Utils.EZMediaplayer
import kotlinx.android.synthetic.main.view_mediaplayer.*

class VideoFragment: Fragment(), View.OnClickListener{

    private lateinit var VideoView: RelativeLayout
    private lateinit var Addon: RelativeLayout
    private lateinit var SurfaceView: SurfaceView
    private lateinit var TitleView: TextView
    private lateinit var NowTime: TextView
    private lateinit var TimeDuration: TextView
    private lateinit var PlayBtn: ImageButton
    private lateinit var PinBtn: ImageButton
    private lateinit var ScreenChangeBtn: ImageButton
    private lateinit var SeekBar: SeekBar

    private var VideoPath : String = "/storage/emulated/0/Download/Ramen.mp4"

    private var Progress: Int = 0
    private var isFullSize: Boolean = false

    private lateinit var EZMediaplayer: EZMediaplayer
    private lateinit var FCallback: FragmentCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        현재는 쓰지않으므로 주석처리
//        Bundle에서 VideoPath로 저장된 값 가져오기.
//        if (arguments != null)
//            VideoPath = arguments.getString("VideoPath")
        if (savedInstanceState != null && !savedInstanceState.isEmpty)
            Progress = savedInstanceState.getInt("VideoPosition")

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("VideoPosition", Progress)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (activity is FragmentCallback)
            FCallback = activity as FragmentCallback
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        view_mediaplayer와 연결
        if (inflater != null){
            return inflater!!.inflate(R.layout.view_mediaplayer, container, false)
        }else {
            return super.onCreateView(inflater, container, savedInstanceState)
        }

    }

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        super.onViewCreated(v, savedInstanceState)

        ViewInit(v)
        MediaInit()
    }

    private fun ViewInit(v: View) {
        Addon = v.findViewById(R.id.Addon)
        VideoView = v.findViewById(R.id.VideoView)
        SurfaceView = v.findViewById(R.id.SurfaceView)
        TitleView = v.findViewById(R.id.TitleView)
        NowTime = v.findViewById(R.id.CurrentPosition)
        TimeDuration = v.findViewById(R.id.VideoDuration)
        PlayBtn = v.findViewById(R.id.playBtn)
        PinBtn = v.findViewById(R.id.PinBtn)
        ScreenChangeBtn = v.findViewById(R.id.ScreenChanger)
        SeekBar = v.findViewById(R.id.MediaProgressBar)

        ScreenChangeBtn.setOnClickListener(this)
    }

    private fun MediaInit() {
        EZMediaplayer = EZMediaplayer(Addon, SurfaceView, PlayBtn, PinBtn, SeekBar, activity!!.applicationContext, object : EZMediaplayer.MediaPlayerListener {
            override fun getNowProgress(progress: Int) {
                Progress = progress
                NowTime.setText(getTimes(progress))
            }

            override fun MediaInitilized() {
                TimeDuration.setText(getTimes(EZMediaplayer.GetVideoDuration()))
                fitFragementSize()
            }
        })
        EZMediaplayer.setMediaFile(VideoPath, Progress)
        EZMediaplayer.startMedia()

        NowTime.setText("0 : 0")
    }

    private fun fitFragementSize() {
        val dMode = resources.configuration.orientation
        val vW = EZMediaplayer.getVideoWidth()
        val vH = EZMediaplayer.getVideoHeight()

        val sW = activity!!.windowManager.defaultDisplay.width
        val sH = activity!!.windowManager.defaultDisplay.height

        SurfaceView.holder.setFixedSize(sW, ((vH.toFloat() / vW.toFloat()) * sW.toFloat()).toInt())
        if (dMode == Configuration.ORIENTATION_LANDSCAPE) {
            VideoView.layoutParams.height = RelativeLayout.LayoutParams.MATCH_PARENT
        }else{
            VideoView.layoutParams.height = RelativeLayout.LayoutParams.WRAP_CONTENT
        }
    }

    private fun getTimes(i: Int): String {
        return String.format("%01d : %01d", (i / (1000 * 60)), (i % (1000 * 60) / 1000))
    }

    override fun onClick(v: View?) {
        when(v) {
            ScreenChangeBtn -> {
                isFullSize = !isFullSize
                FCallback.onChangeScreen()
                fitFragementSize()
            }
        }
    }

}