package com.example.englishlearners.Model
import com.google.gson.annotations.SerializedName

class Topic {
    @SerializedName("id"         ) var id        : Int?    = null
    @SerializedName("title"      ) var title     : String? = null
    @SerializedName("image"      ) var image     : String? = null
    @SerializedName("created_at" ) var createdAt : String? = null
    @SerializedName("updated_at" ) var updatedAt : String? = null
    @SerializedName("user"       ) var user      : Int?    = null
}