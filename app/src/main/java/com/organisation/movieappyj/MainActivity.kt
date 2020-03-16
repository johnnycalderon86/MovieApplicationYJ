package com.organisation.movieappyj

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.postDelayed
import com.androidnetworking.interceptors.HttpLoggingInterceptor
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import kotlin.random.Random


class MainActivity : AppCompatActivity() {


    private val apiKey = BuildConfig.GoogleSecAPIKEY
    //--------------------------Disposables Variable
    private val disposables = CompositeDisposable()

    override fun onDestroy() {
        disposables.dispose()
        super.onDestroy()
    }

    //-----------------------------------------------------------ONCREATE
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//-------------------------------------------------------------logging var
        var logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

//--------------------------------------------------------------------------------client var of type OKHTTpClient
        var client: OkHttpClient = OkHttpClient().newBuilder().addInterceptor(logging).build()
//----------------------------------------------------------------------------------retrofit var of Type Retrofit //TODO: Study retrofit and also the RX library
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://www.omdbapi.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
//---------------------------------------------------------------------------------------SERVICES
        val service = retrofit.create(MovieService::class.java)
        val postersService: Posters = retrofit.create((Posters::class.java))
        //-----------------------------------------------------------------------------------Getting all the UI elements to display Data
        val button = findViewById<FloatingActionButton>(R.id.search_movie_button)
        val editText = findViewById<EditText>(R.id.searchbar_movie)
        val movieTitle = findViewById<TextView>(R.id.movieTitle)
        val moviePlot = findViewById<TextView>(R.id.moviePlot)
        val movieYear = findViewById<TextView>(R.id.movieYear)
        val movieActors = findViewById<TextView>(R.id.movieActors)
        val movieDirector = findViewById<TextView>(R.id.movieDirector)
        val movieImage = findViewById<ImageView>(R.id.movieImage)
        val actorsTextStyle = "Actors"
        var movieUrl: String
        val badmovieUrl = "https://m.media-amazon.com/images/M/MV5BMTE5MTYxMDg5NV5BMl5BanBnXkFtZTYwNjc5MzQ3._V1_SX300.jpg"
        val standardPosterHomeView ="https://m.media-amazon.com/images/M/MV5BZjdkOTU3MDktN2IxOS00OGEyLWFmMjktY2FiMmZkNWIyODZiXkEyXkFqcGdeQXVyMTMxODk2OTU@._V1_SX300.jpg"
        var movieRating = findViewById<TextView>(R.id.movieIMBDrating)
        val movieRatingStarVector = findViewById<ImageView>(R.id.movieRatingStar)
        val displayMovieDetails = findViewById<ScrollView>(R.id.displayMovieScrollView)
        val noDisplayMovieDetails = findViewById<ScrollView>(R.id.displayMovieNotFoundScrollView)
        val posterHomeImageView = findViewById<ImageView>(R.id.homePoster)
        var posterURL :String

//--------------------------------------------------------------------------------------Display Different posters to the homeScreen
        var randomID: String

        fun getRandomNumber(): String {
            randomID = "tt008${Random.nextInt(1000,9999)}"
            Log.d("randomNumber", randomID)
            return randomID
        }
        fun poster() {
            postersService.getPosters(apiKey, getRandomNumber())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d("tag", it.poster)
                    if(it.poster == "N/A"){
                        Picasso.get().load(standardPosterHomeView).fit().centerInside()
                            .into(posterHomeImageView)
                    }else{
                        posterURL = it.poster
                        Picasso.get().load(posterURL).fit()
                            .centerInside()
                            .into(posterHomeImageView)
                    }
                },
                    {

                        Log.e("tag", it.toString())
                    }
                ).addTo(disposables)

        }

        val timer = Timer()
        val monitor = object : TimerTask() {
            override fun run() {
                poster()
            }
        }

        timer.schedule(monitor, 2000, 5000)


//------------------------------------------------------------------------------------BUTTON ONClickListener
            button.setOnClickListener {
                posterHomeImageView.visibility =View.GONE
                monitor.cancel()
                var movieNameSearch = editText.text.toString()
                if (movieNameSearch.isEmpty()) {
                    noDisplayMovieDetails.visibility = View.VISIBLE
                    Picasso.get().load(badmovieUrl).into(badMovieSearch)
                } else {
                    movieRatingStarVector.visibility = View.VISIBLE
                    noDisplayMovieDetails.visibility = View.GONE
                    service.getCurrentMovieData(apiKey, movieNameSearch)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            {
                                if (it.title == null) {
                                    noDisplayMovieDetails.visibility = View.VISIBLE
                                    displayMovieDetails.visibility =View.GONE
                                    Picasso.get().load(badmovieUrl).fit().centerInside().into(badMovieSearch)
                                } else {
                                    movieTitle.text = it.title
                                    moviePlot.text = it.plot
                                    movieYear.text = it.released
                                    movieActors.text = "$actorsTextStyle\n ${it.actors}"
                                    movieDirector.text = "Director: ${it.director}"
                                    movieUrl = it.poster
                                    movieRating.text = "IMDB \n${it.imdbRating}/10"

                                    Picasso.get().load(movieUrl).into(movieImage)
//
                                    displayMovieDetails.visibility = View.VISIBLE
                                }

                            },
                            {

                                Log.e("tag", it.toString())
                            }

                        ).addTo(disposables)

                }

            }



    }
}


