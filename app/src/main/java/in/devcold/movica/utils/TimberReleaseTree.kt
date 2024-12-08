package `in`.devcold.movica.utils

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import `in`.devcold.movica.data.remote.ConnectivityException
import kotlinx.coroutines.CancellationException
import timber.log.Timber

class TimberReleaseTree : Timber.Tree() {

    override fun isLoggable(tag: String?, priority: Int) = priority >= Log.WARN

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (t != null && t !is CancellationException && t !is ConnectivityException) {
            FirebaseCrashlytics.getInstance().recordException(t)
        }
    }
}
