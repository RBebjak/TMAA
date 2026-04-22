package com.example.plantwatering.core.firebase

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseProvider {
    private const val FIRESTORE_DATABASE_ID = "plant"

    fun firestore(context: Context): FirebaseFirestore {
        val app = FirebaseApp.getApps(context).firstOrNull()
            ?: FirebaseApp.initializeApp(context)
            ?: throw FirebaseConfigurationException()

        return FirebaseFirestore.getInstance(app, FIRESTORE_DATABASE_ID)
    }
}
