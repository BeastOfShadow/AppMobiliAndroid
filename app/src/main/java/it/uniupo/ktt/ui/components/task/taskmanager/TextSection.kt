package it.uniupo.ktt.ui.components.task.taskmanager

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

@Composable
fun TextSection(
    sectionName: String
) {
    Text(
        text = sectionName,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally)
    )
}

@Preview(showBackground = true)
@Composable
fun TextSection() {
    TextSection(
        "Ready"
    )
}
