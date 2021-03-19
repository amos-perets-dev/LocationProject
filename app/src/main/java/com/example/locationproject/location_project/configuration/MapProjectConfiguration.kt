package com.example.locationproject.location_project.configuration

class MapProjectConfiguration : IMapProjectConfiguration{

    private val limitDegrees = 45
    private val radiusPlace = 100F

    override fun getLimitDegrees(): Int = limitDegrees
    override fun getRadiusPlace(): Float = radiusPlace

}