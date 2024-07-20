package com.example.uploader.features

import android.content.Context
import android.net.Uri

sealed class UIEvent{
    data class SingleImageChanged(val uri: Uri, val context: Context): UIEvent()
    data class MultipleImageChanged(val uris: List<Uri>, val context: Context): UIEvent()
    data object GetImages : UIEvent()
}