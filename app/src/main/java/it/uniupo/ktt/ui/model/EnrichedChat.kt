package it.uniupo.ktt.ui.model

import com.google.firebase.Timestamp

data class EnrichedChat(
    val chat: Chat,

    // 3 campi extra che aggiungo per comodità per ChatPage (non presenti nella Post Chat)
    val name: String= "",
    val surname: String= "",
    val avatarUrl: String= ""

)
