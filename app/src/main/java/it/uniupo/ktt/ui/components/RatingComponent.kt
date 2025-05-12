package it.uniupo.ktt.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RatingComponent(
    initialRating: Int = 0,
    maxStars: Int = 5,
    comment: String = "",
    onRatingChanged: (Int) -> Unit = {},
    onCommentChanged: (String) -> Unit = {}
) {
    var rating by remember { mutableStateOf(initialRating) }
    var commentState by remember { mutableStateOf(comment) }

    Box(
        modifier = Modifier
            .padding(start = 10.dp)
            .fillMaxWidth()
            .shadow(
                4.dp,
                shape = MaterialTheme.shapes.extraLarge,
                clip = false
            )
            .background(Color(0xFFF5DFFA), shape = MaterialTheme.shapes.extraLarge)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Overall Rating Comment:",
                fontSize = 16.sp,
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = commentState,
                onValueChange = {
                    commentState = it
                    onCommentChanged(it)
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFFFFFFF),
                    unfocusedContainerColor = Color(0xFFFFFFFF),
                    cursorColor = Color.Black,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        4.dp, shape = MaterialTheme.shapes.extraLarge, clip = false
                    )
                    .background(Color(0xFFF5DFFA), shape = CircleShape),
                placeholder = { Text("Insert comment...") },
                shape = MaterialTheme.shapes.extraLarge,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge
            )


            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Overall Rating:",
                fontSize = 16.sp,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (index in 0 until maxStars) {
                    val filled = index < rating
                    Icon(
                        imageVector = if (filled) Icons.Filled.Star else Icons.Filled.StarBorder,
                        contentDescription = "Rating ${index + 1} of $maxStars",
                        tint = if (filled) Color(0xFF000000) else Color.Gray,
                        modifier = Modifier
                            .size(35.dp)
                            .padding(horizontal = 2.dp)
                            .clickable {
                                val newRating = if (index + 1 == rating) index else index + 1
                                rating = newRating
                                onRatingChanged(newRating)
                            }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun RatingComponentPreview() {
    var previewRating by remember { mutableStateOf(3) }
    var previewComment by remember { mutableStateOf("") }

    RatingComponent(
        initialRating = previewRating,
        comment = previewComment,
        onRatingChanged = { previewRating = it },
        onCommentChanged = { previewComment = it }
    )
}