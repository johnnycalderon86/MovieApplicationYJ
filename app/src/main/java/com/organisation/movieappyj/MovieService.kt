package com.organisation.movieappyj

import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*

interface MovieService {
    @GET("./")
    fun getCurrentMovieData(@Query("apikey") apiKey :String,
                            @Query("t")type:String): Observable<MovieResponse2>

}