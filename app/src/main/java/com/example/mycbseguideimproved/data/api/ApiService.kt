package com.example.mycbseguideimproved.data.api

import com.example.mycbseguideimproved.data.model.CategoryResponse
import com.example.mycbseguideimproved.data.model.ContentResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("v1/category/all/")
    suspend fun getCategories(): CategoryResponse
    
    @GET("v1/content/data/{contentId}/")
    suspend fun getContent(@Path("contentId") contentId: Int): ContentResponse
}

object ApiClient {
    private const val BASE_URL = "https://api.mycbseguide.com/"

    fun create(): ApiService {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        return retrofit2.Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
} 