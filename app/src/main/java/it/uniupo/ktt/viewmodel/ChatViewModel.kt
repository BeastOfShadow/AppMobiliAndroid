package it.uniupo.ktt.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ListenerRegistration
import it.uniupo.ktt.ui.firebase.BaseRepository
import it.uniupo.ktt.ui.firebase.ChatRepository
import it.uniupo.ktt.ui.model.Chat
import it.uniupo.ktt.ui.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

import dagger.hilt.android.lifecycle.HiltViewModel
import it.uniupo.ktt.ui.firebase.ChatUtils
import it.uniupo.ktt.ui.firebase.UserRepository
import it.uniupo.ktt.ui.model.EnrichedChat
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor() : ViewModel(){

}