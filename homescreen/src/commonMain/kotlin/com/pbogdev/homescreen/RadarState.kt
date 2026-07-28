package com.pbogdev.homescreen

enum class RadarState {
    IDLE,       // Showing input fields, radar is static
    SEARCHING,  // Inputs hidden, radar is animating/pulsing
    MATCHES     // Inputs hidden, potential matches shown over static/pulsing radar
}