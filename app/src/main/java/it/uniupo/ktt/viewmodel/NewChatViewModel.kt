package it.uniupo.ktt.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import it.uniupo.ktt.ui.firebase.ChatRepository
import it.uniupo.ktt.ui.model.Contact
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

import dagger.hilt.android.lifecycle.HiltViewModel
import it.uniupo.ktt.ui.firebase.ChatUtils
import it.uniupo.ktt.ui.model.EnrichedContact
import javax.inject.Inject

@HiltViewModel
class NewChatViewModel @Inject constructor() : ViewModel(){
    private val _enrichedContactList = MutableStateFlow<List<EnrichedContact>>(emptyList())
    val enrichedContactList: StateFlow<List<EnrichedContact>> = _enrichedContactList.asStateFlow() // Osservabile dai @Composalbe

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

        // OK
    fun loadContacts(uid: String) {
        _isLoading.value = true
        ChatRepository.getAllContactsByUid(
            uid,
            onSuccess = { contacts ->
                // Log.d("DEBUG", "Trovati ${contacts.size} contacts dato uid: $uid")
                // Log.d("DEBUG", "Lista Contacts: ${contacts.joinToString(separator = "\n")}")

                if(contacts.isEmpty()){
                    _enrichedContactList.value = emptyList()
                    _isLoading.value = false
                }
                else{
                    val enrichedList = mutableListOf<EnrichedContact>()
                    var completedCalls = 0

                    fun checkCompletion() {
                        completedCalls++
                        if (completedCalls == contacts.size) {
                            _enrichedContactList.value = enrichedList
                            _isLoading.value = false
                        }
                    }

                    contacts.forEach { contact ->
                        ChatUtils.getUserAndAndAvatarByUid(
                            uidUser = contact.uidContact,
                            onSuccess = { _, downloadUrl ->
                                enrichedList.add(
                                    EnrichedContact(
                                        contact = contact,
                                        avatarUrl = downloadUrl ?: ""
                                    )
                                )
                                checkCompletion()
                            },
                            onError = { e ->
                                Log.e("DEBUG", "Errore durante arricchimento contatto: ${e.message}")
                                enrichedList.add(EnrichedContact(contact, avatarUrl = ""))
                                checkCompletion()
                            }
                        )
                    }
                }
            },
            onError = { error ->
                _enrichedContactList.value = emptyList()  // Best practise -> se ho errore pulisco la lista
                _errorMessage.value = error.message
                _isLoading.value = false
            }
        )
    }
        // OK
    fun postContact(newContact: Contact) {
        ChatRepository.postNewContact(
            newContact = newContact,
            onSuccess = {
                //_contactList.value += newContact
            },
            onError = { error ->
                _errorMessage.value = error.message
            }
        )
    }

}