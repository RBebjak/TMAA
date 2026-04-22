package com.example.plantwatering.core.firebase

sealed class FirestoreSyncException(
    message: String,
    cause: Throwable? = null
) : IllegalStateException(message, cause) {

    class Unreachable(cause: Throwable? = null) : FirestoreSyncException(
        message = "Firestore is unreachable.",
        cause = cause
    )

    class PermissionDenied(cause: Throwable? = null) : FirestoreSyncException(
        message = "Firestore rejected the request.",
        cause = cause
    )

    class Unknown(cause: Throwable? = null) : FirestoreSyncException(
        message = "Firestore sync failed.",
        cause = cause
    )
}
