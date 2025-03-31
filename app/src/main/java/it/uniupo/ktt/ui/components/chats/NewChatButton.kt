package it.uniupo.ktt.ui.components.chats

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.uniupo.ktt.R


@Composable
fun NewChatButton(
    navController: NavController,
    modifier : Modifier = Modifier,
    ) {
    IconButton(
        onClick = {
            //ROUTING to ContactPage
            navController.navigate("new chat")
        },
        modifier = modifier
            .shadow(8.dp, RoundedCornerShape(20.dp), clip = false)
            .background(Color(0xFF9C46FF), shape = RoundedCornerShape(20.dp))
            .size(60.dp)
    ) {
        Image(
            painter = painterResource(id= R.drawable.menu_chat_new),
            contentDescription = "New Chat",
            modifier = Modifier
                .size(58.dp)
                .offset(x= 1.dp, y= 3.dp)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun NewChatButtonPreview() {
    //NewChatButton()
}
