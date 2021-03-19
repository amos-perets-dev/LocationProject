package com.example.locationproject.location_project.configuration

interface IMapProjectConfiguration {

    /**
     * Get the limit degrees of the points in the user field
     */
    fun getLimitDegrees(): Int

    /**
     * Get the radius of place
     */
    fun getRadiusPlace(): Float

}