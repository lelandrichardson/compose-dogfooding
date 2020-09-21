package com.example.dogfood

import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.rememberScrollableController
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.gesture.scrollorientationlocking.Orientation
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ConfigurationAmbient
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.*
import androidx.compose.ui.draw.drawOpacity
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.platform.InspectableParameter
import androidx.compose.ui.platform.ParameterElement
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.ui.tooling.preview.Preview
import dev.chrisbanes.accompanist.coil.CoilImage
import java.lang.Math.abs
import kotlin.math.roundToInt

data class Movie(
        val title: String,
        val posterUrl: String,
        val bgUrl: String,
        val chips: List<String>
)

val movies = listOf(
    Movie(
        title = "Good Boys",
        posterUrl = "https://m.media-amazon.com/images/M/MV5BMTc1NjIzODAxMF5BMl5BanBnXkFtZTgwMTgzNzk1NzM@._V1_.jpg",
        bgUrl = "https://m.media-amazon.com/images/M/MV5BMTc1NjIzODAxMF5BMl5BanBnXkFtZTgwMTgzNzk1NzM@._V1_.jpg",
        chips = listOf("Action", "Drama", "History")
    ),
    Movie(
        title = "Joker",
        posterUrl = "https://i.etsystatic.com/15963200/r/il/25182b/2045311689/il_794xN.2045311689_7m2o.jpg",
        bgUrl = "https://images-na.ssl-images-amazon.com/images/I/61gtGlalRvL._AC_SY741_.jpg",
        chips = listOf("Action", "Drama", "History")
    ),
    Movie(
        title = "The Hustle",
        posterUrl = "https://m.media-amazon.com/images/M/MV5BMTc3MDcyNzE5N15BMl5BanBnXkFtZTgwNzE2MDE0NzM@._V1_.jpg",
        bgUrl = "https://m.media-amazon.com/images/M/MV5BMTc3MDcyNzE5N15BMl5BanBnXkFtZTgwNzE2MDE0NzM@._V1_.jpg",
        chips = listOf("Action", "Drama", "History")
    )
)

val posterAspectRatio = .674f

@Preview
@Composable
fun Screen() {
    val configuration = ConfigurationAmbient.current
    val density = DensityAmbient.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthPx = with(density) { screenWidth.toPx() }
    val screenHeight = configuration.screenHeightDp.dp
    val screenHeightPx = with(density) { screenHeight.toPx() }
    var offset by remember { mutableStateOf(0f) }
    val ctrlr = rememberScrollableController {
        offset += it
        it
    }
    val indexFraction = -1 * offset / screenWidthPx
    Stack(
        Modifier
            .background(Color.Black)
            .fillMaxSize()
            .scrollable(
                Orientation.Horizontal,
                ctrlr,
            )
    )  {
        movies.forEachIndexed { index, movie ->
            val opacity = if (indexFraction.roundToInt() == index) 1f else 0f
            CoilImage(
                    data = movie.bgUrl,
                    modifier = Modifier
                            .drawOpacity(opacity)
                            .fillMaxWidth()
                            .aspectRatio(posterAspectRatio)
            )
        }
        Spacer(
            modifier = Modifier
                    .gravity(Alignment.BottomEnd)
                    .verticalGradient(0f to Color.Transparent, 0.3f to Color.White, 1f to Color.White)
                    .fillMaxWidth()
                    .fillMaxHeight(0.6f)
        )
        movies.forEachIndexed { index, movie ->
            val center = screenWidthPx * index
            val distFromCenter = abs(offset - center) / screenWidthPx
            MoviePoster(
                    movie = movie,
                    modifier = Modifier
                            .offset(getX = { center + offset }, getY = { lerp(0f, -50f, distFromCenter) })
                            .width(screenWidth * .75f)
                            .gravity(Alignment.BottomCenter)
            )
        }
    }
}

fun Modifier.verticalGradient(vararg colors: ColorStop) = this then object : DrawModifier, InspectableParameter {

    // naive cache outline calculation if size is the same
    private var lastSize: Size? = null
    private var lastBrush: Brush? = null

    override fun ContentDrawScope.draw() {
        drawRect()
        drawContent()
    }

    private fun ContentDrawScope.drawRect() {
        var brush = lastBrush
        if (size != lastSize || brush == null) {
            brush = VerticalGradient(
                    *colors,
                    startY = 0f,
                    endY = size.height
            )
            lastSize = size
            lastBrush = brush
        }
        drawRect(brush = brush, alpha = 1f)
    }

    override val nameFallback = "verticalGradient"

    override val valueOverride: Any?
        get() = colors

    override val inspectableElements: Sequence<ParameterElement>
        get() = sequenceOf(
            ParameterElement("colors", colors)
        )
}

fun Modifier.offset(
        getX: () -> Float,
        getY: () -> Float,
        rtlAware: Boolean = true
) = this then object : LayoutModifier {
    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureScope.MeasureResult {
        val placeable = measurable.measure(constraints)
        return layout(placeable.width, placeable.height) {
            if (rtlAware) {
                placeable.placeRelative(getX().roundToInt(), getY().roundToInt())
            } else {
                placeable.place(getX().roundToInt(), getY().roundToInt())
            }
        }
    }
}

@Composable fun MoviePoster(movie: Movie, modifier: Modifier = Modifier) {
    Column(modifier
        .clip(RoundedCornerShape(20.dp))
        .background(Color.White)
        .padding(20.dp)
    ) {
        CoilImage(
            movie.posterUrl,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(posterAspectRatio)
                .clip(RoundedCornerShape(10.dp))
        )
        Text(movie.title,
            fontSize = 24.sp,
            color = Color.Black
        )
        Row {
            for (chip in movie.chips) {
                Chip(chip)
            }
        }
        StarRating(9.0f)
    }
}

@Composable fun BuyTicketButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        backgroundColor = Color.DarkGray,
        elevation = 0.dp,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text("Buy Ticket", color = Color.White)
    }
}

@Composable fun StarRating(rating: Float) {

}

@Composable fun Chip(label: String, modifier: Modifier = Modifier) {
    Text(
        label,
        fontSize = 9.sp,
        color = Color.Gray,
        modifier = modifier
            .border(1.dp, Color.Gray, RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 2.dp)
    )
}