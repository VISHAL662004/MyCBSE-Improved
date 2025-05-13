package com.example.mycbseguideimproved.data.repository

import com.example.mycbseguideimproved.data.api.ApiService
import com.example.mycbseguideimproved.data.model.Category

class CategoryRepository(private val apiService: ApiService) {
    suspend fun getCategories(): List<Category> {
        return try {
            val response = apiService.getCategories()
            if (response.status == "200") {
                response.categories
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
} 