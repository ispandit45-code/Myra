package com.example.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.LuxuryBorderGold
import com.example.ui.theme.LuxurySurfaceElevated
import com.example.ui.theme.TextGold
import com.example.ui.theme.TextPrimary
import com.example.util.LuxuryHaptics

data class QuickActionItem(
    val icon: ImageVector,
    val title: String,
    val command: String
)

@Composable
fun QuickActionPills(
    onCommandSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val actions = listOf(
        QuickActionItem(Icons.Default.Chat, "व्हॉट्सऐप", "व्हॉट्सऐप खोल दे"),
        QuickActionItem(Icons.Default.PlayArrow, "यूट्यूब", "यूट्यूब पै हरियाणवी गाने चला"),
        QuickActionItem(Icons.Default.FlashlightOn, "टॉर्च", "टॉर्च जगा दे"),
        QuickActionItem(Icons.Default.CameraAlt, "कैमरा", "कैमरा खोल रै"),
        QuickActionItem(Icons.Default.BatteryChargingFull, "बैटरी", "बैटरी कितनी सै?"),
        QuickActionItem(Icons.Default.Home, "होम स्क्रीन", "होम स्क्रीन खोल"),
        QuickActionItem(Icons.Default.Layers, "रीसेंट ऐप्स", "रीसेंट ऐप्स दिखा"),
        QuickActionItem(Icons.Default.Phone, "डायलर", "फोन मिला"),
        QuickActionItem(Icons.Default.Settings, "सेटिंग्स", "फोन सेटिंग्स खोल"),
        QuickActionItem(Icons.Default.Mood, "चुटकुला", "ताऊ, कोई हरियाणवी चुटकुला सुणा")
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        actions.forEach { action ->
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = LuxurySurfaceElevated,
                modifier = Modifier
                    .border(1.dp, LuxuryBorderGold.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                    .clickable {
                        LuxuryHaptics.lightTick(context)
                        onCommandSelected(action.command)
                    }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = action.icon,
                        contentDescription = action.title,
                        tint = GoldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = action.title,
                        color = TextGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
