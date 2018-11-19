package com.example.ginggingi.mediaplayerinfragment

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.view.WindowManager
import android.widget.RelativeLayout
import com.example.ginggingi.mediaplayerinfragment.Fragments.VideoFragment
import com.example.ginggingi.mediaplayerinfragment.Interfaces.FragmentCallback
import com.example.ginggingi.mediaplayerinfragment.Models.PermissionModels
import com.example.ginggingi.mediaplayerinfragment.Utils.PermissionChker
import kotlinx.android.synthetic.main.view_mediaplayer.*

class MainActivity : AppCompatActivity(), FragmentCallback{
    private val REQUEST_CODE = 1

    private lateinit var fTransaction: FragmentTransaction

    private lateinit var pChker: PermissionChker
    private lateinit var pModel: PermissionModels
    private lateinit var pList: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        PermissionInit()
        ChkOrientation()
        pChker.RequestPermission(this,
                pList,
                REQUEST_CODE,
                object : PermissionChker.RequestPermissionListener {
                    override fun onSuccess() {
                        if (savedInstanceState == null) {
                            fTransaction = supportFragmentManager.beginTransaction()
                            fTransaction.replace(R.id.Mplayer, VideoFragment())
                            fTransaction.commit()
                        }
                    }

                    override fun onFailed() {
                        finish()
                    }
                })
    }

    private fun ChkOrientation() {
        val dMode = resources.configuration.orientation
        if (dMode == Configuration.ORIENTATION_PORTRAIT){
            //세로모드
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }else{
            //가로모드
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }

    private fun PermissionInit() {
        pModel = PermissionModels()
        pList = arrayOf(
                pModel.readExStorage
        )
        pChker = PermissionChker()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        pChker.RequestPermissionsResult(requestCode, pList, grantResults)
    }

    override fun onChangeScreen() {
        val dMode = resources.configuration.orientation
        if (dMode == Configuration.ORIENTATION_PORTRAIT) {
            //세로
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }else{
            //가로
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    override fun AddonHided() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun AddonShowed() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}
