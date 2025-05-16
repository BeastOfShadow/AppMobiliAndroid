package it.uniupo.ktt.ui.components.homePage

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import it.uniupo.ktt.viewmodel.UserViewModel


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import it.uniupo.ktt.R
import kotlin.math.absoluteValue
import androidx.compose.ui.util.lerp


@Composable
fun ModalShowAvatars(
    onDismiss: () -> Unit
) {
    // collegamento al viewModel(istanziato nella HomeScreen)
    val userViewModelRef = hiltViewModel<UserViewModel>()
    // osservabili
    val avatarUrlListRef by userViewModelRef.avatarUrlsList.collectAsState()
    val isLoadingListRef by userViewModelRef.isLoadingAvatar.collectAsState()

    if (avatarUrlListRef.isEmpty() && !isLoadingListRef) {
        LaunchedEffect(Unit) {
            userViewModelRef.loadAllAvatars()
        }
    }


    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            // wait to build PageUI (DB data not delivered yet)
            isLoadingListRef -> {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
            // lista Avatar recieved vuota (0 Avatar)
            avatarUrlListRef.isEmpty() -> {
                Text(
                    text = "No avatars found.",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Black
                )
            }
            // lista Avatar recieved non vuota (1+ avatar)
            else -> {

                val pagerState = rememberPagerState(
                    initialPage = Int.MAX_VALUE / 2,
                    initialPageOffsetFraction = 0f,
                    pageCount = { Int.MAX_VALUE }
                )

                // Calcolo l'avatar selezionato
                val selectedAvatarUrl =
                    avatarUrlListRef[pagerState.currentPage % avatarUrlListRef.size]

                Column(
                    modifier = Modifier
                        .background(Color(0xFFC5B5D8), shape = RoundedCornerShape(16.dp)) // lilla scuro
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Choose new avatar",
                        fontFamily = FontFamily(Font(R.font.poppins_medium)),
                        fontWeight = FontWeight(500),
                        fontSize = 22.sp,

                        color = Color.White,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    HorizontalPager(
                        state = pagerState,
                        contentPadding = PaddingValues(horizontal = 64.dp),
                        modifier = Modifier
                            .height(200.dp)
                            .fillMaxWidth()
                    ) { index ->
                        val realIndex = index % avatarUrlListRef.size
                        val avatarUrl = avatarUrlListRef[realIndex]

                        // Calcolo OFFSET per l'effetto "avatar laterali + piccoli"
                        val pageOffset = (
                                (pagerState.currentPage - index) + pagerState.currentPageOffsetFraction
                                ).absoluteValue
                        // PARAM de-Scale degli avatar laterali
                        val scale = lerp(
                            start = 0.80f, // modifico per diminuire la loro dim
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )

                        Box(
                            modifier = Modifier
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                }
                                .size(135.dp)
                                .shadow(elevation = 8.dp, shape = CircleShape, clip = false)
                                .background(Color(0xFFF5DFFA), CircleShape),
                            contentAlignment = Alignment.Center
                        ){
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .background(Color.White, CircleShape)
                                    .border(
                                        width = if (pagerState.currentPage == index) 4.dp else 0.dp,
                                        color = if (pagerState.currentPage == index) Color(0xFF9C46FF) else Color.Transparent,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center

                            ){
                                AsyncImage(
                                    model = avatarUrl,
                                    contentDescription = "Avatar",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(105.dp)
                                        .clip(CircleShape)

                                )
                            }
                        }


                    }




                    Spacer(modifier = Modifier.size(20.dp))

                    // BUTTONS
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        //CANCEL BUTTON
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBBA5E1)) // viola chiaro
                        ) {
                            Text(
                                "Cancel",
                                style = TextStyle(
                                    fontFamily = FontFamily(Font(R.font.poppins_semibold)),
                                    fontWeight = FontWeight(400),
                                    fontSize = 18.sp
                                )
                            )
                        }


                        //SELECT BUTTON
                        Button(
                            onClick = {
                                userViewModelRef.updateAvatar(selectedAvatarUrl)
                                onDismiss()
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C46FF)) // viola più scuro
                        ) {
                            Text(
                                "Select",
                                style = TextStyle(
                                    fontFamily = FontFamily(Font(R.font.poppins_semibold)),
                                    fontWeight = FontWeight(400),
                                    fontSize = 18.sp
                                )
                            )
                        }
                    }
                }
            }
        }

    }
}