package com.example.countdown

import android.graphics.Color
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.countdown.ui.theme.CountdownTheme
import android.view.WindowManager
import android.view.View
import android.view.Window
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    window.makeTransparentStatusBar()
    setContent {
      CountdownTheme {
        // A surface container using the 'background' color from the theme
        Surface(color = MaterialTheme.colors.background, modifier = Modifier.fillMaxSize()) {
          Countdown()
        }
      }
    }
  }
}

fun Window.makeTransparentStatusBar() {
  markAttributes(
    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
    true
  )
  decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
      or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
      or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
  markAttributes(
    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
    false
  )
  statusBarColor = Color.TRANSPARENT
  navigationBarColor = Color.TRANSPARENT
}

fun Window.markAttributes(bits: Int, value: Boolean) {
  val params = attributes
  if (value) {
    params.flags = params.flags or bits
  } else {
    params.flags = params.flags and bits.inv()
  }
  attributes = params
}