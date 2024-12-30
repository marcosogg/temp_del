package ie.setu.tazq

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.initialize
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class TazqMainApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Firebase.initialize(this)
        Timber.plant(Timber.DebugTree())
    }
}