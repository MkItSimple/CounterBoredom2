package com.mkitsimple.counterboredom2.ui.main

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment.DIRECTORY_DOWNLOADS
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mkitsimple.counterboredom2.R
import kotlinx.android.synthetic.main.activity_download.*


class DownloadActivity : AppCompatActivity() {
    var firebaseStorage: FirebaseStorage? = null
    var storageReference: StorageReference? = null
    var ref: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download)

        buttonDownload.setOnClickListener {
            download()
        }
    }
    private fun download() {
        storageReference = FirebaseStorage.getInstance().reference
        ref = storageReference!!.child("kotlin.png")
        ref!!.downloadUrl.addOnSuccessListener { uri ->
            val url = uri.toString()
            downloadFiles(
                this@DownloadActivity,
                "kotlin",
                ".png",
                DIRECTORY_DOWNLOADS,
                url
            )
        }.addOnFailureListener { }
    }

    private fun downloadFiles(
        context: Context,
        fileName: String,
        fileExtension: String,
        destinationDirectory: String,
        url: String
    ) {
        val downloadManager =
            context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(url)
        val request = DownloadManager.Request(uri)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalFilesDir(
            context,
            destinationDirectory,
            fileName + fileExtension
        )
        downloadManager.enqueue(request)
    }
}
