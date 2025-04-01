package it.uniupo.ktt.ui.components.chats

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.uniupo.ktt.R

@Composable
fun AddContactButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .background(color = Color(0xFFF5DFFA), shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Cerchio con icona
        Box(
            modifier = Modifier
                .shadow(elevation = 8.dp, shape = CircleShape, clip = false)
                .size(38.dp)
                .clip(CircleShape)
                .background(Color(0xFFA47BD4)), // viola
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.chat_add_contact),
                contentDescription = "Add contact",
                tint = Color.White,
                modifier = Modifier
                    .size(44.dp)
                    .offset(x = 4.dp, y = 1.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = "Add contact",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AddContactButtonPreview() {
    AddContactButton()
}
