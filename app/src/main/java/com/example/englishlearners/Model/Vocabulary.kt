package com.example.englishlearners.Model

import com.google.gson.annotations.SerializedName

class Vocabulary {
    @SerializedName("id"         ) var id         : Int?    = null
    @SerializedName("term"       ) var term       : String? = null
    @SerializedName("definition" ) var definition : String? = null
    @SerializedName("created_at" ) var createdAt  : String? = null
    @SerializedName("updated_at" ) var updatedAt  : String? = null
    @SerializedName("topic"      ) var topic      : Int?    = null
}