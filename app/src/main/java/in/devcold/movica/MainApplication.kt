package `in`.devcold.movica

import android.app.Application
import `in`.devcold.movica.utils.TimberDebugTree
import `in`.devcold.movica.utils.TimberReleaseTree
import timber.log.Timber

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val tree = if (BuildConfig.DEBUG) TimberDebugTree() else TimberReleaseTree()
        Timber.plant(tree)
    }
}