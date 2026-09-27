package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.controller.PhoneController
import com.example.engine.HaryanviFastEngine
import com.example.model.ActionType
import com.example.model.AppInfo
import com.example.model.ChatMessage
import com.example.model.MessageSender
import com.example.service.TauAccessibilityService
import com.example.ui.components.AppDrawerSheet
import com.example.ui.components.ChatFeedItem
import com.example.ui.components.LuxuryVoiceOrb
import com.example.ui.components.QuickActionPills
import com.example.ui.components.TopLuxuryBar
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.LuxuryBackground
import com.example.ui.theme.LuxuryBorder
import com.example.ui.theme.LuxuryBorderGold
import com.example.ui.theme.LuxurySurface
import com.example.ui.theme.LuxurySurfaceElevated
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextGold
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.util.LuxuryHaptics
import com.example.voice.HaryanviVoiceEngine
import com.example.voice.VoiceState
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var phoneController: PhoneController
    private lateinit var fastEngine: HaryanviFastEngine
    private var voiceEngine: HaryanviVoiceEngine? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        phoneController = PhoneController(applicationContext)
        fastEngine = HaryanviFastEngine(phoneController)

        setContent {
            MyApplicationTheme {
                MainScreen(
                    phoneController = phoneController,
                    fastEngine = fastEngine,
                    onInitVoiceEngine = { callback ->
                        if (voiceEngine == null) {
                            voiceEngine = HaryanviVoiceEngine(applicationContext, callback)
                        }
                        voiceEngine!!
                    }
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        voiceEngine?.release()
    }
}

@Composable
fun MainScreen(
    phoneController: PhoneController,
    fastEngine: HaryanviFastEngine,
    onInitVoiceEngine: ((String) -> Unit) -> HaryanviVoiceEngine
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Installed apps
    var installedApps by remember { mutableStateOf<List<AppInfo>>(emptyList()) }
    var isAppDrawerOpen by remember { mutableStateOf(false) }

    // Chat feed state
    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                sender = MessageSender.TAU,
                text = "राम राम भाई! म्हारा नाम ताऊ AI सै। फोन का असली चौधरी, जो थारे एक बोल पै सारे काम चुटकी म्ह कर दे!\n\nबोल के हुक्म सै? चाहे कह दे 'व्हॉट्सऐप खोल', 'यूट्यूब चला', 'टॉर्च जगा' या 'होम पै जा'!",
                actionType = ActionType.CONVERSATION,
                latencyMs = 12L
            )
        )
    }

    // Input state
    var inputText by remember { mutableStateOf("") }
    var isHandsFree by remember { mutableStateOf(false) }
    var isMuted by remember { mutableStateOf(false) }
    var isAccessibilityEnabled by remember { mutableStateOf(TauAccessibilityService.isConnected()) }

    // Voice Engine instance
    var voiceEngineInstance by remember { mutableStateOf<HaryanviVoiceEngine?>(null) }

    // Function to execute user command with sub-500ms feedback
    val handleCommand: (String) -> Unit = remember(isMuted) {
        { command: String ->
            if (command.isNotBlank()) {
                LuxuryHaptics.lightTick(context)

                // Add user query
                messages.add(
                    ChatMessage(
                        sender = MessageSender.USER,
                        text = command
                    )
                )

                // Process through sub-500ms engine
                val result = fastEngine.executeCommand(command)
                messages.add(result.message)

                if (result.isSuccess) {
                    LuxuryHaptics.successPulse(context)
                } else {
                    LuxuryHaptics.errorBuzz(context)
                }

                coroutineScope.launch {
                    listState.animateScrollToItem(messages.size - 1)
                }

                // Voice output
                if (!isMuted) {
                    voiceEngineInstance?.speak(result.spokenHaryanviText)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        val engine = onInitVoiceEngine { spokenText ->
            handleCommand(spokenText)
        }
        voiceEngineInstance = engine
    }

    // Load installed apps in background on startup
    LaunchedEffect(Unit) {
        installedApps = phoneController.getInstalledApps()
    }

    val voiceState by voiceEngineInstance?.voiceState?.collectAsState() ?: remember { mutableStateOf(VoiceState.IDLE) }
    val audioRms by voiceEngineInstance?.audioRms?.collectAsState() ?: remember { mutableStateOf(0f) }
    val partialText by voiceEngineInstance?.partialText?.collectAsState() ?: remember { mutableStateOf("") }

    // Audio record permission launcher
    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasAudioPermission = isGranted
        if (isGranted) {
            voiceEngineInstance?.startListening()
        }
    }

    fun startVoiceListening() {
        if (hasAudioPermission) {
            if (voiceState == VoiceState.LISTENING) {
                voiceEngineInstance?.stopListening()
            } else {
                voiceEngineInstance?.startListening()
            }
        } else {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopLuxuryBar(
                isHandsFree = isHandsFree,
                isMuted = isMuted,
                isAccessibilityEnabled = isAccessibilityEnabled,
                onToggleHandsFree = {
                    isHandsFree = !isHandsFree
                    voiceEngineInstance?.isHandsFreeEnabled = isHandsFree
                    if (isHandsFree) {
                        startVoiceListening()
                    } else {
                        voiceEngineInstance?.stopListening()
                    }
                },
                onToggleMute = {
                    isMuted = !isMuted
                    voiceEngineInstance?.isVoiceSpeechMuted = isMuted
                    if (isMuted) {
                        voiceEngineInstance?.stopSpeaking()
                    }
                },
                onOpenAccessibility = {
                    TauAccessibilityService.openAccessibilitySettings(context)
                },
                onOpenAppDrawer = {
                    if (installedApps.isEmpty()) {
                        installedApps = phoneController.getInstalledApps(true)
                    }
                    isAppDrawerOpen = true
                }
            )
        },
        containerColor = LuxuryBackground,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
        ) {
            // Interactive 3D Voice Orb
            LuxuryVoiceOrb(
                voiceState = voiceState,
                audioRms = audioRms,
                partialText = partialText,
                isHandsFree = isHandsFree,
                onOrbClick = {
                    if (voiceState == VoiceState.SPEAKING) {
                        voiceEngineInstance?.stopSpeaking()
                    } else {
                        startVoiceListening()
                    }
                },
                modifier = Modifier.padding(top = 4.dp)
            )

            // Fast Command Pill Shortcuts
            QuickActionPills(
                onCommandSelected = { command ->
                    handleCommand(command)
                }
            )

            // Chat Messages Feed
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(messages, key = { it.id }) { message ->
                    ChatFeedItem(
                        message = message,
                        onLaunchApp = { pkg ->
                            phoneController.launchApp(pkg)
                        },
                        onReplaySpeech = { text ->
                            voiceEngineInstance?.speak(text)
                        }
                    )
                }
            }

            // Bottom Command Input Bar
            Surface(
                color = LuxurySurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        0.5.dp,
                        LuxuryBorderGold.copy(alpha = 0.4f),
                        RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                    )
                    .navigationBarsPadding(),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = {
                            Text(
                                text = "हुक्म लिखो (उदा: 'व्हॉट्सऐप खोल')…",
                                color = TextTertiary,
                                fontSize = 13.sp
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = LuxuryBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            cursorColor = GoldPrimary,
                            focusedContainerColor = LuxurySurfaceElevated,
                            unfocusedContainerColor = LuxurySurfaceElevated
                        ),
                        trailingIcon = {
                            if (inputText.isNotEmpty()) {
                                IconButton(onClick = { inputText = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear",
                                        tint = TextSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                if (inputText.isNotBlank()) {
                                    val cmd = inputText
                                    inputText = ""
                                    handleCommand(cmd)
                                }
                            }
                        ),
                        shape = RoundedCornerShape(24.dp),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("command_input_field")
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Send or Mic action button
                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                val cmd = inputText
                                inputText = ""
                                handleCommand(cmd)
                            } else {
                                startVoiceListening()
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(GoldPrimary)
                            .testTag("action_button")
                    ) {
                        Icon(
                            imageVector = if (inputText.isNotBlank()) Icons.AutoMirrored.Filled.Send else Icons.Default.Mic,
                            contentDescription = "Submit",
                            tint = GoldDark,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }

    // App Drawer Modal Bottom Sheet
    if (isAppDrawerOpen) {
        AppDrawerSheet(
            apps = installedApps,
            onDismiss = { isAppDrawerOpen = false },
            onAppSelected = { app ->
                isAppDrawerOpen = false
                handleCommand("${app.appName} खोल")
            }
        )
    }
}
