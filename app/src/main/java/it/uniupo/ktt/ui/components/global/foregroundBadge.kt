package it.uniupo.ktt.ui.components.global

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import it.uniupo.ktt.ui.firebase.BaseRepository
import it.uniupo.ktt.ui.model.EnrichedChat
import kotlinx.coroutines.delay


@Composable
fun foregroundBadge(
    highlightedChat: EnrichedChat?,
    onDismiss: () -> Unit,
    onClick: (chatId: String, otherUid: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val showBadge = remember { mutableStateOf(false) }

    LaunchedEffect(highlightedChat) {
        if (highlightedChat != null) {
            showBadge.value = true
            delay(5000)
            showBadge.value = false
        }
    }

    AnimatedVisibility(
        visible = showBadge.value,
        enter = slideInVertically { -it } + fadeIn(),
        exit = slideOutVertically { -it } + fadeOut(),
        modifier = modifier
            .zIndex(11f)
    ) {
        LaunchedEffect(showBadge.value) {
            if (!showBadge.value && highlightedChat != null) {
                delay(300)
                onDismiss()
            }
        }

        val currentUid = BaseRepository.currentUid()
        val otherUid = highlightedChat?.chat?.members?.firstOrNull { it != currentUid } ?: ""
        val contactAvatarUrl = highlightedChat?.avatarUrl ?: ""
        val contactName = "${highlightedChat?.name ?: ""} ${highlightedChat?.surname ?: ""}"
        val lastMsg = highlightedChat?.chat?.lastMsg?.take(50)?.plus(
            if (highlightedChat.chat.lastMsg.length > 50) "..." else ""
        ) ?: ""

        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0E6F6)),
            modifier = Modifier
                .clickable {
                    onClick(highlightedChat!!.chat.chatId, otherUid)
                    showBadge.value = false
                }
                .widthIn(max = 300.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = contactAvatarUrl,
                        contentDescription = "Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = contactName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF3C3C3C),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = lastMsg,
                        fontSize = 14.sp,
                        color = Color(0xFF5C5C5C),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}