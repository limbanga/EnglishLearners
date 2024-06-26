package com.example.englishlearners.model

data class Topic(
    var id: String = "",
    var title: String = "",
    var desc: String = "",
    var isPublic: Boolean = false,
    var ownerId: String = "",
    var owner: AppUser? = null,
    var vocabularyCount: Long = 0,
    var created: Long = 0,
    var updated: Long = 0,
)