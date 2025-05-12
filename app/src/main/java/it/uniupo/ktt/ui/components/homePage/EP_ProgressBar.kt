package it.uniupo.ktt.ui.components.homePage

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.compose.rememberNavController
import it.uniupo.ktt.R
import it.uniupo.ktt.enumUtils.RankLevel
import it.uniupo.ktt.ui.firebase.BaseRepository
import it.uniupo.ktt.ui.firebase.UserRepository
import it.uniupo.ktt.ui.theme.titleColor
import it.uniupo.ktt.viewmodel.UserViewModel

@Composable
fun EP_ProgressBar(
    modifier: Modifier = Modifier,
) {

    val backgroundBarColor = Color(0xFFF5DFFA)
    val fillBarColor = Color(0xFF9C46FF)

    val currentUid = BaseRepository.currentUid()
    var userPoints by remember { mutableStateOf<Int?>(null) }

    // Ricalcolo ogni volta la BAR compreso quando faccio PopBackStack
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(Unit) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && currentUid != null) {
                UserRepository.getUserPointsByUid(
                    uid = currentUid,
                    onSuccess = { points ->
                        Log.d("DEBUG-PROGRESS-BAR-EP", "Punti Ricevuti (resume): $points")
                        userPoints = points
                    },
                    onError = { e -> Log.e("DEBUG", "Errore caricamento punti", e) }
                )
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(currentUid) {
        if (currentUid != null) {
            UserRepository.getUserPointsByUid(
                uid = currentUid,
                onSuccess = { points ->
                    Log.d("DEBUG-PROGRESS-BAR-EP", "Punti Ricevuti: $points")
                    userPoints = points
                            },
                onError = { e -> Log.e("DEBUG", "Errore caricamento punti", e) }
            )
        }
    }

    if (userPoints == null) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
    else{
        val userPt = userPoints // SmartCast (mutable to immutable)

        if(userPt!= null){

            /*
            *                       --- LOGICA PROGRESS BAR ---
            *
            *   2) Logica Ranking: ogni utente ha un totale di PT (punti)
            *                      ogni Livello ha tot PT Totali diversi (Salvo in Locale)
            *                      -> ricavo i PT dell'utente, scorro tutti i livelli contandoli
            *                         e sottraggo i PT di ogni livello ai PT Utente finchè non resto
            *                         con gli ultimi PT Utente non i grado di completare il Livello corrente.
            *
            *                      -> Con i PT Utente rimanenti posso lavorare sul "ratio"
            *                      -> Con il Counter dei Livelli posso scrivere il "rank"
            *                      -> in Base al livello del Counter scelgo lo Sticker meritato
            *
            *           SOLUZIONE: - ENUM
            */

            val level = RankLevel.findLevelByPoint(userPt)

            val rank= level.label
            val rankSticker= level.drawableId
            val ratio = (userPt - level.minPt).toFloat() / (level.maxPt - level.minPt)


                                        //ANIMAZIONE

            //stato interno per animare 1 sola volta la barra
            var animatedStart by remember { mutableStateOf(false) }

            //scelta animazione
            val animatedRatio by animateFloatAsState(
                targetValue = if (animatedStart) ratio.coerceIn(0f, 1f) else 0f,
                animationSpec = tween(durationMillis = 2000),
                label = "animated ratio"
            )

            // Attiva l’animazione una volta al primo recomposition
            LaunchedEffect(Unit) {
                animatedStart = true
            }

            Box{

                Column(
                    modifier = Modifier
                        .offset(x = 0.dp, y = -(15).dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Spacer(
                        modifier = Modifier.height(40.dp)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterHorizontally)

                    ){
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Box( // Barra 1
                                modifier = Modifier
                                    .width(260.dp) // tutta piena
                                    .height(15.dp)
                                    .shadow(
                                        elevation = 4.dp, // altezza dell’ombra
                                        shape = RoundedCornerShape(50),
                                        clip = false
                                    )
                                    .clip(RoundedCornerShape(50))
                                    .background(backgroundBarColor)

                            ) {
                                Box( // Barra 2
                                    modifier = Modifier
                                        .fillMaxWidth(animatedRatio) // piena fino al valore "animatedRatio" (derivato da "ratio")
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(50))
                                        .background(fillBarColor)
                                )
                            }
                        }

                    }

                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )

                    Text(
                        text = rank,
                        style = MaterialTheme.typography.bodyLarge, //Poppins

                        fontSize = 16.sp,
                        fontWeight = FontWeight(800),

                        color = titleColor
                    )





                }

                // Ranck Sticker
                Image(
                    painter = painterResource(id = rankSticker),
                    contentDescription = "Rank sticker",
                    modifier = Modifier
                        .size(62.dp)
                        //.offset(x = -(128).dp, y = -(83).dp)
                        .offset(x = (32).dp, y = -(0).dp)
                )
            }

        }
    }

}



@Preview(showBackground = true)
@Composable
fun EP_ProgressBarPreview() {
    val navController = rememberNavController()

    EP_ProgressBar(
        modifier = Modifier.scale(1.2f)
    )
}