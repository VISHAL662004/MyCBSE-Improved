package com.example.mycbseguideimproved.data.model

import com.google.gson.annotations.SerializedName

data class Category(
    val id: Int,
    val name: String,
    val weight: Int,
    val parent: Int?,
    @SerializedName("web_logo")
    val webLogo: String,
    @SerializedName("mobile_logo")
    val mobileLogo: String
)

data class CategoryResponse(
    val status: String,
    val categories: List<Category>
) 