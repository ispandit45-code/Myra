package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.LuxuryBackground
import com.example.ui.theme.LuxuryBorderGold
import com.example.ui.theme.LuxurySurfaceCard
import com.example.ui.theme.LuxurySurfaceElevated
import com.example.ui.theme.NeonActiveCyan
import com.example.ui.theme.NeonSpeedGreen
import com.example.ui.theme.TextGold
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.LuxuryHaptics
import com.example.voice.VoiceState
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun LuxuryVoiceOrb(
    voiceState: VoiceState,
    audioRms: Float,
    partialText: String,
    isHandsFree: Boolean,
    onOrbClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val infiniteTransition = rememberInfiniteTransition(label = "orb_rotation")
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "idle_pulse"
    )

    // Dynamic scale responding to mic volume
    val dynamicAudioScale = remember { Animatable(1f) }
    LaunchedEffect(audioRms, voiceState) {
        if (voiceState == VoiceState.LISTENING) {
            val target = 1f + (audioRms * 0.45f)
            dynamicAudioScale.animateTo(target, tween(70))
        } else {
            dynamicAudioScale.animateTo(1f, tween(180))
        }
    }

    val finalScale = if (voiceState == VoiceState.LISTENING) dynamicAudioScale.value else pulseScale

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Voice Orb Visualizer Box
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(150.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    LuxuryHaptics.orbActivate(context)
                    onOrbClick()
                }
        ) {
            // Background Canvas: Multi-layer concentric radiant energy rings
            Canvas(
                modifier = Modifier
                    .size(140.dp)
                    .scale(finalScale)
            ) {
                val center = Offset(size.width / 2, size.height / 2)
                val baseRadius = size.width / 2

                // Outer ambient glow
                val outerGlowColor = when (voiceState) {
                    VoiceState.LISTENING -> NeonActiveCyan.copy(alpha = 0.35f + (audioRms * 0.4f))
                    VoiceState.PROCESSING -> NeonSpeedGreen.copy(alpha = 0.4f)
                    VoiceState.SPEAKING -> GoldPrimary.copy(alpha = 0.45f)
                    else -> GoldPrimary.copy(alpha = 0.15f)
                }

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(outerGlowColor, Color.Transparent),
                        center = center,
                        radius = baseRadius
                    ),
                    radius = baseRadius,
                    center = center
                )

                // Concentric Orbital Ring 1
                drawCircle(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            GoldPrimary,
                            NeonActiveCyan.copy(alpha = 0.6f),
                            GoldSecondary,
                            GoldDark,
                            GoldPrimary
                        ),
                        center = center
                    ),
                    radius = baseRadius * 0.88f,
                    center = center,
                    style = Stroke(width = 2.5.dp.toPx())
                )

                // Orbiting Particle beads
                val rad = Math.toRadians(rotation.toDouble())
                val orbitRadius = baseRadius * 0.88f
                val beadX = (center.x + orbitRadius * cos(rad)).toFloat()
                val beadY = (center.y + orbitRadius * sin(rad)).toFloat()
                drawCircle(
                    color = GoldSecondary,
                    radius = 4.dp.toPx(),
                    center = Offset(beadX, beadY)
                )

                // Opposite Bead
                val oppBeadX = (center.x - orbitRadius * cos(rad)).toFloat()
                val oppBeadY = (center.y - orbitRadius * sin(rad)).toFloat()
                drawCircle(
                    color = NeonActiveCyan,
                    radius = 3.5.dp.toPx(),
                    center = Offset(oppBeadX, oppBeadY)
                )

                // Inner core sphere
                val coreColors = when (voiceState) {
                    VoiceState.LISTENING -> listOf(
                        Color(0xFF003844),
                        Color(0xFF001F26),
                        Color(0xFF09090E)
                    )
                    VoiceState.PROCESSING -> listOf(
                        Color(0xFF003B26),
                        Color(0xFF002216),
                        Color(0xFF09090E)
                    )
                    VoiceState.SPEAKING -> listOf(
                        Color(0xFF422F08),
                        Color(0xFF261A00),
                        Color(0xFF09090E)
                    )
                    else -> listOf(
                        Color(0xFF1F1A0E),
                        Color(0xFF14120B),
                        Color(0xFF09090E)
                    )
                }

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = coreColors,
                        center = center,
                        radius = baseRadius * 0.72f
                    ),
                    radius = baseRadius * 0.72f,
                    center = center
                )

                // Inner accent ring
                drawCircle(
                    color = if (voiceState == VoiceState.LISTENING) NeonActiveCyan.copy(alpha = 0.8f) else GoldPrimary.copy(alpha = 0.5f),
                    radius = baseRadius * 0.72f,
                    center = center,
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }

            // Center Icon inside luxury orb
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                GoldPrimary.copy(alpha = 0.22f),
                                Color.Transparent
                            )
                        )
                    )
            ) {
                val iconTint = when (voiceState) {
                    VoiceState.LISTENING -> NeonActiveCyan
                    VoiceState.PROCESSING -> NeonSpeedGreen
                    VoiceState.SPEAKING -> GoldPrimary
                    else -> GoldSecondary
                }

                Icon(
                    imageVector = when (voiceState) {
                        VoiceState.LISTENING -> Icons.Default.Mic
                        VoiceState.SPEAKING -> Icons.Default.Stop
                        else -> Icons.Default.Mic
                    },
                    contentDescription = "Voice Control",
                    tint = iconTint,
                    modifier = Modifier.size(34.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Live Partial Voice Transcript or Status Text
        if (voiceState == VoiceState.LISTENING && partialText.isNotBlank()) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = LuxurySurfaceElevated,
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .drawBehind {
                        drawRoundRect(
                            color = NeonActiveCyan.copy(alpha = 0.4f),
                            style = Stroke(width = 1.dp.toPx())
                        )
                    }
            ) {
                Text(
                    text = "“$partialText”",
                    color = NeonActiveCyan,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }
        } else {
            // Status Tagline
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                val statusColor = when (voiceState) {
                    VoiceState.LISTENING -> NeonActiveCyan
                    VoiceState.PROCESSING -> NeonSpeedGreen
                    VoiceState.SPEAKING -> GoldPrimary
                    else -> TextGold
                }

                val statusText = when (voiceState) {
                    VoiceState.LISTENING -> "सुणूँ सूं, बोल लाडले… (Listening)"
                    VoiceState.PROCESSING -> "⚡ हुक्म समझ रह्या सूं…"
                    VoiceState.SPEAKING -> "🗣️ ताऊ बोल रह्या सै…"
                    VoiceState.IDLE -> if (isHandsFree) "🎙️ हैंड्स-फ्री एक्टिव सै, सीधा बोलो" else "🎙️ बोलण खातर ओर्ब पै दबाओ"
                    VoiceState.ERROR -> "⚠️ माइक शुरू कोन्या होया, फेर दबाओ"
                }

                Text(
                    text = statusText,
                    color = statusColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
