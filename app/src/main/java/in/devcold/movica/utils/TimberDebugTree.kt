package `in`.devcold.movica.utils

import timber.log.Timber

class TimberDebugTree : Timber.DebugTree() {

    override fun createStackElementTag(element: StackTraceElement) = with(element) {
        "($fileName:$lineNumber), ($methodName)"
    }
}
