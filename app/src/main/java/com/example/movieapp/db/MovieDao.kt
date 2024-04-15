package com.example.movieapp.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MovieDao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insertMovie(entity: MovieEntity)

        @Delete
        suspend fun deleteMovie(entity: MovieEntity)

        @Query("SELECT * From MovieTable")
        fun getAllMovies() : MutableList<MovieEntity>

        @Query("SELECT EXISTS (SELECT 1 FROM MovieTable WHERE id = :id)")
        suspend fun existMovie(id: Int): Boolean

}