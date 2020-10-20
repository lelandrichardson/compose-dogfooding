package com.example.pmapp

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Layout
import androidx.compose.ui.Modifier
import androidx.compose.ui.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times

val linePosition = 80.dp

val DrawScope.topRight: Offset get() = size.topRight(Offset.Zero)
val DrawScope.bottomRight: Offset get() = size.bottomRight(Offset.Zero)

fun Modifier.drawLine() = drawBehind {
    drawLine(Color.Black, topRight, bottomRight, 4f)
}

@Composable fun TimelineRow(
    leftContent: @Composable () -> Unit,
    rightContent: @Composable () -> Unit
) {
    Layout({
        Row(
            Modifier
                .padding(end=16.dp)
                .drawLine(),
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
        leftContent = {
            Text("Data")
        }
    ) {
        val x = 4 * 2.dp
        val y = 2.dp * 4
        Text("Tasks")
        Text("Show in days")
    }
}


@Composable
fun TimelineTask(task: Task) {
    TimelineRow(
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