package com.example.movieapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.movieapp.model.movieDetails.MovieDetailResponse

@Database(entities = [MovieEntity::class], version = 2, exportSchema = false)
abstract  class MovieDatabse : RoomDatabase() {
        abstract  fun moviesDoa() : MovieDao
}