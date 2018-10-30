package com.example.ginggingi.mediaplayerinfragment.Utils

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.*

import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.SeekBar
import com.example.ginggingi.mediaplayerinfragment.R
import java.io.File

class EZMediaplayer: AppCompatActivity,
        SurfaceHolder.Callback,
        View.OnClickListener,
        MediaPlayer.OnPreparedListener,
        SeekBar.OnSeekBarChangeListener{

    private var fileUri: String = ""
    private var CurrentPosition = 0

    private var pressTime = 0
    private var sec = 3000

    private var Addon: RelativeLayout
    private var Surface: SurfaceView
    private var playBtn: ImageButton
    private var pinBtn: ImageButton
    private var Seekbar: SeekBar
    private var mContext: Context
    private var mediaPlayerListener: MediaPlayerListener

    private var AddonCondition: Boolean = true // true: On, False: Off
    private var isMediaPlayerinit: Boolean = false
    private var PinChked: Boolean = false

    private lateinit var params: ViewGroup.MarginLayoutParams

    private lateinit var handler: Handler
    private lateinit var seekRunnable: Runnable

    private lateinit var SurfaceHolder: SurfaceHolder

    private lateinit var mediaPlayer: MediaPlayer

    constructor(Addon: RelativeLayout,
                Surface: SurfaceView,
                playBtn: ImageButton,
                pinBtn: ImageButton,
                Seekbar: SeekBar,
                mContext: Context,
                mediaPlayerListener: MediaPlayerListener) {
        this.Addon = Addon
        this.Surface = Surface
        this.playBtn = playBtn
        this.pinBtn = pinBtn
        this.Seekbar = Seekbar
        this.mContext = mContext
        this.mediaPlayerListener = mediaPlayerListener
    }

    fun setMediaFile(fileUri: String) {
        this.fileUri = fileUri
    }
    fun setMediaFile(fileUri: String, currentPosition: Int) {
        this.fileUri = fileUri
        this.CurrentPosition = currentPosition
    }
    fun setMediaFile(file: File) {
        this.fileUri = Uri.fromFile(file).toString()
    }
    fun setMediaFile(file: File, currentPosition: Int) {
        this.fileUri = Uri.fromFile(file).toString()
        this.CurrentPosition = currentPosition
    }

    fun startMedia() {
        AddonInit()
        SurfaceInit()
    }

    fun releaseMedia() {
        if (mediaPlayer != null)
            mediaPlayer.release()
    }

    private fun SeekBarInit() {
        Seekbar.max = mediaPlayer.duration/250
        Seekbar.setOnSeekBarChangeListener(this)
        SeekBarRun()
    }
    private fun SeekBarRun() {
        seekRunnable = Runnable{
            if (mediaPlayer != null && mediaPlayer.isPlaying) {
                Seekbar.setProgress(mediaPlayer.currentPosition/250)
                mediaPlayerListener.getNowProgress(mediaPlayer.currentPosition)
            }
            handler.postDelayed( seekRunnable,250)
        }
        handler.postDelayed(seekRunnable, 250)
    }

    private fun AddonInit() {
        playBtn.setOnClickListener(this)
        pinBtn.setOnClickListener(this)
        handler = Handler()
        params = Seekbar.layoutParams as ViewGroup.MarginLayoutParams
    }
    private fun SurfaceInit() {
        mediaPlayer = MediaPlayer()
        SurfaceHolder = Surface.holder

        Surface.setOnClickListener(this)
        SurfaceHolder.addCallback(this)
    }

    override fun onClick(v: View?) {
        when(v) {
            playBtn -> {
                if (isMediaPlayerinit) {
                    if (!mediaPlayer.isPlaying) {
                        playBtn.setImageResource(R.drawable.ic_pause_white_24dp)
                        mediaPlayer.start()
                        HideAddon()
                    }else{
                        playBtn.setImageResource(R.drawable.ic_play_arrow_white_24dp)
                        mediaPlayer.pause()
                    }
                }
            }

            pinBtn -> {
                if (PinChked)
                    setPinDisChk()
                else
                    setPinChk()
            }

            Surface -> {
                ChkUserTouch()
                if (!AddonCondition)
                    ShowAddon()
            }

        }
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {

    }
    override fun surfaceDestroyed(holder: SurfaceHolder?) {

    }
    override fun surfaceCreated(holder: SurfaceHolder?) {
        mediaPlayer.setDisplay(holder)
        try{
            if(File(fileUri).exists()) {
                mediaPlayer.setDataSource(
                        mContext,
                        Uri.parse(File(fileUri).path))
                mediaPlayer.prepare()
                mediaPlayer.setOnPreparedListener(this)
            }
        }catch (e: Exception) {
            e.printStackTrace()
        }
    }
    override fun onPrepared(mp: MediaPlayer?) {
        if (!ChkIsFirst())
            mediaPlayer.seekTo(CurrentPosition)

        mediaPlayer.start()
        HideAddon()
        isMediaPlayerinit = true
        mediaPlayerListener.MediaInitilized()
        SeekBarInit()
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (fromUser){
            mediaPlayer.seekTo(progress*250)
        }
    }
    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        if (mediaPlayer.isPlaying) {
            ShowAddon()
            Seekbar.thumb.alpha = 255
            mediaPlayer.pause()
        }
    }
    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        if (!mediaPlayer.isPlaying) {
            Seekbar.thumb.alpha = 0
            mediaPlayer.start()
        }
    }

    fun GetVideoDuration() : Int{
        return mediaPlayer.duration
    }

    private fun ChkIsFirst(): Boolean {
        return if (CurrentPosition != 0) false else true
    }
    private fun ChkUserTouch() {
        if (pressTime == 0 && !PinChked){
            sec = 0
            pressTime = System.currentTimeMillis().toInt()
            handler.postDelayed(ChkUserTouchRunnable, 3000)
            Log.i("Sec1" , sec.toString())
        }else {
            sec = (System.currentTimeMillis() - pressTime).toInt()
            Log.i("Sec2" , sec.toString())
            if (sec <= 3000) {
                setPinChk()
            }
            pressTime = 0
        }
    }

    private fun setPinChk() {
        PinChked = true
        pinBtn.setImageResource(R.drawable.ic_radio_button_checked_white_24dp)
    }
    private fun setPinDisChk() {
        PinChked = false
        pinBtn.setImageResource(R.drawable.ic_radio_button_unchecked_white_24dp)
        HideAddon()
    }

    private fun HideAddon() {
        if (!PinChked) {
            Addon.visibility = View.GONE
            AddonCondition = false

//            params.setMargins(-50, 0, -50, -40)
            Seekbar.thumb.alpha = 0
//            Seekbar.layoutParams = params
        }
    }
    private fun ShowAddon() {
        Addon.visibility = View.VISIBLE
        AddonCondition = true

        params.setMargins(0, 0, 0, 0)
        Seekbar.layoutParams = params
    }

    fun getVideoWidth(): Int { return mediaPlayer.videoWidth }
    fun getVideoHeight(): Int { return mediaPlayer.videoHeight }

    val ChkUserTouchRunnable = Runnable {
        kotlin.run {
            if (!PinChked) {
                Log.i("Sec3", sec.toString())
                pressTime = 0
                if(AddonCondition)
                    HideAddon()
            }else{
                setPinChk()
            }
        }
    }

    interface MediaPlayerListener {
        fun getNowProgress(progress: Int)
        fun MediaInitilized()
    }
}