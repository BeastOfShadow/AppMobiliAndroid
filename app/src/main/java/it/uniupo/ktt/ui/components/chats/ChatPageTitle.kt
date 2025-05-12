package it.uniupo.ktt.ui.components.chats

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import it.uniupo.ktt.R
import it.uniupo.ktt.ui.theme.primary
import it.uniupo.ktt.ui.theme.secondary
import it.uniupo.ktt.ui.theme.titleColor

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ChatPageTitle(
    navController: NavController,
    nome: String,
    imgId: Int,   //R.drawable?
    modifier: Modifier
)
{
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
            .padding(10.dp)
            .background(Color.White)
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .shadow(4.dp, shape = CircleShape, clip = false)
        ) {
            FilledIconButton(
                onClick = {
                    navController.popBackStack("chat", inclusive = false) //return
                },
                modifier = Modifier.size(34.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = primary,
                    contentColor = titleColor
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBackIosNew,
                    contentDescription = "Back",
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
                .padding(end = 34.dp)
                .shadow(4.dp, shape = MaterialTheme.shapes.extraLarge, clip = false)
                .background(
                    color = secondary,
                    shape = MaterialTheme.shapes.extraExtraLarge
                )
                // possibile cambiare a 15.dp
                .padding(vertical = 8.dp),
        ) {
            Row (
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .offset(x= 10.dp, y= 0.dp)

            ) {
                IconButton(onClick = { /* opzionale: zoom foto */ },
                    modifier = Modifier
                        .shadow(4.dp, shape = CircleShape, clip = false)
                        .size(48.dp)
                        .background(Color.White, shape = CircleShape)
                ) {
                    Image(
                        //painter = painterResource(imgId),
                        painter = painterResource(imgId),
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .offset(x= 0.dp, y= 0.dp)
                            .scale(0.8f)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = nome,
                    style = MaterialTheme.typography.bodyLarge, //Poppins

                    fontSize = 23.sp,
                    fontWeight = FontWeight(400),

                    color = titleColor,
                )


            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatPageTitlePreview() {
    val navController = rememberNavController()

    ChatPageTitle(
        navController = navController,
        nome = "LUIGI CAPUANI",
        imgId = R.drawable.profile_female_default,
        modifier = Modifier
            .scale(1.3f),
    )
}
