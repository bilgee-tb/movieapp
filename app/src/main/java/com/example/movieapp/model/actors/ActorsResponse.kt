package com.example.movieapp.model.actors

data class ActorsResponse(
    val cast: List<Cast>,
    val crew: List<Crew>,
    val id: Int
)