package com.example.pmapp

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.materialIcon
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawShadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.pmapp.ui.typography
import dev.chrisbanes.accompanist.coil.CoilImage
import kotlin.math.roundToInt

enum class Status(val color: Color, val humanReadable: String) {
    New(Color(0xFF2CC09C), "New"),
    InProgress(Color(0xFFF26950), "In Progress"),
    Review(Color.Red, "Review"),
    Done(Color(0xFF5A55CA), "Done"),
}

class Project(
    val id: Int,
    val title: String,
    val date: String,
    val days: Int,
    val status: Status,
    val progress: Float,
    val users: List<User>,
    val tasks: List<Task>
)
class User(
    val id: Int,
    val name: String,
) {
    fun imageUrlForSize(size: Int) = "https://i.pravatar.cc/$size?img=$id"
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
    progress = 0.85f,
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
    ScrollableColumn {
        Column(Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
            Text(project.title, style = typography.h1)
            Row {
                Text("${project.days} days", style = typography.body2)
                Text("|", modifier = Modifier.padding(horizontal=4.dp), style = typography.body2)
                Text(project.date, style = typography.body2)
            }
            Row(Modifier.padding(top = 16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                AvatarList(
                    size = 44.dp,
                    users = project.users,
                    showAddButton = true,
                    onAddClick={
                        // TODO
                    },
                    onUserClick = {
                        // TODO
                    }
                )
                ProjectProgressIndicator(
                    progress = project.progress,
                    status = project.status
                )
            }
        }

        Column(
            Modifier
                .background(Color(0xFFF1F5FE), shape = RoundedCornerShape(topLeft = 40.dp))
                .padding(top = 40.dp)
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
        ) {
            TimelineHeader()
            for (index in project.tasks.indices) {
                val task = project.tasks[index]
                val prev = project.tasks.getOrNull(index - 1)
                val next = project.tasks.getOrNull(index + 1)
                TimelineTask(
                    task = task,
                    topLineState = when {
                        prev == null -> LineState.Undefined
                        task.timeCode == prev.timeCode -> LineState.In
                        else -> LineState.Out
                    },
                    bottomLineState = when {
                        next == null -> LineState.Undefined
                        task.timeCode == next.timeCode -> LineState.In
                        else -> LineState.Out
                    },
                )
            }
        }
    }
}



private val stroke = Stroke(8f, cap = StrokeCap.Butt)
fun Modifier.circularProgress(
    progress: Float,
    primaryColor: Color,
) = this.drawBehind {
    drawCircularIndicator(
        startAngle = 270f,
        sweep = progress * 360f,
        color = primaryColor,
        stroke = stroke,
    )
}


@Composable fun ProjectProgressIndicator(
    modifier: Modifier = Modifier,
    progress: Float,
    status: Status
) {
    val text = "${(progress * 100).roundToInt()}%"
    val color = status.color
    Box(
        modifier
            .circularProgress(progress, color)
            .size(48.dp),
        gravity = ContentGravity.Center
    ) {
        Text(text, color = color, fontWeight = FontWeight.Bold)
    }
}

private fun DrawScope.drawCircularIndicator(
    startAngle: Float,
    sweep: Float,
    color: Color,
    stroke: Stroke
) {
    // To draw this circle we need a rect with edges that line up with the midpoint of the stroke.
    // To do this we need to remove half the stroke width from the total diameter for both sides.
    val diameterOffset = stroke.width / 2
    val arcDimen = size.width - 2 * diameterOffset
    drawArc(
        color = color,
        startAngle = startAngle,
        sweepAngle = sweep,
        useCenter = false,
        topLeft = Offset(diameterOffset, diameterOffset),
        size = Size(arcDimen, arcDimen),
        style = stroke
    )
    if (sweep < 360) {
        drawArc(
            color = color.copy(alpha = 0.3f),
            startAngle = (startAngle + sweep) % 360,
            sweepAngle = 360 - sweep,
            useCenter = false,
            topLeft = Offset(diameterOffset, diameterOffset),
            size = Size(arcDimen, arcDimen),
            style = stroke
        )
    }
}


private fun Modifier.layoutOffset(x: Dp = 0.dp, y: Dp = 0.dp) = this then layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    layout(placeable.width + x.toIntPx(), placeable.height + y.toIntPx()) {
        placeable.placeRelative(x.toIntPx(), y.toIntPx())
    }
}

@Composable fun AvatarList(
    size: Dp,
    users: List<User>,
    modifier: Modifier = Modifier,
    showAddButton: Boolean = false,
    onUserClick: (User) -> Unit,
    onAddClick: () -> Unit = {}
) {
    Row(modifier) {
        users.forEachIndexed { index, user ->
            Avatar(
                size,
                user,
                onClick = { onUserClick(user) },
                stack = index != 0
            )
        }
        if (showAddButton) {
            AvatarAddButton(
                size,
                onClick = onAddClick,
                stack = users.isNotEmpty()
            )
        }
    }
}

@Composable fun AvatarAddButton(
    size: Dp,
    onClick: () -> Unit,
    stack: Boolean = false,
    modifier: Modifier = Modifier
) {
    Avatar(size, onClick, stack, modifier) {
        Icon(Icons.Filled.Add)
    }
}

@Composable
fun Avatar(
    size: Dp,
    onClick: () -> Unit,
    stack: Boolean = false,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val overlap = -size / 5
    IconButton(
        onClick = onClick,
        modifier = modifier
            .layoutOffset(if (!stack) 0.dp else overlap)
            .drawShadow(5.dp, CircleShape, clip = false)
            .background(Color.White, CircleShape)
            .padding(2.dp)
            .size(size),
        icon = content
    )
}

@Composable fun Avatar(
    size: Dp,
    user: User,
    onClick: () -> Unit,
    stack: Boolean = false,
    modifier: Modifier = Modifier
) {
    Avatar(size, onClick, stack, modifier) {
        CoilImage(
            user.imageUrlForSize(with(DensityAmbient.current) { size.toIntPx() }),
            modifier = Modifier
                .clip(CircleShape)
                .fillMaxSize()
        )
    }
}













