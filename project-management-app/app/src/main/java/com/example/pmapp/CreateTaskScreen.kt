package com.example.pmapp

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Stop
import androidx.compose.runtime.*
import androidx.compose.runtime.State
import androidx.compose.runtime.snapshots.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pmapp.ui.bgColor
import com.example.pmapp.ui.primaryGreen
import dev.chrisbanes.accompanist.coil.CoilImage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.sqrt


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


class Attachment(
  val name: String,
  val preview: String,
  val size: Int
) {
  val progress: Float get() = 0.8f
}

val mockAttachment = Attachment(
  name = "Reference_1",
  preview = "https://i.pravatar.cc/48?img=5",
  size = 168,
)

@Composable
fun CreateTaskScreen(clients: List<Client> = mockClients) {
  var projectName by remember { mutableStateOf("Create additional pages") }
  var client by remember { mutableStateOf(clients.first()) }
  var startDate by remember { mutableStateOf("") }
  var endDate by remember { mutableStateOf("") }
  var category by remember { mutableStateOf("Design") }
  val attachments = remember { mutableStateListOf(mockAttachment) }

  ScrollableColumn {
    Surface(
      color = bgColor,
      contentColor = Color.White,
    ) {
      Column {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 20.dp)) {
          Field("PROJECT NAME") {
            ClientDropdownField(
                selectedValue = client,
                values = clients,
                onValueChange = { client = it },
            )
          }

          Field("PROJECT NAME") {
            PMTextField(
              modifier = Modifier.weight(1f),
              value = projectName,
              onValueChange = { projectName = it }
            )
          }

          Field("START/END DATES") {
            DatePicker(value = startDate, onValueChange = { startDate = it })
            Text(" - ")
            DatePicker(value = endDate, onValueChange = { endDate = it })
          }

          Field("ASSIGNEE") {
            AvatarList(
                size = 40.dp,
                users = mockProject.users,
                onUserClick = {},
                showAddButton = true,
                onAddClick = {}
            )
          }

          Field("CATEGORY") {
            RadioChips(
                selectedValue = category,
                onSelectedChange = { category = it },
                values = listOf("Design", "Frontend", "Backend")
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(topLeft = 24.dp, topRight = 24.dp),
          color = Color.White
        ) {
          Column(Modifier.padding(horizontal = 16.dp, vertical = 24.dp)) {
            Text("Description")
            IconButton(onClick = {}) {
              Icon(Icons.Default.Attachment, tint = primaryGreen)
            }
            Text("ATTACHMENTS")
            for (attachment in attachments) {
              AttachmentProgress(attachment)
            }
            Button(
              modifier = Modifier.fillMaxWidth(),
              onClick = {}
            ) {
              Text("CREATE TASK")
            }
          }
        }
      }
    }
  }
}

@Composable fun AttachmentProgress(attachment: Attachment) {
  Row {
    CoilImage(
      modifier = Modifier
        .size(48.dp)
        .clip(CircleShape)
        .background(Color.White),
      data = attachment.preview,
    )
    Spacer(Modifier.size(8.dp))
    Column(Modifier.weight(1f)) {
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(attachment.name)
        Text("${attachment.size} KB")
      }
      LinearProgressIndicator(
        modifier = Modifier.fillMaxWidth(),
        progress = attachment.progress,
        color = primaryGreen
      )
    }
    IconButton(onClick = {}) {
      Icon(Icons.Default.Stop)
    }
  }
}

@Composable
inline fun Field(label: String, content: @Composable RowScope.() -> Unit) {
  Column(Modifier.padding(vertical = 8.dp)) {
    Text(
      label,
      color = Color.White.copy(alpha = 0.7f),
      fontSize = 10.sp,
      modifier = Modifier.padding(bottom=8.dp)
    )
    Row { content() }
  }
}

@Composable fun RadioChips(
    selectedValue: String,
    values: List<String>,
    onSelectedChange: (String) -> Unit
) {
  val selectedColor = ButtonConstants.defaultButtonColors(backgroundColor = primaryGreen, contentColor = Color.White)
  val unselectedColor = ButtonConstants.defaultButtonColors(backgroundColor = Color.White)
  Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
    for(value in values) {
      val selected = value == selectedValue
      Button(
        contentPadding = PaddingValues(start = 8.dp, end = 24.dp, top = 4.dp, bottom = 4.dp),
        colors = if (selected) selectedColor else unselectedColor,
        shape = RoundedCornerShape(8.dp),
        onClick = { onSelectedChange(value) },
      ) {
        if (selected)
          Icon(
            asset = Icons.Default.Check,
            modifier = Modifier.size(16.dp)
          )
        else
          Spacer(Modifier.size(16.dp))
        Text(
          value,
          modifier = Modifier.padding(start=4.dp),
          color = if (selected) Color.White else Color.Black
        )
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
  Row {
    CoilImage(
      modifier = Modifier
        .size(48.dp)
        .clip(CircleShape)
        .background(Color.White),
      data = "https://i.pravatar.cc/200?img=2"
    )
    Spacer(Modifier.size(6.dp))
    PMTextField(value = selectedValue.name, onValueChange = {})
  }
}

@Composable fun DatePicker(
  value: String,
  onValueChange: (String) -> Unit
) {
  PMTextField(value = value, onValueChange = onValueChange)
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
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
) {
  TextField(
    modifier = modifier,
    value = value,
    onValueChange = onValueChange,
    textStyle = TextStyle(color = Color.White),
    activeColor = Color.White,
    inactiveColor = Color.White.copy(alpha=0.7f),
    backgroundColor = bgColor
  )
}