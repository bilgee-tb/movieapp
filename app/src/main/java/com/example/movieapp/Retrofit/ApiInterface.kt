package com.example.movieapp.Retrofit

import com.example.movieapp.model.actors.ActorsResponse
import com.example.movieapp.model.genere.GenreResponse
import com.example.movieapp.model.movieDetails.MovieDetailResponse
import com.example.movieapp.model.popularMovie.MovieResponse
import com.example.movieapp.utils.Constants.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterface {

    @GET("movie/popular")
    suspend fun getPopularMovies(@Query("page") page: Int, @Query("api_key") api_key: String =API_KEY): Response<MovieResponse>

    @GET("genre/movie/list")
    suspend fun getGenres(@Query("api_key") api_key: String =API_KEY): Response<GenreResponse>

    @GET("discover/movie")
    suspend fun getMoviesByGenres(@Query("page") page: Int,@Query("with_genres") with_genres: String,@Query("api_key") api_key: String =API_KEY): Response<MovieResponse>

    @GET("search/movie")
    suspend fun getSearchMoviesList(@Query("page") page: Int,@Query("query") query: String,@Query("api_key") api_key: String =API_KEY): Response<MovieResponse>

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(@Path("movie_id") id: Int,@Query("api_key") api_key: String =API_KEY): Response<MovieDetailResponse>



    @GET("movie/{movie_id}/credits")
    suspend fun getMovieCredits(@Path("movie_id") id: Int,@Query("api_key") api_key: String =API_KEY): Response<ActorsResponse>
}