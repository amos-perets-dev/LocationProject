package com.example.locationproject.network.base

import retrofit2.Retrofit

interface IBaseNetworkManager {

    /**
     * Create the retrofit to handel the request / response
     */
    fun buildRetrofit(): Retrofit?
}