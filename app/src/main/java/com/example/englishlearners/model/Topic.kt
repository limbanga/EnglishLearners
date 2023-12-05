package com.example.englishlearners.model

import android.service.quicksettings.Tile

data class Topic(
    var id: String = "",
    var title: String = "",
    var desc: String = "",
    var created: Long = 0,
    var updated: Long = 0,
)