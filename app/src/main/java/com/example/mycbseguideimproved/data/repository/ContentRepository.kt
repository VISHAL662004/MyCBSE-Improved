package com.example.mycbseguideimproved.data.repository

import com.example.mycbseguideimproved.data.api.ApiService
import com.example.mycbseguideimproved.data.model.Content

class ContentRepository(private val apiService: ApiService) {
    suspend fun getContent(contentId: Int): Content? {
        return try {
            val response = apiService.getContent(contentId)
            if (response.status == "200") {
                response.data
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
} 