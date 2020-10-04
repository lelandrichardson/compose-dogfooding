package com.example.dogfood

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Text
import androidx.compose.foundation.animation.AndroidFlingDecaySpec
import androidx.compose.foundation.animation.FlingConfig
import androidx.compose.foundation.animation.defaultFlingConfig
import androidx.compose.foundation.animation.fling
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.ScrollableController
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.snapshotFlow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.gesture.scrollorientationlocking.Orientation
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.*
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.*
import androidx.ui.tooling.preview.Preview
import dev.chrisbanes.accompanist.coil.CoilImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.coroutines.CoroutineContext
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.reflect.KProperty

@Immutable
data class Movie(
        val title: String,
        val posterUrl: String,
        val bgUrl: String,
        val color: Color,
        val chips: List<String>
)

val movies = listOf(
    Movie(
        title = "Good Boys",
        posterUrl = "https://m.media-amazon.com/images/M/MV5BMTc1NjIzODAxMF5BMl5BanBnXkFtZTgwMTgzNzk1NzM@._V1_.jpg",
        bgUrl = "https://m.media-amazon.com/images/M/MV5BMTc1NjIzODAxMF5BMl5BanBnXkFtZTgwMTgzNzk1NzM@._V1_.jpg",
        color = Color.Red,
        chips = listOf("Action", "Drama", "History")
    ),
    Movie(
        title = "Joker",
        posterUrl = "https://i.etsystatic.com/15963200/r/il/25182b/2045311689/il_794xN.2045311689_7m2o.jpg",
        bgUrl = "https://images-na.ssl-images-amazon.com/images/I/61gtGlalRvL._AC_SY741_.jpg",
        color = Color.Blue,
        chips = listOf("Action", "Drama", "History")
    ),
    Movie(
        title = "The Hustle",
        posterUrl = "https://m.media-amazon.com/images/M/MV5BMTc3MDcyNzE5N15BMl5BanBnXkFtZTgwNzE2MDE0NzM@._V1_.jpg",
        bgUrl = "https://m.media-amazon.com/images/M/MV5BMTc3MDcyNzE5N15BMl5BanBnXkFtZTgwNzE2MDE0NzM@._V1_.jpg",
        color = Color.Yellow,
        chips = listOf("Action", "Drama", "History")
    )
)

val posterAspectRatio = .674f

@Preview
@Composable
fun Screen() {
    val configuration = ConfigurationAmbient.current
    val screenWidth = configuration.screenWidthDp.dp
    val posterWidthDp = screenWidth * 0.6f
    val posterSpacingDp = posterWidthDp + 20.dp
    val carouselState = rememberCarouselState()
    Stack {
        Carousel(
            items = movies,
            state = carouselState,
            spacing = posterSpacingDp,
            getBackgroundImage = { it.bgUrl },
            getForegroundImage = { it.posterUrl },
        ) { movie, expanded ->
            MoviePoster(
                movie = movie,
                expanded = expanded,
                expandedWidth = screenWidth,
                normalWidth = posterWidthDp,
            )
        }
        BuyTicketButton(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .width(posterWidthDp)
                .padding(20.dp),
            onClick = {
                carouselState.expandSelectedItem()
            }
        )
    }
}

class ValueHolder<T>(var value: T)

operator fun <T> ValueHolder<T>.getValue(thisRef: Any?, property: KProperty<*>) = value
operator fun <T> ValueHolder<T>.setValue(thisRef: Any?, property: KProperty<*>, value: T) {
    this.value = value
}

private fun indexToOffset(index: Int, spacingPx: Float): Float = -1 * index * spacingPx
private fun offsetToIndex(offset: Float, spacingPx: Float): Int =
    floor(-1 * offset / spacingPx).toInt()

@Composable fun rememberCarouselState(): CarouselState {
    val density = DensityAmbient.current
    val clock = AnimationClockAmbient.current.asDisposableClock()
    val animatedOffset = animatedFloat(0f)
    val expanded = animatedFloat(0f)
    return remember(clock, density) {
        CarouselState(
            density,
            animatedOffset,
            expanded,
            clock,
        )
    }
}

class CarouselState(
    private val density: Density,
    internal val animatedOffset: AnimatedFloat,
    internal val expanded: AnimatedFloat,
    clock: AnimationClockObservable
) {
    private val flingConfig = FlingConfig(AndroidFlingDecaySpec(density)) { adjustTarget(it) }
    internal val scrollableController = ScrollableController({ consumeScrollDelta(it) }, flingConfig, clock)
    var expandedIndex by mutableStateOf<Int?>(null)
        private set
    private var itemCount: Int = 0
    internal var spacingPx: Float = 0f
    internal fun update(count: Int, spacing: Dp) {
        itemCount = count
        spacingPx = with(density) { spacing.toPx() }
    }
    private val upperBound: Float = 0f
    private val lowerBound: Float get() = -1 * (itemCount - 1) * spacingPx

    private fun adjustTarget(target: Float): TargetAnimation? {
        return TargetAnimation((target / spacingPx).roundToInt() * spacingPx)
    }
    private fun consumeScrollDelta(delta: Float): Float {
        var target = animatedOffset.value + delta
        var consumed = delta
        when {
            target > upperBound -> {
                consumed = upperBound - animatedOffset.value
                target = upperBound
            }
            target < lowerBound -> {
                consumed = lowerBound - animatedOffset.value
                target = lowerBound
            }
        }
        animatedOffset.snapTo(target)
        return consumed
    }
    fun expandSelectedItem() {
        if (expandedIndex != null) {
            expandedIndex = null
            expanded.animateTo(0f)
        } else {
            expandedIndex = selectedIndex
            expanded.animateTo(1f)
        }
    }
    val selectedIndex: Int get() = offsetToIndex(animatedOffset.value, spacingPx)
}


@Composable fun <T> Carousel(
    items: List<T>,
    spacing: Dp,
    state: CarouselState,
    getBackgroundImage: (T) -> Any,
    getForegroundImage: (T) -> Any,
    foregroundContent: @Composable (item: T, expanded: Boolean) -> Unit
) {
    // TODO: think more about this
    state.update(items.size, spacing)
    val spacingPx = state.spacingPx
    val animatedOffset = state.animatedOffset
    val expanded = state.expanded
    Stack(
        Modifier
            .background(Color.Black)
            .fillMaxSize()
            .scrollable(
                Orientation.Horizontal,
                state.scrollableController,
            )
    ) {
        items.forEachIndexed { index, item ->
            CoilImage(
                data = getBackgroundImage(item),
                modifier = Modifier
                    .carouselBackground(
                        index = index,
                        getIndexFraction = { -1 * animatedOffset.value / spacingPx },
                        getExpandedFraction = { expanded.value }
                    )
                    .fillMaxWidth()
                    .aspectRatio(posterAspectRatio)
            )
            if (index != state.expandedIndex) {
                CoilImage(
                        data = getForegroundImage(item),
                        modifier = Modifier
                                .carouselExpandedBackground(
                                        index = index,
                                        getIndexFraction = { -1 * animatedOffset.value / spacingPx },
                                        getExpandedFraction = { expanded.value }
                                )
                                .offset(getX = {
                                    (index - state.selectedIndex) * 0.75f * spacingPx
                                }, getY = { 0f })
                                .align(Alignment.BottomCenter)
                                .width(spacing)
                                .aspectRatio(posterAspectRatio)
                )
            }
        }
        state.expandedIndex?.let {
            CoilImage(
                    data = getForegroundImage(items[it]),
                    modifier = Modifier
                            .carouselExpandedBackground(
                                    index = it,
                                    getIndexFraction = { -1 * animatedOffset.value / spacingPx },
                                    getExpandedFraction = { expanded.value }
                            )
                            .offset(
                                    getX = { 0f },
                                    getY = { 0f }
                            )
                            .align(Alignment.BottomCenter)
                            .width(spacing)
                            .aspectRatio(posterAspectRatio)
            )
        }
        Spacer(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .verticalGradient(0f to Color.Transparent, 0.3f to Color.White, 1f to Color.White)
                .fillMaxWidth()
                .fillMaxHeight(0.6f)
        )
        items.forEachIndexed { index, item ->
            val center = spacingPx * index
            Column(
                Modifier
                    .zIndex(if (state.expandedIndex == index) 1f else 0f)
                    .offset(getX = {
                        center + animatedOffset.value
                    }, getY = {
                        val distFromCenter = abs(animatedOffset.value + center) / spacingPx
                        lerp(0f, 50f, distFromCenter)
                    })
                    .align(Alignment.TopCenter)
            ) {
                foregroundContent(item, state.expandedIndex == index)
            }
        }
    }
}

@Composable fun Scope(content: @Composable () -> Unit) = content()

fun Modifier.carouselExpandedBackground(
    index: Int,
    getIndexFraction: () -> Float,
    getExpandedFraction: () -> Float,
) = this then object : DrawLayerModifier {
    override val alpha: Float
        get() {
            return getExpandedFraction()
        }

    override val translationY: Float
        get() {
            return lerp(0f, -400f, getExpandedFraction())
        }
}

fun Modifier.carouselBackground(
    index: Int,
    getIndexFraction: () -> Float,
    getExpandedFraction: () -> Float,
) = this then object : DrawLayerModifier {
    override val alpha: Float
        get() {
            val indexFraction = getIndexFraction()
            val leftIndex = floor(indexFraction).toInt()
            val rightIndex = ceil(indexFraction).toInt()
            return if (index == leftIndex || index == rightIndex)
                1f - getExpandedFraction()
            else
                0f
        }
    override val shape: Shape
        get() {
            val indexFraction = getIndexFraction()
            val leftIndex = floor(indexFraction).toInt()
            val rightIndex = ceil(indexFraction).toInt()
            return when (index) {
                rightIndex -> {
                    // 1, 0.25 -> 0f, 0.25
                    val fraction = indexFraction - index + 1
                    FractionalRectangleShape(0f, fraction)
                }
                leftIndex -> {
                    // 0, 0.25 -> 0.25, 1f
                    val fraction = indexFraction - index
                    FractionalRectangleShape(fraction, 1f)
                }
                else -> RectangleShape
            }
        }
    override val clip: Boolean
        get() = true
}

fun FractionalRectangleShape(startFraction: Float, endFraction: Float) = object : Shape {
    override fun createOutline(size: Size, density: Density) =
            Outline.Rectangle(
                    Rect(
                            top = 0f,
                            left = (startFraction * size.width).coerceAtMost(size.width - 1f),
                            bottom = size.height,
                            right = (endFraction * size.width).coerceAtLeast(1f)
                    )
            )
}

fun lerp(start: Float, stop: Float, fraction: Float): Float {
    return (1 - fraction) * start + fraction * stop
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

@OptIn(ExperimentalComposeApi::class)
fun AnimatedFloat.tracks(other: AnimatedFloat, scope: CoroutineScope) {
    snapshotFlow { other.value }
        .onEach {
            animateTo(it)
        }.launchIn(scope)
}


val imageWidthKey = FloatPropKey()
val imageScaleKey = FloatPropKey()
val posterTransition = transitionDefinition<Boolean> {

}

@Composable fun MoviePoster(
        movie: Movie,
        expanded: Boolean,
        expandedWidth: Dp,
        normalWidth: Dp,
        modifier: Modifier = Modifier
) {
    val posterPadding = 20.dp
    val fullImageWidth = normalWidth - 2 * posterPadding
    val t = animatedFloat(if (expanded) 1f else 0f)
    onCommit(expanded) {
        t.animateTo(if (expanded) 1f else 0f)
    }
    val t2 = animatedFloat(0f)
    val scope = rememberCoroutineScope()
    onActive {
        t2.tracks(t, scope)
    }
    val imageScale = lerp(1f, 0f, t.value)
    val imageAlpha = lerp(1f, 0f, t.value)
    val imageWidth = lerp(fullImageWidth.value, 0f, t2.value)
    Column(modifier
        .width(if (expanded) expandedWidth else normalWidth)
        .padding(top = 200.dp)
        .clip(RoundedCornerShape(20.dp))
        .background(Color.White)
        .padding(posterPadding)
        .padding(bottom = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CoilImage(
            movie.posterUrl,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .drawLayer(
                    scaleX = imageScale,
                    scaleY = imageScale,
                    alpha = imageAlpha,
                )
                .width(imageWidth.dp)
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

@Composable fun BuyTicketButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        backgroundColor = Color.DarkGray,
        elevation = 0.dp,
        modifier = modifier
            .padding(vertical = 10.dp)
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