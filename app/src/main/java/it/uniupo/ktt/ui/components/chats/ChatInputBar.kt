package it.uniupo.ktt.ui.components.chats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

@Composable
fun ChatInputBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSendClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFFF2F2F2))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

//        val imeBottom = WindowInsets.ime.getBottom(LocalDensity.current).toDp()
//        val customBottomPadding: Dp = if (imeBottom > 0.dp) imeBottom - 4.dp else 0.dp

        TextField(
            value = text,
            onValueChange = onTextChange,
            placeholder = { Text("Scrivi un messaggio...") },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            )
            ,
            modifier = Modifier
                .weight(1f),
            singleLine = true,
            shape = RoundedCornerShape(20.dp),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = {
                if (text.isNotBlank()) onSendClick()
            })
        )

        IconButton(
            onClick = onSendClick,
            enabled = text.isNotBlank()
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Invia",
                tint = if (text.isNotBlank()) Color(0xFF9C46FF) else Color.Gray
            )
        }
    }
}



