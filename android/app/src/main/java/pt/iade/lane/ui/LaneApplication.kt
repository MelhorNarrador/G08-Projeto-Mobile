package pt.iade.lane

import android.app.Application
import android.util.Log
import com.google.android.libraries.places.api.Places

class LaneApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            val appInfo = packageManager.getApplicationInfo(
                packageName,
                android.content.pm.PackageManager.GET_META_DATA
            )
            val apiKey = appInfo.metaData.getString("com.google.android.geo.API_KEY")
            if (!Places.isInitialized() && apiKey != null) {
                Places.initialize(applicationContext, apiKey)
                Log.d("LaneApplication", "Google Places inicializado com sucesso.")
            }
        } catch (e: Exception) {
            Log.e("LaneApplication", "Erro ao inicializar Places: ${e.message}")
        }
    }
}