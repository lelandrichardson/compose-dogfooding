package com.example.dogfood

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Text
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.ui.platform.setContent
import androidx.ui.tooling.preview.Preview
import com.example.dogfood.api.AmbientMovieApi
import com.example.dogfood.api.MovieApi
import com.example.dogfood.api.baseOkHttpClient
import com.example.dogfood.ui.DogfoodTheme
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MainActivity : AppCompatActivity() {
    private val retrofit: Retrofit = Retrofit
        .Builder()
        .baseUrl("https://api.themoviedb.org/3/")
        .client(baseOkHttpClient)
        .addConverterFactory(
            MoshiConverterFactory.create(
                Moshi.Builder()
                    .addLast(KotlinJsonAdapterFactory())
                    .build()
            )
        )
        .build()
    val api = retrofit.create(MovieApi::class.java)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DogfoodTheme {
                Providers(AmbientMovieApi provides api) {
                    // A surface container using the 'background' color from the theme
                    Surface(color = MaterialTheme.colors.background) {
                        Screen()
                    }
                }
            }
        }
    }
}
