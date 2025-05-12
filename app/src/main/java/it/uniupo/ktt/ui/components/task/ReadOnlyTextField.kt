package it.uniupo.ktt.ui.components.task

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

@Composable
fun ReadOnlyTextField(
    label: String,
    value: String
) {
    Text(
        text = label,
        style = TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF403E3E),
        )
    )

    Spacer(modifier = Modifier.size(10.dp))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(4.dp, shape = MaterialTheme.shapes.extraLarge)
            .clip(CircleShape)
            .background(Color(0xFFF5DFFA))
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = value,
            style = TextStyle(
                fontSize = 16.sp,
                color = Color.Black
            )
        )
    }
}

@Preview
@Composable
fun ReadOnlyTextFieldPreview() {
    ReadOnlyTextField(
        label = "Label",
        value = "Value"
    )
}