package pt.iade.lane.data.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.tasks.await

object LocationUtils {
    const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private val LOCATION_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    fun hasLocationPermission(context: Context): Boolean {
        val fineGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarseGrand = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        return fineGranted || coarseGrand
    }

fun requestLocationPermission(activity: Activity) {
    if (!hasLocationPermission(activity)) {
        ActivityCompat.requestPermissions(activity, LOCATION_PERMISSIONS, LOCATION_PERMISSION_REQUEST_CODE)
    }
}
    @SuppressLint("MissingPermission")
    suspend fun getLastKnownLocation(context: Context): LatLng? {
    if (!hasLocationPermission(context)) return null

    return try {
        val fusedClient = LocationServices.getFusedLocationProviderClient(context)
        val location = fusedClient.lastLocation.await()
        if (location != null) {
            LatLng(location.latitude, location.longitude)
        } else {
            null
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
}