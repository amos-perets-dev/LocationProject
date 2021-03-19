package com.example.locationproject.location_project.configuration

class MapProjectConfiguration : IMapProjectConfiguration{

    private val limitDegrees = 100
    private val radiusPlace = 1000F

    override fun getLimitDegrees(): Int = limitDegrees
    override fun getRadiusPlace(): Float = radiusPlace

}