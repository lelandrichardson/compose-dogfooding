package com.example.countdown

import android.graphics.Matrix
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.CacheDrawScope
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toOffset
import androidx.compose.ui.util.lerp
import androidx.core.content.ContextCompat.getSystemService
import com.example.countdown.ui.theme.bgColorCenter
import com.example.countdown.ui.theme.bgColorEdge
import com.example.countdown.ui.theme.darkRed
import com.example.countdown.ui.theme.lightOrange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.*

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Countdown() {
  val scope = rememberCoroutineScope()
  val vibrator = systemService<Vibrator>()
  val state = remember { TickwheelState(scope, vibrator) }
  Box(
    Modifier
      .fillMaxSize()
      .background(Brush.radialGradient(listOf(bgColorCenter, bgColorEdge))),
    contentAlignment = Alignment.Center
  ) {
    TickWheel(
      Modifier.fillMaxWidth(),
      state = state,
      ticks = 60,
      startColor = lightOrange,
      endColor = darkRed,
    ) {
      Text(
        state.timeLeftDisplay,
        color = Color.White,
        fontSize = 48.sp,
        textAlign = TextAlign.Center
      )
    }
    AnimatedVisibility(
      state.totalSeconds > 0,
      Modifier
        .align(Alignment.BottomCenter)
        .padding(bottom = 80.dp)
    ) {
      Row {
        IconButton(onClick = { state.toggle() }) {
          Icon(
            if (state.isCountingDown) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = if (state.isCountingDown) "Pause" else "Start",
          )
        }
        IconButton(onClick = { state.clear() }) {
          Icon(Icons.Default.Clear, contentDescription = "Clear")
        }
      }
    }
  }
}

class TickwheelState(
  private val scope: CoroutineScope,
  private val vibrator: Vibrator?
) {
  var totalSeconds by mutableStateOf(0)
    private set
  private var isDragging by mutableStateOf(false)
  private var endPosition by mutableStateOf<Offset?>(null)
  private var job by mutableStateOf<Job?>(null)
  val seconds: Int
    get() = totalSeconds % 60
  val minutes: Int
    get() = floor(totalSeconds.toDouble() / 60).toInt()
  val isCountingDown: Boolean
    get() = job != null
  val timeLeftDisplay: String
    get() {
      return buildString {
        append("$minutes".padStart(2, '0'))
        append(":")
        append("$seconds".padStart(2, '0'))
      }
    }

  fun endDrag() {
    val current = endPosition
    if (current != null) {
      isDragging = false
    } else {
      error("Position was null when it shouldn't have been")
    }
  }

  fun startDrag(startPosition: Offset) {
    isDragging = true
    endPosition = startPosition
    stop()
  }

  fun onDrag(delta: Offset) {
    val prev = endPosition
    val prevSeconds = totalSeconds
    val next = if (prev != null) {
      val prevTheta = prev.theta
      val next = prev + delta
      val nextTheta = next.theta
      val nextMinutes = when {
        prevTheta > 90f && nextTheta < -90f -> minutes + 1
        prevTheta < -90f && nextTheta > 90f -> max(0, minutes - 1)
        else -> minutes
      }
      val nextSeconds = floor((nextMinutes) * 60 + ((next.theta + 180f) / 360f * 60f)).toInt()
      if (nextSeconds != prevSeconds) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
          vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
        }
      }
      totalSeconds = nextSeconds
      next
    } else {
      delta
    }
    endPosition = next
  }

  fun toggle() {
    if (job == null) {
      job = scope.launch {
        while (totalSeconds > 0) {
          delay(1000)
          countDown()
        }
        endPosition = null
      }
    } else {
      stop()
    }
  }

  fun clear() {
    stop()
    totalSeconds = 0
    endPosition = null
  }

  private fun stop() {
    job?.cancel()
    job = null
  }

  private fun countDown() {
    val next = totalSeconds - 1
    val theta = (((next % 60) * 6 - 180) * PI / 180).toFloat()
    val radius = 100f
    totalSeconds = next
    endPosition = Offset(
      cos(theta) * radius,
      sin(theta) * radius
    )
  }
}

const val TickWidth = 9f
const val Epsilon = 9f
const val RadiusA = 0.36f
const val RadiusB = 0.40f
const val RadiusC = 0.48f
const val RadiusD = 0.75f
const val RadiusE = 1.4f

@Composable fun TickWheel(
  modifier: Modifier,
  ticks: Int,
  startColor: Color,
  endColor: Color,
  state: TickwheelState,
  content: @Composable () -> Unit
) {
  var origin by remember { mutableStateOf(Offset.Zero) }
  val minuteTransition by animateFloatAsState(state.minutes.toFloat())
  Box(
    modifier
      .aspectRatio(1f)
      .onSizeChanged { origin = it.center.toOffset() }
      .pointerInput(Unit) {
        detectDragGestures(
          onDragStart = { offset ->
            state.startDrag(offset - origin)
          },
          onDragEnd = {
            state.endDrag()
          },
          onDragCancel = {
            state.endDrag()
          },
          onDrag = { change, amount ->
            state.onDrag(amount)
            change.consumeAllChanges()
          }
        )
      }
      .drawWithCache {
        val unitRadius = size.width / 2f
        val a = unitRadius * RadiusA
        val b = unitRadius * RadiusB
        val c = unitRadius * RadiusC
        val d = unitRadius * RadiusD
        val e = unitRadius * RadiusE

        val offBrush = Color.White
          .copy(alpha = 0.1f)
          .toBrush()
        val sweep = Brush.sweepGradient(
          startColor,
          endColor,
          center,
          // use a little over 180deg so that the first "tick" mark isn't split down the middle
          -182f
        )
        onDrawBehind {
          val endAngle = state.seconds * 6 - 180
          val minutes = state.minutes
          for (i in 0 until ticks) {
            val angle = i * (360 / ticks) - 180 // -180 to 180
            val theta = angle * PI.toFloat() / 180f // radians
            val onBrush = if (angle < endAngle) sweep else offBrush
            val up = minutes >= minuteTransition
            val t = 1 - abs(minutes - minuteTransition)
            if (up) {
              if (minutes > 1) {
                drawTick(sweep, theta, lerp(b, a, t), lerp(c, b, t), 1 - t)
              }
              if (minutes > 0) {
                drawTick(sweep, theta, lerp(c, b, t), lerp(d, c, t), 1f)
              }
              drawTick(onBrush, theta, lerp(d, c, t), lerp(e, d, t), t)
            } else {
              if (minutes > 0) {
                drawTick(sweep, theta, lerp(a, b, t), lerp(b, c, t), t)
              }
              drawTick(onBrush, theta, lerp(b, c, t), lerp(c, d, t), 1f)
              drawTick(offBrush, theta, lerp(c, d, t), lerp(d, e, t), 1 - t)
            }
          }
        }
      },
    contentAlignment = Alignment.Center
  ) {
    content()
  }
}

fun DrawScope.drawTick(
  brush: Brush,
  theta: Float,
  startRadius: Float,
  endRadius: Float,
  alpha: Float
) {
  drawLine(
    brush,
    center + Offset(
      cos(theta) * (startRadius + Epsilon),
      sin(theta) * (startRadius + Epsilon)
    ),
    center + Offset(
      cos(theta) * (endRadius - Epsilon),
      sin(theta) * (endRadius - Epsilon)
    ),
    TickWidth,
    StrokeCap.Round,
    alpha = alpha.coerceIn(0f, 1f)
  )
}

val Offset.theta: Float get() = (atan2(y.toDouble(), x.toDouble()) * 180.0 / PI).toFloat()

fun Color.toBrush(): Brush = SolidColor(this)

fun Brush.Companion.sweepGradient(startColor: Color, endColor: Color, center: Offset, startAngle: Float): Brush {
  val matrix = Matrix().also { it.setRotate(startAngle, center.x, center.y) }
  return ShaderBrush(android.graphics.SweepGradient(
      center.x,
      center.y,
      startColor.toArgb(),
      endColor.toArgb()
    )
    .also {
      it.setLocalMatrix(matrix)
    })
}
val CacheDrawScope.center: Offset get() = Offset(size.width / 2, size.height / 2)


@Composable
inline fun <reified T> systemService(): T? {
  val context = LocalContext.current
  return remember { getSystemService(context, T::class.java) }
}
