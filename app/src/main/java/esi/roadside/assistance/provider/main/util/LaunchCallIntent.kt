package esi.roadside.assistance.provider.main.util

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import kotlin.apply
import kotlin.let

fun Context.launchCallIntent(phoneNumber: String) {
    Intent(Intent.ACTION_DIAL).apply {
        data = "tel:$phoneNumber".toUri()
    }.let {
        if (it.resolveActivity(this.packageManager) != null) startActivity(it)
    }
}