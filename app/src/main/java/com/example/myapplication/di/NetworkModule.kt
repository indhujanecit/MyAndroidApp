package com.example.myapplication.di

import android.util.Log
import com.example.myapplication.data.ApiClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.processor.internal.definecomponent.codegen._dagger_hilt_components_SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    private val token = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI2ZGYzODIxYWVhMTk2MDY4ZWY4NDE3NDA3OGJiNDdiZiIsIm5iZiI6MTU5Mzc2NTY0My4yMzMsInN1YiI6IjVlZmVlZjBiYmU3ZjM1MDAzMmE2Y2M4YyIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.S1qeof1oEH-cHmpSnPGVb4W61qxkbxcR4Wz0JImrAZs"
    @Provides
    fun provideBaseUrl() = "https://api.themoviedb.org/3/"

    @Provides
    @Singleton
    fun provideRetrofit(baseUrl: String): Retrofit =
        Retrofit.Builder()
            .baseUrl(provideBaseUrl())
            .client(provideOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .addHeader("accept", "application/json")
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(provideLoggingInterceptor())
            .build()

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor { message ->
            Log.d("NetworkLog", message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideMovieApi(retrofit: Retrofit): ApiClient =
        retrofit.create(ApiClient::class.java)

}