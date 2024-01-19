package com.example.newsapp.di

import android.content.Context
import androidx.room.Room
import com.example.newsapp.data.local.db.ArticleDatabase
import com.example.newsapp.data.network.api.NewsApi
import com.example.newsapp.data.local.repository.BookmarkedNewsRepository
import com.example.newsapp.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AppModule::class]
)
object TestAppModule {

    @Provides
    @Singleton
    fun getRetrofit(): Retrofit {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    fun provideApi(retrofit: Retrofit): NewsApi {
        return retrofit.create(NewsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideTestDatabase(@ApplicationContext app: Context) =
        Room.inMemoryDatabaseBuilder(
            app,
            ArticleDatabase::class.java
        ).allowMainThreadQueries().build()

    @Provides
    @Singleton
    fun provideBookmarkedNewsRepository(db: ArticleDatabase) =
        BookmarkedNewsRepository(db.getBookmarkedNewsDao())
}