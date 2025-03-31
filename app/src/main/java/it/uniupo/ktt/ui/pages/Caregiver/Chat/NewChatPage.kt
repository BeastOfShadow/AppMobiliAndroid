package it.uniupo.ktt.ui.pages.Caregiver.Chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import it.uniupo.ktt.ui.components.PageTitle
import it.uniupo.ktt.ui.components.chats.NewChatButton

@Composable
fun NewChatPage(navController: NavController) {
    if (!LocalInspectionMode.current && FirebaseAuth.getInstance().currentUser == null) {
        navController.navigate("landing") {
            popUpTo("new chat") { inclusive = true }
            launchSingleTop = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp)
    ) {
        Column() {
            PageTitle(
                navController = navController,
                title = "New Chat"
            )

            Spacer(modifier = Modifier.height(20.dp))

            //Page
            Box(
                modifier = Modifier
                    //.shadow(8.dp, RoundedCornerShape(20.dp), clip = false)
                    .padding(4.dp)
                    .background(Color(0xFFF5DFFA), shape = RoundedCornerShape(10))
                    .fillMaxSize()
            ){

//                Text(
//                    text = "prova di testo",
//                    style = MaterialTheme.typography.bodyLarge, //Poppins
//
//
//                    //letterSpacing = 1.sp,
//                    fontSize = 22.sp,
//                    fontWeight = FontWeight(400),
//                    lineHeight = 34.sp,
//
//
//
//                    color = Color(0xFF423C3C),
//                    textAlign = TextAlign.Center,
//                    modifier = Modifier
//                        //.align(Alignment.Center)
//                        .offset(x= 0.dp, y= 260.dp)
//                        .padding(horizontal = 55.dp)
//                )

                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(20.dp)
                ){
                    NewChatButton(
                        navController = navController,

                        //Modifier aggiuntivi utili passabili
                        modifier = Modifier
                            .scale(1.2f)
                    )
                }



            }
        }
    }
}

@Preview
@Composable
fun NewChatPagePreview() {
    NewChatPage(navController = NavController(context = LocalContext.current))
}
