package com.pbogdev.sharedui.theme


import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.pbogdev.sharedui.Res
import com.pbogdev.sharedui.inter_bold
import com.pbogdev.sharedui.inter_medium
import com.pbogdev.sharedui.inter_regular
import com.pbogdev.sharedui.inter_semibold
import org.jetbrains.compose.resources.Font


@Composable
fun getInterFontFamily(): FontFamily {
    return FontFamily(
        Font(resource = Res.font.inter_regular, weight = FontWeight.Normal),
        Font(resource = Res.font.inter_medium, weight = FontWeight.Medium),
        Font(resource = Res.font.inter_semibold, weight = FontWeight.SemiBold),
        Font(resource = Res.font.inter_bold, weight = FontWeight.Bold)
    )
}

@Composable
fun getVibeTypography(): Typography {
    val interFontFamily = getInterFontFamily()

    return Typography(
        // Massive Match Percentages (e.g., "95%")
        displayLarge = TextStyle(
            fontFamily = interFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 57.sp,
            lineHeight = 64.sp,
            letterSpacing = (-0.25).sp
        ),

        // Main Screen / Overlay Titles
        headlineMedium = TextStyle(
            fontFamily = interFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 28.sp,
            lineHeight = 36.sp
        ),

        // Collapsed Match Items, Settings Headers
        titleMedium = TextStyle(
            fontFamily = interFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.15.sp
        ),

        // The Vibe Text, Input Fields, Chat Messages
        bodyLarge = TextStyle(
            fontFamily = interFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.5.sp
        ),

        // Shared Interests, Privacy Policy
        bodyMedium = TextStyle(
            fontFamily = interFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.25.sp
        ),

        // Buttons (Message, Scan, Save)
        labelLarge = TextStyle(
            fontFamily = interFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp
        ),

        // Timestamps, TTL warnings, tiny metadata
        labelSmall = TextStyle(
            fontFamily = interFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp
        )
    )
}