package com.example.studyapp.map

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri


fun openMaps(context: Context, buildingName: String) {
    val cleanedName = buildingName.trim()
    if (cleanedName.isEmpty()) return

    val search = "$cleanedName, Arkansas State University, Jonesboro, Arkansas"
    val mapIntent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse("geo:0,0?q=${Uri.encode(search)}")
    )

    try {
        context.startActivity(mapIntent)
    } catch (_: ActivityNotFoundException) {
        // A browser is a useful fallback on devices without a maps app.
        val webUri = Uri.parse(
            "https://www.google.com/maps/search/?api=1&query=${Uri.encode(search)}"
        )
        context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
    }
}
