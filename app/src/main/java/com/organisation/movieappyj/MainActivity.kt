package com.organisation.movieappyj

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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

class MainActivity : AppCompatActivity(){



    private  val apiKey= BuildConfig.GoogleSecAPIKEY    
    //--------------------------Disposables Variable //Todo: Learn what CompositeDisposable class does
    val disposables = CompositeDisposable()

    override fun onDestroy() {
        disposables.dispose()
        super.onDestroy()
    }
    //-----------------------------------------------------------ONCREATE
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//-------------------------------------------------------------logging var //TODO: Learn more about HTTPLoggingInterceptor
        var logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

//--------------------------------------------------------------------------------client var of type OKHTTpClient //TODO: Learn more about OkHttpClient
        var client: OkHttpClient = OkHttpClient().newBuilder().addInterceptor(logging).build()
//----------------------------------------------------------------------------------retrofit var of Type Retrofit //TODO: Study retrofit and also the RX library
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://www.omdbapi.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        val service = retrofit.create(MovieService::class.java)
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
        var movieUrl: String;
        val ifEmptyTextView = findViewById<TextView>(R.id.ifEmptyTextView)
        val badMovieImage = findViewById<ImageView>(R.id.badMovieSearch)
        val badmovieUrl = "https://m.media-amazon.com/images/M/MV5BMTE5MTYxMDg5NV5BMl5BanBnXkFtZTYwNjc5MzQ3._V1_SX300.jpg"
        var movieRating = findViewById<TextView>(R.id.movieIMBDrating)
        val movieRatingStarVector = findViewById<ImageView>(R.id.movieRatingStar)
//------------------------------------------------------------------------------------BUTTON onClickListener
        button.setOnClickListener {
            var movieNameSearch = editText.text.toString()


            if (movieNameSearch.isEmpty()) {
                ifEmptyTextView.visibility = View.VISIBLE
                badMovieImage.visibility = View.VISIBLE
                Picasso.get().load(badmovieUrl).into(badMovieSearch)
            }

            else {

                movieRatingStarVector.visibility = View.VISIBLE
                badMovieImage.visibility = View.GONE
                ifEmptyTextView.visibility = View.GONE
                service.getCurrentMovieData(apiKey, movieNameSearch)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {

                            if(it.title == null ){
                                movieRatingStarVector.visibility = View.GONE
                                movieTitle.visibility = View.GONE
                                movieDirector.visibility = View.GONE
                                moviePlot.visibility = View.GONE
                                movieYear.visibility = View.GONE
                                movieActors.visibility = View.GONE
                                movieRating.visibility = View.GONE
                                movieImage.visibility = View.GONE
                                ifEmptyTextView.visibility = View.VISIBLE
                                badMovieImage.visibility = View.VISIBLE
                                Picasso.get().load(badmovieUrl).into(badMovieSearch)



                            } else {
                                movieTitle.text = it.title
                                moviePlot.text = it.plot
                                movieYear.text = it.released
                                movieActors.text = "$actorsTextStyle\n ${it.actors}"
                                movieDirector.text = "Director: ${it.director}"
                                movieUrl = it.poster
                                movieRating.text = "IMDB \n${it.imdbRating}/10"

                                Picasso.get().load(movieUrl).into(movieImage)
                                movieRatingStarVector.visibility = View.VISIBLE
                                movieTitle.visibility = View.VISIBLE
                                movieDirector.visibility = View.VISIBLE
                                moviePlot.visibility = View.VISIBLE
                                movieYear.visibility = View.VISIBLE
                                movieActors.visibility = View.VISIBLE
                                movieRating.visibility = View.VISIBLE
                                movieImage.visibility = View.VISIBLE
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