package com.example.englishlearners.model

data class Folder(
    var id: String = "",
    var name: String = "",
    var ownerId: String = "",
    var owner: AppUser? = null,
)