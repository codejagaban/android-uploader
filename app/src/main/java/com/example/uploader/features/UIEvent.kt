package com.example.uploader.features

import android.content.Context
import android.net.Uri

sealed class UIEvent{
    data class SingleImageChanged(val context: Context, val uri: Uri): UIEvent()
    data class MultipleImageChanged(val context: Context,val uris: List<Uri>): UIEvent()
    data object GetImages : UIEvent()
}