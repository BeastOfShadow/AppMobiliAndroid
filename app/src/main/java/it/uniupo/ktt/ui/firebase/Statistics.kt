package it.uniupo.ktt.ui.firebase

object StatisticsRepository {

    fun getTaskCountsByStatus(
        uid: String,
        onResult: (ready: Int, ongoing: Int, completed: Int) -> Unit,
        onError: (Exception) -> Unit = {}
    ) {
        val db = BaseRepository.db
        var ready = 0
        var ongoing = 0
        var completed = 0

        // Counter per sapere quando ho finito tutte le chiamate
        var completedCalls = 0

        // mi permette di eseguire le 3 query in parallelo (senza ".await"), sfruttando un contatore
        // per regolare l'attesa del return (che ritorna una tupla di 3 valori)
        fun checkIfAllDone() {
            completedCalls++
            if (completedCalls == 3) {
                onResult(ready, ongoing, completed)
            }
        }

        // prima query per i readyTasks
        db.collection("tasks")
            .whereEqualTo("caregiver", uid)
            .whereEqualTo("status", "ready")
            .get()
            .addOnSuccessListener { snapshot -> // Snap VUOTO se non ho DOC, e la sua ".size()" è "0"
                ready = snapshot.size()
                checkIfAllDone()
            }
            .addOnFailureListener { onError(it) }

        // seconda query per gli ongoingTasks
        db.collection("tasks")
            .whereEqualTo("caregiver", uid)
            .whereEqualTo("status", "ongoing")
            .get()
            .addOnSuccessListener { snapshot ->
                ongoing = snapshot.size()
                checkIfAllDone()
            }
            .addOnFailureListener { onError(it) }

        // terza query per i completedTasks
        db.collection("tasks")
            .whereEqualTo("caregiver", uid)
            .whereEqualTo("status", "completed")
            .get()
            .addOnSuccessListener { snapshot ->
                completed = snapshot.size()
                checkIfAllDone()
            }
            .addOnFailureListener { onError(it) }
    }





}