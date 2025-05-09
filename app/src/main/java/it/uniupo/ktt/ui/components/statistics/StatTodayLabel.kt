package it.uniupo.ktt.ui.components.statistics

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.uniupo.ktt.R

@Composable
fun StatTodayLabel(
    value: Int,
    label: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Text(
            text = value.toString(),
            fontFamily = FontFamily(Font(R.font.poppins_regular)),
            fontWeight = FontWeight(500),
            fontSize = 25.sp,
            color = Color(0xFF272727)
        )
        Text(
            text = label,
            fontFamily = FontFamily(Font(R.font.poppins_regular)),
            fontWeight = FontWeight(500),
            fontSize = 15.sp,
            color = Color(0xFF827676),
            modifier = Modifier
                .offset(y = -(5.dp))
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StatTodayLabelPreview() {
    StatTodayLabel(
        value = 10,
        label = "(todo)"
    )
}