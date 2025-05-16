package com.example.mycbseguideimproved.data.model

import com.google.gson.annotations.SerializedName

data class Content(
    val id: Int,
    val title: String,
    val description: String,
    val content: String,
    @SerializedName("content_type")
    val contentType: Int,
    val category: Int,
    @SerializedName("is_published")
    val isPublished: Boolean,
    @SerializedName("file_name")
    val fileName: String?,
    @SerializedName("file_path")
    val filePath: String?,
    @SerializedName("file_url")
    val fileUrl: String?,
    @SerializedName("has_download")
    val hasDownload: Boolean
)

data class ContentResponse(
    val status: String,
    val data: Content
) 