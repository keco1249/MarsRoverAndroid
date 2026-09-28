package com.kc.marsrovers.navigation

object Routes {
    const val HOME = "home"

    const val ROVER_NAME_ARG = "roverName"
    const val ROVER = "rover/{$ROVER_NAME_ARG}"
    fun rover(roverName: String) = "rover/$roverName"
}
