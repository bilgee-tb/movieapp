package com.example.movieapp.di

import android.content.Context
import androidx.room.Room
import com.example.movieapp.db.MovieDatabse
import com.example.movieapp.db.MovieEntity
import com.example.movieapp.model.movieDetails.MovieDetailResponse
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabseModule {


        @Provides
        @Singleton
        fun provideDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
            context, MovieDatabse::class.java,"movies_db"
        )
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
        @Provides
        @Singleton
        fun provideDao(db : MovieDatabse) = db.moviesDoa()


        @Provides
        @Singleton
        fun provideEntity() = MovieEntity()
}