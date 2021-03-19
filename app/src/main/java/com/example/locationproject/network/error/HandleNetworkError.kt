package com.example.locationproject.network.error

import androidx.annotation.StringRes
import com.example.locationproject.R
import java.net.ConnectException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException
import javax.net.ssl.SSLHandshakeException
import javax.net.ssl.SSLProtocolException

class HandleNetworkError : IHandleNetworkError {

    @StringRes
    override fun generateErrorID(throwable: Throwable): Int {
        if (throwable is UnknownHostException
            || throwable is SSLHandshakeException
            || throwable is TimeoutException
            || throwable is ConnectException
            || throwable is SSLProtocolException
        ) {
            return R.string.error_internet_unavailable_text

        }
        return R.string.error_general_error_text
    }

}