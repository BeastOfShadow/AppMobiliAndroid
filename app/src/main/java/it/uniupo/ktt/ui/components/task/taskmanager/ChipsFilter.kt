package it.uniupo.ktt.ui.components.task.taskmanager

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.uniupo.ktt.ui.theme.subtitleColor
import it.uniupo.ktt.ui.theme.tertiary

@Composable
fun ChipsFilter(
    filters: List<String>,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        filters.forEach { filter ->
            Box(
                modifier = Modifier
                    .then(
                        if (selectedFilter != filter) {
                            Modifier.border(
                                width = 1.dp,
                                color = Color(0xFF403E3E),
                                shape = MaterialTheme.shapes.extraLarge
                            )
                        } else {
                            Modifier
                        }
                    )
                    .shadow(
                        4.dp,
                        shape = MaterialTheme.shapes.extraLarge,
                        clip = false
                    )
                    .clickable {
                        onFilterSelected(filter)
                    },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            if (selectedFilter == filter) tertiary else Color.White,
                            shape = MaterialTheme.shapes.extraLarge
                        )
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = filter,
                        fontWeight = if (selectedFilter == filter) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 14.sp,
                        color = if (selectedFilter == filter) Color.White else subtitleColor
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ChipsFilterPreview() {
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Ready", "Ongoing", "Completed")

    ChipsFilter(
        filters = filters,
        selectedFilter = selectedFilter,
        onFilterSelected = { selectedFilter = it }
    )
}
