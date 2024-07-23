package com.example.uploader.features

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.uploadcare.android.library.api.UploadcareClient
import com.uploadcare.android.library.api.UploadcareFile
import com.uploadcare.android.library.callbacks.UploadFileCallback
import com.uploadcare.android.library.callbacks.UploadFilesCallback
import com.uploadcare.android.library.callbacks.UploadcareAllFilesCallback
import com.uploadcare.android.library.exceptions.UploadcareApiException
import com.uploadcare.android.library.upload.FileUploader
import com.uploadcare.android.library.upload.MultipleFilesUploader
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update




class UIViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(UIState())
    val uiState: StateFlow<UIState> = _uiState.asStateFlow()

    private val client = UploadcareClient("dc10a36db143e2574355","3f84c490a8ca6240df3b")

    fun onEvent(event: UIEvent){
        when(event){
            is UIEvent.SingleImageChanged ->{
                uploadSingleImage(event.context, event.uri)
            }
            is UIEvent.MultipleImageChanged->{
                uploadMultipleImages(event.context, event.uris)
            }
            is UIEvent.GetImages->{
                getImages()
            }

        }
    }

    private fun uploadSingleImage(context: Context, uri: Uri) {
        _uiState.update { it.copy(isUploading = true) }


        val uploader = FileUploader(client, uri, context).store(true)
        uploader.uploadAsync(object : UploadFileCallback {
            override fun onFailure(e: UploadcareApiException) {
                Log.i("ERROR", e.message.toString())
            }

            override fun onProgressUpdate(
                bytesWritten: Long,
                contentLength: Long,
                progress: Double
            ) {
            }

            override fun onSuccess(result: UploadcareFile) {
                val imageResult = ImageResults(uid = result.uuid, imageUrl = result.originalFileUrl.toString())
                val images = _uiState.value.images
                images.add(imageResult)
                _uiState.update { it.copy(isUploading = false, images = images) }
            }

        })

    }
        private fun uploadMultipleImages(context: Context, uris: List<Uri>){
        _uiState.value = UIState(isUploading = true)
            val uploader = MultipleFilesUploader(client, uris, context).store(true)

            uploader.uploadAsync(object : UploadFilesCallback {
            override fun onFailure(e: UploadcareApiException) {
                Log.i("ERROR", e.message.toString())
            }

            override fun onProgressUpdate(
                bytesWritten: Long,
                contentLength: Long,
                progress: Double
            ) {
                // Upload progress info.
            }

                override fun onSuccess(result: List<UploadcareFile>) {
                    val images = _uiState.value.images
                    result.forEach {
                        images.add(
                            ImageResults(
                                uid = it.uuid,
                                imageUrl = it.originalFileUrl.toString()
                            )
                        )
                    }
                    _uiState.update { it.copy(isUploading = false, images = images) }
                }
        })

    }
        private fun getImages(){
        _uiState.value = UIState(isUploading = true)
            val images = _uiState.value.images
        client.getFiles().asListAsync(object : UploadcareAllFilesCallback{
            override fun onFailure(e: UploadcareApiException) {
                Log.i("ERROR", e.message.toString())
            }

            override fun onSuccess(result: List<UploadcareFile>) {
                result.forEach {
                    images.add(
                        ImageResults(
                            uid = it.uuid,
                            imageUrl = it.originalFileUrl.toString()
                        )
                    )
                }
                _uiState.update { it.copy(isUploading = false, images = images) }
            }

        })
    }
}


