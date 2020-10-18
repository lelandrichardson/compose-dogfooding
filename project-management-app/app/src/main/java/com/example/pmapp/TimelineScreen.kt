package com.example.pmapp

import androidx.compose.foundation.Box
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.pmapp.ui.typography
import dev.chrisbanes.accompanist.coil.CoilImage

enum class Status {
    New,
    InProgress,
    Review,
    Done,
}

class Project(
    val id: Int,
    val title: String,
    val date: String,
    val days: Int,
    val status: Status,
    val users: List<User>,
    val tasks: List<Task>
)
class User(
    val id: Int,
    val name: String,
) {
    fun imageUrlForSize(size: Int) = "https://i.pravatar.cc/150?img=$id"
}
class Task(
    val id: Int,
    val timeCode: String,
    val title: String,
    val tag: String,
    val status: Status,
    val assignees: List<User>,
    val commentCount: Int,
    val attachmentCount: Int,
)

val zachary = User(
    id = 2,
    name = "Zachary Butler",
)
val mary = User(
    id = 3,
    name = "Mary Brown",
)
val sarah = User(
    id = 4,
    name = "Sarah Murphy"
)
val mockProject = Project(
    id = 1,
    title = "Create additional pages",
    date = "Dec 18, 2019",
    days = 3,
    status = Status.InProgress,
    users = listOf(mary, sarah, zachary),
    tasks = listOf(
        Task(
            id = 163,
            timeCode = "24.19",
            title = "Contact page",
            tag = "#Design",
            status = Status.InProgress,
            assignees = listOf(zachary),
            commentCount = 3,
            attachmentCount = 5,
        ),
        Task(
            id = 158,
            timeCode = "24.19",
            title = "Calculator page",
            tag = "#Design",
            status = Status.Done,
            assignees = listOf(sarah, mary),
            commentCount = 8,
            attachmentCount = 2,
        ),
        Task(
            id = 157,
            timeCode = "23.19",
            title = "Technical Task",
            tag = "#Frontend",
            status = Status.Review,
            assignees = listOf(zachary),
            commentCount = 4,
            attachmentCount = 8,
        ),
        Task(
            id = 159,
            timeCode = "23.19",
            title = "Calculator page",
            tag = "#Backend",
            status = Status.Done,
            assignees = listOf(mary),
            commentCount = 4,
            attachmentCount = 6,
        ),
        Task(
            id = 163,
            timeCode = "22.19",
            title = "Contact page",
            tag = "#Design",
            status = Status.InProgress,
            assignees = listOf(zachary),
            commentCount = 3,
            attachmentCount = 5,
        ),
        Task(
            id = 158,
            timeCode = "22.19",
            title = "Calculator page",
            tag = "#Design",
            status = Status.Done,
            assignees = listOf(sarah, mary),
            commentCount = 8,
            attachmentCount = 2,
        ),
        Task(
            id = 157,
            timeCode = "21.19",
            title = "Technical Task",
            tag = "#Frontend",
            status = Status.Review,
            assignees = listOf(zachary),
            commentCount = 4,
            attachmentCount = 8,
        ),
        Task(
            id = 159,
            timeCode = "21.19",
            title = "Calculator page",
            tag = "#Backend",
            status = Status.Done,
            assignees = listOf(mary),
            commentCount = 4,
            attachmentCount = 6,
        ),
    )
)


@Composable
fun TimelineScreen(project: Project = mockProject) {
    Column {
        Text(project.title, style = typography.h1)
        Row {
            Text("${project.days} days", style = typography.body2)
            Text("|", modifier = Modifier.padding(horizontal=4.dp), style = typography.body2)
            Text(project.date, style = typography.body2)
        }
        AvatarList(users = project.users)
    }
}


private fun Modifier.layoutOffset(x: Dp = 0.dp, y: Dp = 0.dp) = this then layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    layout(placeable.width + x.toIntPx(), placeable.height + y.toIntPx()) {
        placeable.placeRelative(x.toIntPx(), y.toIntPx())
    }
}

@Composable fun AvatarList(users: List<User>) {
    Row {
        users.forEachIndexed { index, user ->
            Avatar(
                user,
                modifier = Modifier.layoutOffset(if (index == 0) 0.dp else -10.dp)
            )
        }
    }
}

@Composable fun Avatar(user: User, modifier: Modifier = Modifier) {
    CoilImage(
        user.imageUrlForSize(with(DensityAmbient.current) { 40.dp.toIntPx() }),
        modifier = modifier
            .drawShadow(5.dp, CircleShape, clip = false)
            .background(Color.White, CircleShape)
            .padding(2.dp)
            .clip(CircleShape)
            .size(40.dp)
    )
}













