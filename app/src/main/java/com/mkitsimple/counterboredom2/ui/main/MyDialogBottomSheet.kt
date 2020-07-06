package com.mkitsimple.counterboredom2.ui.main

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mkitsimple.counterboredom2.R
import kotlinx.android.synthetic.main.my_dialog_bottom_sheet.*

class MyDialogBottomSheet(filename: String) : BottomSheetDialogFragment() {

    //var firebaseStorage: FirebaseStorage? = null
    var storageReference: StorageReference? = null
    var ref: StorageReference? = null
    val filename: String = filename

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.my_dialog_bottom_sheet, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        tvSaveImage.setOnClickListener {
            //Snackbar snackbar = Snackbar
            if (filename != ""){
                download(it, filename)
            }
        }

        tvCancelSave.setOnClickListener {
            dismiss()
        }
    }

    private fun download(it: View, filename: String) {
        storageReference = FirebaseStorage.getInstance().reference
        ref = storageReference!!.child("messages").child(filename)
        ref!!.downloadUrl.addOnSuccessListener { uri ->
            dismiss()
            Toast.makeText(context, "Image Saved!", Toast.LENGTH_LONG).show()
            val url = uri.toString()
            downloadFiles(
                it.context,
                filename,
                ".jpeg",
                Environment.DIRECTORY_DOWNLOADS,
                url
            )
        }.addOnFailureListener {}
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