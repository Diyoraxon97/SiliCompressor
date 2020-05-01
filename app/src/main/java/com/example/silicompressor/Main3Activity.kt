package com.example.silicompressor

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.iceteck.silicompressorr.SiliCompressor
import kotlinx.android.synthetic.main.activity_main3.*
import java.io.File
import java.lang.Boolean
import java.net.URISyntaxException


class Main3Activity : AppCompatActivity() {

    companion object {
        const val REQUEST_SELECT_VIDEO = 0
    }

    private lateinit var path: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)
        chooseVideo.setOnClickListener {
            checkPermissions(
                arrayOf(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) { pickVideo() }

        }
        buttonPlay.setOnClickListener { VideoPlayActivity.start(this, path) }
    }

    //Pick a video file from device
    private fun pickVideo() {
        val intent = Intent()
        intent.apply {
            type = "video/*"
            action = Intent.ACTION_PICK
        }
        startActivityForResult(Intent.createChooser(intent, "Select video"), REQUEST_SELECT_VIDEO)
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK)
            if (requestCode == REQUEST_SELECT_VIDEO) {
                if (data != null && data.data != null) {
                    val uri = data.data
                    path = getMediaPath(this, uri!!)
                    val file = File(path)
                    videoImage.load(file)
                    textSizeOriginal.text= getFileSize(file.length())
                    val destinationDirectory =
                        File( Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).path + "/Silicompressor/videos")
                    var time = 0L
                    if (destinationDirectory.mkdirs() || destinationDirectory.isDirectory) {
                        //compress and output new video specs
                        //new VideoCompressAsyncTask(this).execute("true", mCurrentPhotoPath, f.getPath());
                   VideoCompressAsyncTask(this).execute(
                            "false",
                            uri.toString(),
                            destinationDirectory.path
                        )
                    }
                }
            }

        super.onActivityResult(requestCode, resultCode, data)
    }

}
class VideoCompressAsyncTask(private val mContext: Context):AsyncTask<String,String,String>(){
    override fun doInBackground(vararg params: String?): String {
        var filePath: String? = null
        try {

            //This bellow is just a temporary solution to test that method call works
            val b = Boolean.parseBoolean(params[0])
            filePath = if (b) {
                SiliCompressor.with(mContext).compressVideo(params[1], params[2])
            } else {
                val videoContentUri: Uri = Uri.parse(params[1])
                // Example using the bitrate and video size parameters
                /*filePath = SiliCompressor.with(mContext).compressVideo(
                                    videoContentUri,
                                    params[2],
                                    1280,
                                    720,
                                    1500000);*/
                SiliCompressor.with(mContext).compressVideo(
                    videoContentUri.toString(),
                    params[2]
                )
            }
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return filePath!!
    }

    override fun onPreExecute() {
        super.onPreExecute()
        Log.d("ttt","onPreExecute")
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)

    }
}