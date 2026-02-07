package com.pbogdev.viberadar

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform