package com.organisation.movieappyj.random_posters

import kotlin.random.Random

var randomNumber:Int =0

fun getRandomNumber(){
    randomNumber = Random.nextInt(0, 8)
    println(randomNumber)
}

