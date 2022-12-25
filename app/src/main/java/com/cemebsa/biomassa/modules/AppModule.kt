package com.cemebsa.biomassa.modules

import android.app.DownloadManager
import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences{
        return context.getSharedPreferences("user_login_preferences", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideDownloadManager(@ApplicationContext context: Context): DownloadManager{
        return context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    }
}