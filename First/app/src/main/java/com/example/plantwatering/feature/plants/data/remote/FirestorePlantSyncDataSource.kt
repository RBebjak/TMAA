package com.example.plantwatering.feature.plants.data.remote

import android.content.Context
import android.util.Log
import com.example.plantwatering.core.firebase.FirestoreSyncException
import com.example.plantwatering.core.firebase.FirebaseProvider
import com.example.plantwatering.feature.plants.domain.model.Plant
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Source
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout

class FirestorePlantSyncDataSource(
    context: Context
) {
    private val appContext = context.applicationContext
    private val firestore: FirebaseFirestore by lazy { FirebaseProvider.firestore(appContext) }
    private val plantsCollection: CollectionReference
        get() = firestore.collection(COLLECTION_NAME)

    suspend fun uploadPlants(plants: List<Plant>) {
        try {
            withTimeout(FIRESTORE_TIMEOUT_MS) {
                val snapshot = plantsCollection.get(Source.SERVER).await()
                val batch = firestore.batch()

                Log.d("Firestore", "Fetched ${snapshot.size()} remote plants")
                Log.d("Firestore", "Uploading ${plants.size} local plants")

                snapshot.documents.forEach {
                    batch.delete(it.reference)
                }

                plants.forEach { plant ->
                    batch.set(plantsCollection.document(plant.id), plant.toDocument())
                }

                batch.commit().await()

                Log.d("Firestore", "Upload complete")
            }
        } catch (throwable: Throwable) {
            throw throwable.toFirestoreSyncException()
        }
    }

    suspend fun downloadPlants(): List<Plant> {
        return withTimeout(FIRESTORE_TIMEOUT_MS) {
            plantsCollection
                .get(Source.SERVER)
                .await()
                .documents
                .mapNotNull { document ->
                    val name = document.getString(FIELD_NAME) ?: return@mapNotNull null
                    val wateringIntervalDays = document.getLong(FIELD_WATERING_INTERVAL_DAYS) ?: return@mapNotNull null
                    val lastWateredAtMillis = document.getLong(FIELD_LAST_WATERED_AT_MILLIS) ?: return@mapNotNull null

                    Plant(
                        id = document.id,
                        name = name,
                        wateringIntervalDays = wateringIntervalDays,
                        lastWateredAtMillis = lastWateredAtMillis
                    )
                }
        }
    }

    private fun Plant.toDocument(): Map<String, Any> {
        return mapOf(
            FIELD_NAME to name,
            FIELD_WATERING_INTERVAL_DAYS to wateringIntervalDays,
            FIELD_LAST_WATERED_AT_MILLIS to lastWateredAtMillis
        )
    }

    companion object {
        private const val FIRESTORE_TIMEOUT_MS = 10_000L
        private const val COLLECTION_NAME = "plants"
        private const val FIELD_NAME = "name"
        private const val FIELD_WATERING_INTERVAL_DAYS = "wateringIntervalDays"
        private const val FIELD_LAST_WATERED_AT_MILLIS = "lastWateredAtMillis"
    }
}

private fun Throwable.toFirestoreSyncException(): FirestoreSyncException {
    return when (this) {
        is FirestoreSyncException -> this
        is TimeoutCancellationException -> FirestoreSyncException.Unreachable(this)
        is FirebaseFirestoreException -> when (code) {
            FirebaseFirestoreException.Code.PERMISSION_DENIED ->
                FirestoreSyncException.PermissionDenied(this)
            FirebaseFirestoreException.Code.UNAVAILABLE,
            FirebaseFirestoreException.Code.DEADLINE_EXCEEDED ->
                FirestoreSyncException.Unreachable(this)
            else -> FirestoreSyncException.Unknown(this)
        }
        else -> FirestoreSyncException.Unknown(this)
    }
}
