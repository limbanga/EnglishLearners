package com.example.englishlearners.model

data class Folder(
    var id: String = "",
    var name: String = "",
    var desc: String = "",
    var ownerId: String = "",
    var owner: AppUser? = null,
    var topics: ArrayList<Topic>? = null,
)