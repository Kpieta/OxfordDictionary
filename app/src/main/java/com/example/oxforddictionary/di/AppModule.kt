package com.example.oxforddictionary.di

import com.example.oxforddictionary.api.Api
import com.example.oxforddictionary.api.ApiConstants
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AppModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): Interceptor {
        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return interceptor
    }

    @Provides
    @Singleton
    fun provideOkhttp() =
        OkHttpClient.Builder()
            .addInterceptor(provideLoggingInterceptor())
            .addInterceptor(provideAuthInterceptor())
            .build()

    private fun provideAuthInterceptor(): Interceptor {
        val interceptor = Interceptor { chain ->
            val request = chain
                .request()
                .newBuilder()
                .addHeader(
                    "app_id", ApiConstants.app_id
                )
                .addHeader(
                    "app_key", ApiConstants.app_key
                )
                .build()
            chain.proceed(request)
        }
        return interceptor
    }


    @Provides
    @Singleton
    fun provideMoshi() =
        Moshi
            .Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

    @Provides
    @Singleton
    fun provideRetrofit() =
        Retrofit
            .Builder()
            .baseUrl(ApiConstants.BASE_URL)
            .client(provideOkhttp())
            .addConverterFactory(MoshiConverterFactory.create(provideMoshi()))
            .build()

    @Provides
    @Singleton
    fun provideAPI(): Api =
        provideRetrofit().create(Api::class.java)


}