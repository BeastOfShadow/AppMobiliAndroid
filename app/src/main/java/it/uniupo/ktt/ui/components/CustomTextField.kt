package it.uniupo.ktt.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomTextField(
    label: String,
    textfieldValue: String,
    onValueChange: (String) -> Unit
) {
    Text(
        text = label,
        style = TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight(500),
            color = Color(0xFF403E3E),
        ),
        modifier = Modifier.padding(start = 20.dp)
    )

    Spacer(modifier = Modifier.size(10.dp))

    TextField(
        value = textfieldValue,
        onValueChange = onValueChange,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFF5DFFA),
            unfocusedContainerColor = Color(0xFFF5DFFA),
            cursorColor = Color.Black,
            disabledLabelColor = Color.Red,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                4.dp, shape = MaterialTheme.shapes.extraLarge, clip = false
            )
            .clip(CircleShape)
            .background(Color(0xFFF5DFFA))
            .padding(start = 10.dp, end = 5.dp),
        trailingIcon = @Composable {
            if (textfieldValue.isNotEmpty()) {
                IconButton(onClick = { onValueChange("") }) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = null
                    )
                }
            }
        },
        shape = MaterialTheme.shapes.extraLarge,
        singleLine = true
    )
}

@Preview
@Composable
fun CustomTextFieldPreview() {
    CustomTextField(
        label = "Label",
        textfieldValue = "Value",
        onValueChange = { var textfieldValue = it }
    )
}