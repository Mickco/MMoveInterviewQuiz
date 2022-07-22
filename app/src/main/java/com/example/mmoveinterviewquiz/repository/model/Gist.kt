package com.example.mmoveinterviewquiz.repository.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Gist(
    val id: String,
    val url: String,
    val username: String,
    val csvFilename: String?,
): Parcelable