package com.example.dogfood.api

import androidx.compose.runtime.ambientOf
import com.squareup.moshi.Json
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

val AmbientMovieApi = ambientOf<MovieApi>()

interface MovieApi {
    @GET("movie/now_playing")
    suspend fun nowPlaying(@Query("page") page: Int): MoviesResponse

    @GET("movie/top_rated")
    suspend fun topRated(@Query("page") page: Int): MoviesResponse

    @GET("movie/upcoming")
    suspend fun upcoming(@Query("page") page: Int): MoviesResponse

    @GET("movie/{movieId}")
    suspend fun movie(
        @Path("movieId") id: Int,
        @Query("append_to_response") append: String = "images,credits"
    ): Movie

//    @GET("movie/{movieId}/credits")
//    suspend fun credits(@Path("movieId") movieId: Int): MovieCredits

//    @GET("movie/{movieId}/images")
//    suspend fun images(@Path("movieId") movieId: Int): ImagesResponse

}

data class MoviesResponse(
    @field:Json(name="page") val page: Int,
    @field:Json(name="results") val results: List<PartialMovie>
)

//data class ImagesResponse(
//    @field:Json(name="id") val id: Int,
//    @field:Json(name="backdrops") val backdrops: List<MovieImage>,
//    @field:Json(name="posters") val posters: List<MovieImage>
//)

data class PartialMovie(
    @field:Json(name="id") val id: Int,
    @field:Json(name="title") val title: String,
    @field:Json(name="poster_path") val poster_path: String?,
    @field:Json(name="backdrop_path") val backdrop_path: String?,
) {
    val posterUrl: String
        get() = "https://image.tmdb.org/t/p/w342${poster_path ?: "/fCayJrkfRaCRCTh8GqN30f8oyQF.jpg"}"
    val backdropUrl: String
        get() = "https://image.tmdb.org/t/p/w780${backdrop_path ?: "/fCayJrkfRaCRCTh8GqN30f8oyQF.jpg"}"
}

data class Movie(
    @field:Json(name="id") val id: Int,
    @field:Json(name="title") val title: String?,
    @field:Json(name="poster_path") val poster_path: String?,
    @field:Json(name="backdrop_path") val backdrop_path: String?,
    @field:Json(name="overview") val overview: String?,
    @field:Json(name="tagline") val tagline: String,
    @field:Json(name="genres") val genres: List<Genre>,
    @field:Json(name="images") val images: MovieImages,
    @field:Json(name="credits") val credits: MovieCredits,
) {
    val posterUrl: String
        get() = "https://image.tmdb.org/t/p/w342/$poster_path"
    val backdropUrl: String
        get() = "https://image.tmdb.org/t/p/w300/$backdrop_path"
}

data class Genre(
    @field:Json(name="id") val id: Int,
    @field:Json(name="name") val name: String,
)

data class MovieImages(
    @field:Json(name="posters") val posters: List<MovieImage>,
    @field:Json(name="backdrops") val backdrops: List<MovieImage>
)

data class MovieImage(
    @field:Json(name="aspect_ratio") val aspect_ratio: Double,
    @field:Json(name="file_path") val file_path: String,
    @field:Json(name="height") val height: Int,
    @field:Json(name="width") val width: Int
)

data class MovieCredits(
    @field:Json(name="cast") val cast: List<CastMember>
)

data class CastMember(
    @field:Json(name="id") val id: Int,
    @field:Json(name="cast_id") val cast_id: Int,
    @field:Json(name="credit_id") val credit_id: String,
    @field:Json(name="character") val character: String,
    @field:Json(name="name") val name: String,
    @field:Json(name="profile_path") val profile_path: String?
) {
    val profileUrl: String
        get() = "https://image.tmdb.org/t/p/w185${profile_path ?: ""}"
}
