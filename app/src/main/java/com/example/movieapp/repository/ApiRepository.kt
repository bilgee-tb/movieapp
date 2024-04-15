package com.example.movieapp.repository

import com.example.movieapp.Retrofit.ApiInterface
import javax.inject.Inject

class ApiRepository  @Inject  constructor( private val apiInterface:ApiInterface) {
    suspend fun getPopularMovies(page: Int) = apiInterface.getPopularMovies(page)
    suspend fun getGenres() = apiInterface.getGenres()
    suspend fun getMovieByGenre(page: Int,with_genres: String) =apiInterface.getMoviesByGenres(page,with_genres)
    suspend fun  getSearchMovielist(page: Int,query:String)=apiInterface.getSearchMoviesList(page,query)
    suspend fun getMovieDetails(id: Int) = apiInterface.getMovieDetails(id)
    suspend fun getMovieCredits(id: Int) = apiInterface.getMovieCredits(id)

}