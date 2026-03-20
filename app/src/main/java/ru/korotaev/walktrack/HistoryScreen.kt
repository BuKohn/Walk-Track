package ru.korotaev.walktrack

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HistoryScreen(
    onWalkItemClick: (WalkHistoryItem) -> Unit = {}
) {
    val historyItems = listOf(
        WalkHistoryItem("февр. 11, 2026", "16:23", 3434, 4000),
        WalkHistoryItem("февр. 11, 2026", "10:58", 1245, 4000),
        WalkHistoryItem("февр. 10, 2026", "22:01", 4234, 4000),
        WalkHistoryItem("февр. 9, 2026", "22:21", 1234, 4000),
        WalkHistoryItem("февр. 9, 2026", "12:01", 4241, 4000),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "История",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(historyItems) { item ->
                HistoryItemCard(
                    item = item,
                    onClick = { onWalkItemClick(item) }
                )
            }
        }
    }
}

@Composable
fun HistoryItemCard(
    item: WalkHistoryItem,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = item.date,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = item.time,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Text(
                    text = "${item.steps} / ${item.goal} шагов",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0E0E0)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Открыть",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Gray
                )
            }
        }
    }
}

data class WalkHistoryItem(
    val date: String,
    val time: String,
    val steps: Int,
    val goal: Int
)