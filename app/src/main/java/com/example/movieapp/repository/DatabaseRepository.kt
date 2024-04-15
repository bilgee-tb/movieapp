package com.example.movieapp.repository

import com.example.movieapp.db.MovieDao
import com.example.movieapp.db.MovieEntity
import com.example.movieapp.model.movieDetails.MovieDetailResponse
import javax.inject.Inject

class DatabaseRepository @Inject constructor(private val dao : MovieDao){


         fun getAllFavoriteList ()=dao.getAllMovies()
        suspend fun insertMovie(entity: MovieEntity) = dao.insertMovie(entity)
        suspend fun deleteMovie(entity: MovieEntity) = dao.deleteMovie(entity)
        suspend fun existMovie(id: Int) = dao.existMovie(id)


}