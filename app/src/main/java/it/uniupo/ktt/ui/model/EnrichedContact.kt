package it.uniupo.ktt.ui.model

data class EnrichedContact(
    val contact: Contact,

    // Campo AvatarUrl Extra per non appesantire FireBase DB
    val avatarUrl: String= ""
)