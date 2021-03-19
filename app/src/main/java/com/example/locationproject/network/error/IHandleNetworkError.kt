package com.example.locationproject.network.error

interface IHandleNetworkError {

    /**
     * Call to generate the error from the server
     * @param throwable - error from the server
     *
     * @return [Int] error ID
     */
    fun generateErrorID(throwable: Throwable): Int
}