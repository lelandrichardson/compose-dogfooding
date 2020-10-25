package com.example.pmapp

import android.graphics.DashPathEffect
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times

val linePosition = 80.dp
val timelineDotSize = 8.dp
val dashPathEffect = DashPathEffect(floatArrayOf(20f, 10f), 0f)

val DrawScope.topRight: Offset get() = Offset(size.width, 0f)
val DrawScope.bottomRight: Offset get() = Offset(size.width, size.height)

enum class LineState(val color: Color, val stroke: Stroke) {
    Undefined(Color.Gray, Stroke(width = 1f, pathEffect = dashPathEffect)), // dotted line
    In(Color.White, Stroke(width = 8f)), // solid white
    Out(Color.Gray, Stroke(width = 1f)) // solid gray
}

fun Modifier.drawLine(
        status: Status?,
        top: LineState,
        bottom: LineState
) = drawBehind {
    val centerPosition = Offset(x=size.width, y=size.height / 2f)
    drawLine(
        color = top.color,
        start = topRight,
        end = centerPosition,
        strokeWidth = top.stroke.width,
        pathEffect = top.stroke.pathEffect
    )
    drawLine(
        color = bottom.color,
        start = centerPosition,
        end = bottomRight,
        strokeWidth = bottom.stroke.width,
        pathEffect = bottom.stroke.pathEffect
    )
    if (status != null) {
        drawCircle(
            color = Color.White,
            radius = timelineDotSize.toPx(),
            center = centerPosition
        )
        drawCircle(
            color = status.color,
            radius = (timelineDotSize - 2.dp).toPx(),
            center = centerPosition
        )
    }
}

@Composable fun TimelineRow(
    status: Status?,
    topLineState: LineState,
    bottomLineState: LineState,
    leftContent: @Composable () -> Unit,
    rightContent: @Composable () -> Unit
) {
    Layout({
        Row(
            Modifier
                .padding(end=16.dp)
                .drawLine(status, topLineState, bottomLineState),
            verticalAlignment = Alignment.CenterVertically
        ) {
            leftContent()
        }
        Row {
            rightContent()
        }
    }) { (left, right), constraints ->
        val leftWidth = linePosition.toIntPx()
        val placedRight = right.measure(
            androidx.compose.ui.unit.Constraints(
                minWidth = (constraints.minWidth - leftWidth).coerceAtLeast(0),
                maxWidth = (constraints.maxWidth - leftWidth).coerceAtLeast(0),
                minHeight = constraints.minHeight,
                maxHeight = constraints.maxHeight
            )
        )
        val placedLeft = left.measure(androidx.compose.ui.unit.Constraints.fixed(
            leftWidth,
            placedRight.height
        ))
        layout(placedLeft.width + placedRight.width, placedRight.height) {
            placedLeft.placeRelative(0, 0)
            placedRight.placeRelative(leftWidth, 0)
        }
    }
}

@Composable
fun TimelineHeader() {
    TimelineRow(
        status = null,
        topLineState = LineState.Undefined,
        bottomLineState = LineState.Undefined,
        leftContent = {
            Text("Data")
        }
    ) {
        Text("Tasks")
        Text("Show in days")
    }
}


@Composable
fun TimelineTask(
    task: Task,
    topLineState: LineState,
    bottomLineState: LineState,
) {
    TimelineRow(
        status = task.status,
        topLineState = topLineState,
        bottomLineState = bottomLineState,
        leftContent = {
            Text(task.timeCode)
        }
    ) {
        Column(
            Modifier
                .padding(vertical = 8.dp)
                .background(Color.White, RoundedCornerShape(6.dp))
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    task.status.humanReadable,
                    color = task.status.color
                )
                Text(task.tag)
            }
            Row {
                Text(task.title)
            }
            Row {
                IconButton(onClick = {}) {
                    Row {
                        Icon(Icons.Default.ChatBubbleOutline)
                        Text("${task.commentCount}")
                    }
                }
                IconButton(onClick = {}) {
                    Row {
                        Icon(Icons.Default.Attachment)
                        Text("${task.attachmentCount}")
                    }
                }
                Spacer(Modifier.weight(1f))
                Text("N\u00B0 ${task.id}")
                AvatarList(
                    32.dp,
                    task.assignees,
                    onUserClick = { }
                )
            }
        }
    }
}