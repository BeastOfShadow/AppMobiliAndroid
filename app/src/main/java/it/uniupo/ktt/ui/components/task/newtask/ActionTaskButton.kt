package it.uniupo.ktt.ui.components.task.newtask

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import kotlinx.coroutines.CoroutineScope
import androidx.compose.ui.draw.shadow
import it.uniupo.ktt.ui.theme.tertiary

@Composable
fun ActionTaskButton(
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    cancelText: String = "Cancel",
    confirmText: String = "Create",
    buttonWidth: Int = 140,
    buttonHeight: Int = 45,
    textSize: TextUnit = 16.sp,
    buttonColor: Color = tertiary
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(buttonWidth.dp)
                .height(buttonHeight.dp)
                .shadow(
                    4.dp,
                    shape = MaterialTheme.shapes.large,
                    clip = false
                )
                .background(
                    color = buttonColor,
                    shape = MaterialTheme.shapes.large
                )
                .clickable { onCancel() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = cancelText,
                color = Color.White,
                fontSize = textSize,
                fontWeight = FontWeight.Bold
            )
        }
        Box(
            modifier = Modifier
                .width(buttonWidth.dp)
                .height(buttonHeight.dp)
                .shadow(
                    4.dp,
                    shape = MaterialTheme.shapes.large,
                    clip = false
                )
                .background(
                    color = buttonColor,
                    shape = MaterialTheme.shapes.large
                )
                .clickable { onConfirm() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = confirmText,
                color = Color.White,
                fontSize = textSize,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
