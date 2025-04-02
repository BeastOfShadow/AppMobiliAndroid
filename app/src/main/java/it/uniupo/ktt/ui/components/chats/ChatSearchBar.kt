package it.uniupo.ktt.ui.components.chats

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.uniupo.ktt.R

@Composable
fun ChatSearchBar(
    query: String,
    onQueryChanged: (String) -> Unit,
    modifier: Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(color = Color(0xFFF5DFFA), shape = RoundedCornerShape(12.dp)) // lilla
            .padding(16.dp)
    ) {

        TextField(
            value = query,
            onValueChange = onQueryChanged,
            placeholder = {
                Text(
                    "Search ...",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    //modifier = Modifier.offset(y = (-6).dp)
                )
            },
            trailingIcon = {
                AnimatedContent(
                    targetState = query.isNotEmpty(),
                    transitionSpec = {
                        fadeIn(animationSpec = tween(200)) togetherWith fadeOut(animationSpec = tween(200))
                    },
                    label = "SearchClearTransition"
                ) { hasText ->
                    if (hasText) {
                        IconButton(onClick = { onQueryChanged("") }) {
                            Icon(
                                painter = painterResource(id = R.drawable.chat_delete),
                                contentDescription = "clear text",
                                tint = Color(0xFF746767),
                                modifier = Modifier
                                    .size(30.dp)
                                    .padding(end = 8.dp)
                            )
                        }
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.chat_search),
                            contentDescription = "search contact",
                            tint = Color(0xFF746767),
                            modifier = Modifier
                                .size(35.dp)
                                .padding(end = 8.dp)
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(45),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFFFFFFF),
                unfocusedContainerColor = Color(0xFFFFFFFF),
                disabledContainerColor = Color(0xFFFFFFFF),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChatSearchBarPreview() {
    ChatSearchBar(
        query = "",
        onQueryChanged = {},
        modifier = Modifier
    )
}
