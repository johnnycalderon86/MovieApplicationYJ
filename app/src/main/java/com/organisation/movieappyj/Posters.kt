package com.organisation.movieappyj

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface Posters {
    @GET("./")
    fun getPosters(@Query("apikey")apiKey:String,
                   @Query("i")imdbID:String):Observable<MovieResponse2>
}