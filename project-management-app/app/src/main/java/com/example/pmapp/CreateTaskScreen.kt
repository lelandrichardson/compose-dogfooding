package com.example.pmapp

import androidx.compose.foundation.Icon
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.IconButton
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

val bgColor = Color(0xFF33354E)

class Client(
    val id: Int,
    val name: String,
    val logo: String,
)

val mockClients = listOf(
    Client(
        id = 0,
        name = "Awsmd Team",
        logo = "",
    ),
    Client(
        id = 1,
        name = "Google",
        logo = "",
    ),
    Client(
        id = 2,
        name = "Airbnb",
        logo = "",
    ),
)


@Composable
fun CreateTaskScreen(clients: List<Client> = mockClients) {
  var projectName by remember { mutableStateOf("Create additional pages") }
  var client by remember { mutableStateOf(clients.first()) }
  var startDate by remember { mutableStateOf("") }
  var endDate by remember { mutableStateOf("") }
  var category by remember { mutableStateOf("Design") }

  ScrollableColumn(
      Modifier
          .background(bgColor)
          .padding(horizontal = 16.dp, vertical = 20.dp)
  ) {
    FieldLabel("PROJECT NAME")
    ClientDropdownField(
        selectedValue = client,
        values = clients,
        onValueChange = { client = it },
    )
    FieldLabel("PROJECT NAME")
    PMTextField(value = projectName, onValueChange = { projectName = it })
    Row {
      FieldLabel("START/END DATES")
      DatePicker(value = startDate, onValueChange = { startDate = it })
      DatePicker(value = endDate, onValueChange = { endDate = it })
    }
    FieldLabel("ASSIGNEE")
    AvatarList(
        size = 40.dp,
        users = mockProject.users,
        onUserClick = {},
        showAddButton = true,
        onAddClick = {}
    )
    FieldLabel("CATEGORY")
    RadioChips(
        selectedValue = category,
        onSelectedChange = { category = it },
        values = listOf("Design", "Frontend", "Backend")
    )
  }
}

@Composable fun FieldLabel(label: String) {
  Text(label)
}

@Composable fun RadioChips(
    selectedValue: String,
    values: List<String>,
    onSelectedChange: (String) -> Unit
) {
  Row {
    for(value in values) {
      val selected = value == selectedValue
      IconButton(
          modifier = Modifier.background(if (selected) Color.Green else Color.White),
          onClick = { onSelectedChange(value) },
      ) {
          Row {
            if (selected) Icon(Icons.Default.Check)
            Text(value, color = if (selected) Color.White else Color.Black)
          }
      }
    }
  }
}


@Composable
fun ClientDropdownField(
    selectedValue: Client,
    onValueChange: (Client) -> Unit,
    values: List<Client>,
) {

}

@Composable fun DatePicker(
  value: String,
  onValueChange: (String) -> Unit
) {
  TextField(value = value, onValueChange = onValueChange)
}

@Composable
fun <T> DropdownField(
    selectedValue: T,
    onValueChange: (T) -> Unit,
    values: List<T>,
    composeItem: @Composable (T) -> Unit
) {

}

@Composable
fun PMTextField(
    value: String,
    onValueChange: (String) -> Unit,
) {
  TextField(value = value, onValueChange = onValueChange)
}