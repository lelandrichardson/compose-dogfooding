package com.example.pmapp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp


@Composable
fun Playground() {
  BasicForm()
}


val items = listOf(
  "foo",
  "bar",
  "baz",
  "hello this is a long string"
)

val TextFieldValue.unselectedText: String get() = text.removeRange(selection.start, selection.end)

class TypeAheadTransformation(val items: List<String>) : VisualTransformation {
  override fun filter(text: AnnotatedString): TransformedText {
    val prefix = text.text
    val result = items.firstOrNull { it.startsWith(prefix) }

    return when {
      prefix.isEmpty() || result == null -> TransformedText(text, OffsetMapping.Identity)
      else -> TransformedText(
        buildAnnotatedString {
          append(prefix)
          withStyle(SpanStyle(color = Color.Gray)) {
            append(result.substring(prefix.length))
          }
        },
        object : OffsetMapping {
          override fun originalToTransformed(offset: Int): Int = offset
          override fun transformedToOriginal(offset: Int): Int = offset.coerceAtMost(prefix.length)
        }
      )
    }
  }
}

@Composable fun TypeAhead2() {
  var value by remember { mutableStateOf("") }
  val transformation = remember { TypeAheadTransformation(items) }
  TextField(
    value = value,
    visualTransformation = transformation,
    onValueChange = { value = it}
  )
}

@Composable fun TypeAhead1() {
  var value by remember { mutableStateOf(TextFieldValue("")) }
  TextField(
    value = value,
    onValueChange = { next ->
      val prefix = next.text
      val result = items.firstOrNull { it.startsWith(prefix) }
      value = when {
        value.text == prefix -> next
        prefix.isEmpty() -> next
        result == null -> next
        // user hit backspace
        value.unselectedText == next.text && next.selection.length == 0 -> next.copy(
          text = result,
          selection = TextRange(prefix.length - 1, result.length)
        )
        else -> next.copy(
          text = result,
          selection = TextRange(prefix.length, result.length)
        )
      }
    }
  )
}


@Composable fun BasicForm() {
  var value1 by remember { mutableStateOf("") }
  val focus1 = remember { FocusRequester() }
  var value2 by remember { mutableStateOf("") }
  val focus2 = remember { FocusRequester() }
  var value3 by remember { mutableStateOf("") }
  val focus3 = remember { FocusRequester() }
  var value4 by remember { mutableStateOf("") }
  val focus4 = remember { FocusRequester() }
  val focusRequester = remember { FocusRequester() }

  Column(Modifier.focusRequester(focusRequester)) {
    Button(onClick={
      focus3.requestFocus()
    }) {
      Text("Enter But more! Value 3")
    }

    TextField(
      modifier = Modifier
        .focusRequester(focus2),
      label = { Text("Valuawdawde 2") },
      value = value2,
      keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
      keyboardActions = KeyboardActions(onNext = { focus3.requestFocus() }),
      onValueChange = { value2 = it }
    )

    TextField(
      modifier = Modifier
        .focusRequester(focus1),
      label = { Text("Vaadawawdawddwlue 1") },
      value = value1,
      keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
      keyboardActions = KeyboardActions(onNext = { focus2.requestFocus() }),
      onValueChange = { value1 = it }
    )
    Button({}) { Text("This is just neat....?")}

    TextField(
      modifier = Modifier
        .focusRequester(focus3),
      label = { Text("Valuawdawde 3") },
      value = value3,
      keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
      keyboardActions = KeyboardActions(onNext = { focus4.requestFocus() }),
      onValueChange = { value3 = it }
    )

    TextField(
      modifier = Modifier
        .focusRequester(focus4),
      label = { Text("Value 4") },
      value = value4,
      keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
      keyboardActions = KeyboardActions(onNext = {
        // hide software keyboard?
      }),
      onValueChange = { value4 = it }
    )
  }
}







private fun String.substringSafe(start: Int, end: Int = length): String {
  return when {
    start > length -> ""
    end >= length -> substring(start)
    else -> substring(start, end)
  }
}

object PhoneNumberTransformation: VisualTransformation {
  private val mutedStyle = SpanStyle(color=Color.Gray)

  override fun filter(text: AnnotatedString): TransformedText {
    val rawText = text.text
    val countryCode: String
    val areaCode: String
    val firstNumbers: String
    val secondNumbers: String
    when (rawText.length) {
      // 012 345 6789
      in 0..10 -> {
        countryCode = ""
        areaCode = rawText.substringSafe(0, 3)
        firstNumbers = rawText.substringSafe(3, 6)
        secondNumbers = rawText.substringSafe(6)
      }
      else -> {
        countryCode = ""
        areaCode = rawText.substringSafe(0, 3)
        firstNumbers = rawText.substringSafe(3, 6)
        secondNumbers = rawText.substringSafe(6)
      }
    }
    // 5554 -> (555) 4
    // +1 (555) 444-6666
    return TransformedText(
      buildAnnotatedString {
        withStyle(mutedStyle) {
          append("(")
        }
        append(areaCode)
        if (areaCode.length < 3) {
          withStyle(mutedStyle) {
            append("#".repeat(3 - areaCode.length))
          }
        }
        withStyle(mutedStyle) {
          append(")")
        }
        append(firstNumbers)
        if (firstNumbers.length < 3) {
          withStyle(mutedStyle) {
            append("#".repeat(3 - firstNumbers.length))
          }
        }
        withStyle(mutedStyle) {
          append("-")
        }
        append(secondNumbers)
        if (secondNumbers.length < 4) {
          withStyle(mutedStyle) {
            append("#".repeat(4 - secondNumbers.length))
          }
        }
      },
      object : OffsetMapping {
        // (012)345-6789
        // 0123456789
        override fun originalToTransformed(offset: Int): Int = when {
          offset < 3 -> offset + 1
          offset < 6 -> offset + 2
          else -> offset + 3
        }

        override fun transformedToOriginal(offset: Int): Int = when {
          offset <= 1 -> 0
          offset < 4 -> offset - 1
          offset < 8 -> offset - 2
          else -> offset - 3
        }
      }
    )
  }
}

// visual transformation, phone number
@Composable fun PhoneNumberField() {
  var phoneNumber by remember { mutableStateOf("") }
  Column {
    TextField(
      modifier = Modifier.padding(16.dp),
      value = phoneNumber,
      onValueChange = {
        phoneNumber = it.replace(Regex("\\D"), "")
      },
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
      visualTransformation = PhoneNumberTransformation,
      maxLines = 1
    )
  }
}

// grow the textfield as the value grows
@Composable fun GrowingTextField() {
  var message by remember { mutableStateOf("") }
  var expanded by remember { mutableStateOf(true) }

  Column {
    Checkbox(checked = expanded, onCheckedChange = { expanded = it })

    TextField(
      modifier = Modifier
        .padding(16.dp)
        .height(120.dp),
      value = message,
      onValueChange = { message = it },
//      maxLines = if (expanded) Int.MAX_VALUE else 1
    )
  }
}