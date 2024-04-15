package com.example.movieapp.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "MovieTable")
data class MovieEntity(
    @PrimaryKey
    var id: Int=0,
    var title: String= "",
    var posterPath: String= "",
    var voteAverage: String="",
    var releaseDate: String="",
    var originalLanguage: String="",
    var runTime:String=""
)
