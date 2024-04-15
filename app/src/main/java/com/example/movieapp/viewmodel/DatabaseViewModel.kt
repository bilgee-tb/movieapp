package com.example.movieapp.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.model.movieDetails.MovieDetailResponse
import com.example.movieapp.repository.DatabaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.example.movieapp.db.MovieEntity
import kotlinx.coroutines.withContext

@HiltViewModel
class DatabaseViewModel @Inject constructor(private val databaseRepository: DatabaseRepository) : ViewModel() {



    val favoriteMovieList = MutableLiveData<List<MovieEntity>?>() // Changed type to List<MovieEntity>

    val emptyList = MutableLiveData<Boolean>()

    fun deleteMovie(movie: MovieEntity) {
        viewModelScope.launch {
            try {
                databaseRepository.deleteMovie(movie)
            } catch (e: Exception) {
                // Log the error or handle it appropriately
                Log.e("DatabaseViewModel", "Error deleting movie", e)
            }
        }
    }

    fun insertMovie(movie: MovieEntity) {
        viewModelScope.launch {
            try {
                databaseRepository.insertMovie(movie)
            } catch (e: Exception) {
                // Log the error or handle it appropriately
                Log.e("DatabaseViewModel", "Error inserting movie", e)
            }
        }
    }
    fun loadFavoriteMovieList() { viewModelScope.launch {
            try {
                val favoriteMovies = databaseRepository.getAllFavoriteList() // Access the LiveData value
                if (favoriteMovies.isNotEmpty()) { // Check if the list is not null and not empty

                    favoriteMovieList.postValue(favoriteMovies)
                    emptyList.postValue(false)
                } else {
                    emptyList.postValue(true)
                }
            } catch (e: Exception) {
                // Log the error or handle it appropriately
                Log.e("DatabaseViewModel", "Error loading favorite movie list", e)
            }
        }
    }
    //Database
    val isFavorite = MutableLiveData<Boolean>()
    suspend fun existMovie(id:Int)= withContext(viewModelScope.coroutineContext){
        databaseRepository.existMovie(id)
    }

    fun favoriteMovie(id: Int, entity: MovieEntity) { // Renamed the function
        viewModelScope.launch {
            try {
                val isMovieInFavorites = databaseRepository.existMovie(id)
                if (isMovieInFavorites){
                    isFavorite.postValue(false)
                    databaseRepository.deleteMovie(entity)
                }else{
                    isFavorite.postValue(true)
                    databaseRepository.insertMovie(entity)
                }
            } catch (e: Exception) {
                // Log the error or handle it appropriately
                Log.e("DatabaseViewModel", "Error toggling favorite movie", e)
            }
        }
    }
}