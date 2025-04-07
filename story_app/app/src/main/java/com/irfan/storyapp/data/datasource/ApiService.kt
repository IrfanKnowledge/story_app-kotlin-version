package com.irfan.storyapp.data.datasource

import com.irfan.storyapp.data.model.auth.LoginResponse
import com.irfan.storyapp.data.model.auth.RegisterResponse
import com.irfan.storyapp.data.model.story.DetailStoryResponse
import com.irfan.storyapp.data.model.story.ListStoryResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
    ): Response<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String,
    ): Response<LoginResponse>

    @GET("stories")
    suspend fun getStories(
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("location") location: Int? = null,
    ): Response<ListStoryResponse>

    @GET("stories/{id}")
    suspend fun getDetailStory(
        @Path("id") id: String? = null,
    ): Response<DetailStoryResponse>
}