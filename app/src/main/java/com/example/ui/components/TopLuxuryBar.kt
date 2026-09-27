package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.LuxuryBorder
import com.example.ui.theme.LuxuryBorderGold
import com.example.ui.theme.LuxurySurface
import com.example.ui.theme.LuxurySurfaceElevated
import com.example.ui.theme.NeonActiveCyan
import com.example.ui.theme.NeonSpeedGreen
import com.example.ui.theme.TextGold
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.LuxuryHaptics

@Composable
fun TopLuxuryBar(
    isHandsFree: Boolean,
    isMuted: Boolean,
    isAccessibilityEnabled: Boolean,
    onToggleHandsFree: () -> Unit,
    onToggleMute: () -> Unit,
    onOpenAccessibility: () -> Unit,
    onOpenAppDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Surface(
        color = LuxurySurface,
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 0.5.dp,
                color = LuxuryBorderGold.copy(alpha = 0.4f),
                shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
            ),
        shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Brand Crest & Title
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(GoldDark)
                            .border(1.5.dp, GoldPrimary, CircleShape)
                    ) {
                        Text(
                            text = "ताऊ",
                            color = GoldSecondary,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "TAU AI",
                                color = GoldPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = NeonSpeedGreen.copy(alpha = 0.15f),
                                modifier = Modifier.border(
                                    0.5.dp,
                                    NeonSpeedGreen.copy(alpha = 0.5f),
                                    RoundedCornerShape(10.dp)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Bolt,
                                        contentDescription = "Fast Speed",
                                        tint = NeonSpeedGreen,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Text(
                                        text = "<0.5s",
                                        color = NeonSpeedGreen,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        Text(
                            text = "हरियाणवी वॉइस कंट्रोलर",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                // Action Controls: Hands-Free, Mute, Apps list
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Hands-Free Pill
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isHandsFree) NeonActiveCyan.copy(alpha = 0.18f) else LuxurySurfaceElevated,
                        modifier = Modifier
                            .border(
                                1.dp,
                                if (isHandsFree) NeonActiveCyan else LuxuryBorder,
                                RoundedCornerShape(16.dp)
                            )
                            .clickable {
                                LuxuryHaptics.lightTick(context)
                                onToggleHandsFree()
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = "Hands Free",
                                tint = if (isHandsFree) NeonActiveCyan else TextSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isHandsFree) "हैंड्स-फ्री ON" else "हैंड्स-फ्री",
                                color = if (isHandsFree) NeonActiveCyan else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Audio Mute/Unmute
                    IconButton(
                        onClick = {
                            LuxuryHaptics.lightTick(context)
                            onToggleMute()
                        },
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.AutoMirrored.Filled.VolumeMute else Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Mute Toggle",
                            tint = if (isMuted) TextSecondary else GoldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // All Apps Drawer Trigger
                    IconButton(
                        onClick = {
                            LuxuryHaptics.lightTick(context)
                            onOpenAppDrawer()
                        },
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Apps,
                            contentDescription = "All Apps",
                            tint = GoldSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Accessibility prompt banner if not enabled
            if (!isAccessibilityEnabled) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = LuxuryBorder.copy(alpha = 0.4f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            LuxuryHaptics.lightTick(context)
                            onOpenAccessibility()
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "होम, बैक व रीसेंट्स के संपूर्ण कंट्रोल खातर सर्विस ऑन करो",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "चालू करें >",
                            color = GoldPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
