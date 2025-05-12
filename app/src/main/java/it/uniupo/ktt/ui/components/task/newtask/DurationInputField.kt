package it.uniupo.ktt.ui.components.task.newtask

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DurationInputField(
    duration: String,
    onDurationChange: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Duration: ",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF403E3E),
                )
            )
            TextField(
                value = duration,
                onValueChange = { newText ->
                    if (newText.length <= 5) {
                        onDurationChange(newText)
                    }
                },
                label = { Text("HH:MM") },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF5DFFA),
                    unfocusedContainerColor = Color(0xFFF5DFFA),
                    cursorColor = Color.Black,
                    disabledLabelColor = Color.Red,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier.width(90.dp)
            )
        }
    }
}
