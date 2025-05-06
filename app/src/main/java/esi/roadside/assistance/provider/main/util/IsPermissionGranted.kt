package esi.roadside.assistance.provider.main.util

import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED


fun Context.isPermissionGranted(permission: String): Boolean {
    return checkSelfPermission(permission) == PERMISSION_GRANTED
}