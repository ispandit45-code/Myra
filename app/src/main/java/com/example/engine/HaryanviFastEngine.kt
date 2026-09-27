package com.example.engine

import com.example.controller.PhoneController
import com.example.model.ActionType
import com.example.model.AppInfo
import com.example.model.ChatMessage
import com.example.model.MessageSender

data class CommandResult(
    val message: ChatMessage,
    val spokenHaryanviText: String,
    val isSuccess: Boolean
)

class HaryanviFastEngine(private val phoneController: PhoneController) {

    private val haryanviGreetings = listOf(
        "राम राम भाई! बोल लाडले, आज के हुक्म सै?",
        "जमा राजी-खुशी चौधरा! बता के काम निपटाऊं?",
        "हाँ भाई, ताऊ हाजिर सै! हुक्म कर!"
    )

    private val haryanviJokes = listOf(
        "एक बार ताऊ डॉक्टर पै गया। डॉक्टर बोल्या - 'ताऊ, मीठा खाणा छोड़ दे।' ताऊ बोल्या - 'रै डाक्टर, मीठा तो छोड़ दूंगा, पर गुड़ की चा नै ना कहिए!'",
        "एक छोरा ताऊ ताहिं बोल्या - 'ताऊ, अंग्रेजी बोलना सिखा दे।' ताऊ बोल्या - 'रै लाडले, हरियाणवी म्ह जो दम सै वो अंग्रेजी म्ह कित! बोल - जमा धाकड़!'",
        "ताऊ बस म्ह बैठ्या था। कंडक्टर बोल्या - 'ताऊ, टिकट ले ले।' ताऊ बोल्या - 'ना भाई, म्हनै तो खाली सीट पै बैठना सै, टिकट पै कोन्या!'",
        "छोरा - 'बाबू, फोन म्ह ताऊ AI आ गया सै!' बाबू - 'अच्छा, फेर तो हुक्म चला, खेत म्ह पानी भी योहे लगवावैगा के!'"
    )

    fun executeCommand(rawInput: String): CommandResult {
        val startTime = System.currentTimeMillis()
        val text = rawInput.trim()
        val lower = text.lowercase()

        // 1. Check for Conversational & Greetings
        if (matchesGreeting(lower)) {
            val reply = haryanviGreetings.random()
            val latency = System.currentTimeMillis() - startTime
            return CommandResult(
                message = ChatMessage(
                    sender = MessageSender.TAU,
                    text = reply,
                    actionType = ActionType.CONVERSATION,
                    latencyMs = latency
                ),
                spokenHaryanviText = reply,
                isSuccess = true
            )
        }

        if (lower.contains("हाल चाल") || lower.contains("haal chaal") || lower.contains("kaisa hai") || lower.contains("कैसा सै")) {
            val reply = "जमा कसूता भाई! भगवान की दया त सब राजी-खुशी सै। तू बता, के सेवा करूँ तेरी?"
            val latency = System.currentTimeMillis() - startTime
            return CommandResult(
                message = ChatMessage(
                    sender = MessageSender.TAU,
                    text = reply,
                    actionType = ActionType.CONVERSATION,
                    latencyMs = latency
                ),
                spokenHaryanviText = reply,
                isSuccess = true
            )
        }

        if (lower.contains("कौन सै") || lower.contains("kaun hai") || lower.contains("tera naam") || lower.contains("तेरा नाम") || lower.contains("who are you")) {
            val reply = "म्हारा नाम ताऊ AI सै लाडले! हरियाणा का पहला धाकड़ AI असिस्टेंट, जो तेरे फोन नै चुटकियों म्ह चलावै सै!"
            val latency = System.currentTimeMillis() - startTime
            return CommandResult(
                message = ChatMessage(
                    sender = MessageSender.TAU,
                    text = reply,
                    actionType = ActionType.CONVERSATION,
                    latencyMs = latency
                ),
                spokenHaryanviText = reply,
                isSuccess = true
            )
        }

        if (lower.contains("चुटकुला") || lower.contains("chutkula") || lower.contains("joke") || lower.contains("हंसा") || lower.contains("मजाक")) {
            val joke = haryanviJokes.random()
            val latency = System.currentTimeMillis() - startTime
            return CommandResult(
                message = ChatMessage(
                    sender = MessageSender.TAU,
                    text = joke,
                    actionType = ActionType.CONVERSATION,
                    latencyMs = latency
                ),
                spokenHaryanviText = joke,
                isSuccess = true
            )
        }

        // 2. System Hardware: Torch / Flashlight
        if (matchesTorch(lower)) {
            val turnOff = lower.contains("बंद") || lower.contains("band") || lower.contains("off") || lower.contains("बुझा") || lower.contains("bujha")
            val targetState = !turnOff
            val (ok, state) = phoneController.toggleTorch(targetState)
            val latency = System.currentTimeMillis() - startTime
            val reply = if (ok) {
                if (state) "ले भाई, बत्ती बाळ दी! पूरा चानणा कर दिया।" else "टॉर्च बुझा दी लाडले, बत्ती बंद हो गी।"
            } else {
                "अरै यार, फोन का फ्लैशलाइट ऑन कोन्या हो पा रह्या!"
            }
            return CommandResult(
                message = ChatMessage(
                    sender = MessageSender.TAU,
                    text = reply,
                    actionType = ActionType.SYSTEM_ACTION,
                    actionDetails = if (state) "टॉर्च चालू (ON)" else "टॉर्च बंद (OFF)",
                    latencyMs = latency
                ),
                spokenHaryanviText = reply,
                isSuccess = ok
            )
        }

        // 3. Battery status
        if (matchesBattery(lower)) {
            val (level, isCharging) = phoneController.getBatteryLevel()
            val latency = System.currentTimeMillis() - startTime
            val chargeStatus = if (isCharging) "और चार्जिंग चाल रही सै।" else "चार्जिंग पै कोन्या।"
            val reply = if (level >= 0) {
                "चौधरा, फोन म्ह $level% बैटरी बची सै, $chargeStatus"
            } else {
                "बैटरी की जानकारी ना मिल पाई भाई!"
            }
            return CommandResult(
                message = ChatMessage(
                    sender = MessageSender.TAU,
                    text = reply,
                    actionType = ActionType.SYSTEM_ACTION,
                    actionDetails = "बैटरी: $level% ${if (isCharging) "⚡ चार्जिंग" else ""}",
                    latencyMs = latency
                ),
                spokenHaryanviText = reply,
                isSuccess = level >= 0
            )
        }

        // 4. Navigation: Home Screen / Close App
        if (matchesHome(lower)) {
            val ok = phoneController.goHome()
            val latency = System.currentTimeMillis() - startTime
            val reply = "ले चौधरा, सीधा होम स्क्रीन पै पहुंचा दिया!"
            return CommandResult(
                message = ChatMessage(
                    sender = MessageSender.TAU,
                    text = reply,
                    actionType = ActionType.SYSTEM_ACTION,
                    actionDetails = "होम स्क्रीन (Home Action)",
                    latencyMs = latency
                ),
                spokenHaryanviText = reply,
                isSuccess = ok
            )
        }

        // 5. Navigation: Recent Apps
        if (matchesRecents(lower)) {
            val ok = phoneController.openRecents()
            val latency = System.currentTimeMillis() - startTime
            val reply = if (ok) {
                "ले भाई, सारी रीसेंट ऍप्स खोल दी, छांट ले!"
            } else {
                "रीसेंट ऍप्स खातर ताऊ AI की एक्सेसिबिलिटी सर्विस ऑन करणी पड़ैगी भाई!"
            }
            return CommandResult(
                message = ChatMessage(
                    sender = MessageSender.TAU,
                    text = reply,
                    actionType = ActionType.SYSTEM_ACTION,
                    actionDetails = "हालिया ऐप्स (Recent Apps)",
                    latencyMs = latency
                ),
                spokenHaryanviText = reply,
                isSuccess = ok
            )
        }

        // 6. Navigation: Notification shade
        if (lower.contains("notification") || lower.contains("नोटिफिकेशन") || lower.contains("सूचना") || lower.contains("मैसेज दिखा")) {
            val ok = phoneController.openNotifications()
            val latency = System.currentTimeMillis() - startTime
            val reply = if (ok) {
                "सारे नोटिफिकेशन नीचै कर दिए, देख ले के-के आया सै!"
            } else {
                "नोटिफिकेशन खातर एक्सेसिबिलिटी इज़ाज़त दे दो भाई!"
            }
            return CommandResult(
                message = ChatMessage(
                    sender = MessageSender.TAU,
                    text = reply,
                    actionType = ActionType.SYSTEM_ACTION,
                    actionDetails = "नोटिफिकेशन पैनल",
                    latencyMs = latency
                ),
                spokenHaryanviText = reply,
                isSuccess = ok
            )
        }

        // 7. System Settings / Wifi / Bluetooth
        if (lower.contains("wifi") || lower.contains("वाईफाई") || lower.contains("वाइफ़ाई")) {
            val ok = phoneController.openWifiSettings()
            val latency = System.currentTimeMillis() - startTime
            val reply = "ले भाई, वाईफाई सेटिंग खोल दी!"
            return CommandResult(
                message = ChatMessage(
                    sender = MessageSender.TAU,
                    text = reply,
                    actionType = ActionType.SYSTEM_ACTION,
                    actionDetails = "Wi-Fi सेटिंग्स",
                    latencyMs = latency
                ),
                spokenHaryanviText = reply,
                isSuccess = ok
            )
        }

        if (lower.contains("bluetooth") || lower.contains("ब्लूटूथ") || lower.contains("ब्लुटूथ")) {
            val ok = phoneController.openBluetoothSettings()
            val latency = System.currentTimeMillis() - startTime
            val reply = "ले भाई, ब्लूटूथ सेटिंग खोल दी!"
            return CommandResult(
                message = ChatMessage(
                    sender = MessageSender.TAU,
                    text = reply,
                    actionType = ActionType.SYSTEM_ACTION,
                    actionDetails = "ब्लूटूथ सेटिंग्स",
                    latencyMs = latency
                ),
                spokenHaryanviText = reply,
                isSuccess = ok
            )
        }

        if (lower.contains("sound") || lower.contains("volume") || lower.contains("आवाज") || lower.contains("आवाज़") || lower.contains("साउंड")) {
            val ok = phoneController.openSoundSettings()
            val latency = System.currentTimeMillis() - startTime
            val reply = "आवाज़ की सेटिंग खोल दी भाई, जितनी चाहे कर ले!"
            return CommandResult(
                message = ChatMessage(
                    sender = MessageSender.TAU,
                    text = reply,
                    actionType = ActionType.SYSTEM_ACTION,
                    actionDetails = "ध्वनि सेटिंग्स",
                    latencyMs = latency
                ),
                spokenHaryanviText = reply,
                isSuccess = ok
            )
        }

        if (lower.contains("setting") || lower.contains("सेटिंग") || lower.contains("सेटिंग्स")) {
            val ok = phoneController.openSettings()
            val latency = System.currentTimeMillis() - startTime
            val reply = "ले चौधरा, फोन की मुख्य सेटिंग खोल दी!"
            return CommandResult(
                message = ChatMessage(
                    sender = MessageSender.TAU,
                    text = reply,
                    actionType = ActionType.SYSTEM_ACTION,
                    actionDetails = "फोन सेटिंग्स",
                    latencyMs = latency
                ),
                spokenHaryanviText = reply,
                isSuccess = ok
            )
        }

        // 8. Dialer / Calls
        if (matchesCall(lower)) {
            val number = extractPhoneNumber(lower)
            val ok = phoneController.openDialer(number)
            val latency = System.currentTimeMillis() - startTime
            val reply = if (number.isNotBlank()) {
                "ले भाई, $number पै कॉल मिलावण खातर डायलर खोल दिया!"
            } else {
                "ले चौधरा, डायलर खोल दिया, जिसको मर्जी फोन मिला!"
            }
            return CommandResult(
                message = ChatMessage(
                    sender = MessageSender.TAU,
                    text = reply,
                    actionType = ActionType.SYSTEM_ACTION,
                    actionDetails = if (number.isNotBlank()) "डायल: $number" else "फोन डायलर",
                    latencyMs = latency
                ),
                spokenHaryanviText = reply,
                isSuccess = ok
            )
        }

        // 9. YouTube query / search
        if (matchesYouTubeSearch(lower)) {
            val query = extractYouTubeQuery(text)
            val ok = phoneController.searchYouTube(query)
            val latency = System.currentTimeMillis() - startTime
            val reply = "यूट्यूब पै '$query' चला दिया लाडले, मजे ले!"
            return CommandResult(
                message = ChatMessage(
                    sender = MessageSender.TAU,
                    text = reply,
                    actionType = ActionType.SYSTEM_ACTION,
                    actionDetails = "यूट्यूब सर्च: $query",
                    latencyMs = latency
                ),
                spokenHaryanviText = reply,
                isSuccess = ok
            )
        }

        // 10. Web search
        if (lower.contains("search") || lower.contains("गूगल") || lower.contains("dhoond") || lower.contains("ढूंढ") || lower.contains("खोज")) {
            val cleanQuery = text.replace(Regex("(search|गूगल|google|पे|पै|ढूंढ|खोज|कर|दिखा)", RegexOption.IGNORE_CASE), "").trim()
            val query = if (cleanQuery.isBlank()) text else cleanQuery
            val ok = phoneController.searchWeb(query)
            val latency = System.currentTimeMillis() - startTime
            val reply = "इंटरनेट पै '$query' ढूंढ दिया भाई!"
            return CommandResult(
                message = ChatMessage(
                    sender = MessageSender.TAU,
                    text = reply,
                    actionType = ActionType.SYSTEM_ACTION,
                    actionDetails = "वेब खोज: $query",
                    latencyMs = latency
                ),
                spokenHaryanviText = reply,
                isSuccess = ok
            )
        }

        // 11. Match installed apps dynamically!
        val installedApps = phoneController.getInstalledApps()
        val matchedApp = findMatchingApp(lower, installedApps)
        if (matchedApp != null) {
            val ok = phoneController.launchApp(matchedApp.packageName)
            val latency = System.currentTimeMillis() - startTime
            val reply = if (ok) {
                "चाल भाई, ${matchedApp.appName} चालू कर दिया तेरे खातर!"
            } else {
                "अरै लाडले, ${matchedApp.appName} चालू करण म्ह दिक्कत आई!"
            }
            return CommandResult(
                message = ChatMessage(
                    sender = MessageSender.TAU,
                    text = reply,
                    actionType = ActionType.APP_LAUNCH,
                    targetApp = matchedApp,
                    actionDetails = "${matchedApp.appName} लॉन्च (${matchedApp.packageName})",
                    latencyMs = latency
                ),
                spokenHaryanviText = reply,
                isSuccess = ok
            )
        }

        // 12. App not found fallback
        val cleanCandidate = cleanAppCandidate(text)
        val latency = System.currentTimeMillis() - startTime
        val reply = if (cleanCandidate.isNotBlank()) {
            "अरै लाडले, यो '$cleanCandidate' ऐप फोन म्ह कोन्या मिल्या! एक बार नाम दोबारा बोल के देख।"
        } else {
            "हुक्म समझ कोन्या आया भाई! बोल 'व्हॉट्सऐप खोल', 'कैमरा चला', 'टॉर्च जला' या 'होम पै जा'।"
        }

        return CommandResult(
            message = ChatMessage(
                sender = MessageSender.TAU,
                text = reply,
                actionType = ActionType.HELP,
                latencyMs = latency
            ),
            spokenHaryanviText = reply,
            isSuccess = false
        )
    }

    private fun matchesGreeting(s: String): Boolean {
        return s.contains("राम राम") || s.contains("ram ram") || s.contains("नमस्ते") ||
                s.contains("namaste") || s.contains("hello") || s.contains("हैलो") ||
                s.contains("जय बाबा की") || s.contains("jai baba ki")
    }

    private fun matchesTorch(s: String): Boolean {
        return s.contains("torch") || s.contains("टॉर्च") || s.contains("बत्ती") ||
                s.contains("flashlight") || s.contains("फ्लैशलाइट") || s.contains("चानणा")
    }

    private fun matchesBattery(s: String): Boolean {
        return s.contains("battery") || s.contains("बैटरी") || s.contains("चार्ज") ||
                s.contains("charge") || s.contains("चार्जिंग")
    }

    private fun matchesHome(s: String): Boolean {
        return s.contains("home") || s.contains("होम") || s.contains("होम स्क्रीन") ||
                (s.contains("बंद कर") && !s.contains("टॉर्च") && !s.contains("बत्ती")) ||
                s.contains("बाहर निकल") || s.contains("bahar nikal") || s.contains("peeche ho")
    }

    private fun matchesRecents(s: String): Boolean {
        return s.contains("recent") || s.contains("रीसेंट") || s.contains("टास्क") ||
                s.contains("सारे ऐप") || s.contains("खुली ऐप")
    }

    private fun matchesCall(s: String): Boolean {
        return s.contains("call") || s.contains("कॉल") || s.contains("फोन मिला") ||
                s.contains("डायल") || s.contains("dial") || s.contains("मिला")
    }

    private fun matchesYouTubeSearch(s: String): Boolean {
        return (s.contains("youtube") || s.contains("यूट्यूब") || s.contains("गाणे") || s.contains("गाने")) &&
                (s.contains("बजा") || s.contains("चला") || s.contains("दिखा") || s.contains("सर्च") || s.contains("search") || s.contains("play"))
    }

    private fun extractPhoneNumber(s: String): String {
        val digits = s.filter { it.isDigit() }
        return if (digits.length >= 3) digits else ""
    }

    private fun extractYouTubeQuery(text: String): String {
        var query = text.replace(Regex("(youtube|यूट्यूब|पे|पै|सर्च|search|कर|गाने|गाना|गीत|बजा|चला|चलाओ|दिखाओ|दिखा|सुणाओ|सुणा|play)", RegexOption.IGNORE_CASE), "").trim()
        return if (query.isBlank()) "haryanvi songs" else query
    }

    private fun cleanAppCandidate(text: String): String {
        return text.replace(Regex("(खोल|खोल दे|चला|चला दे|चालू कर|दिखा|open|launch|start|रै|लाडले|ताऊ|भाई|ऐप|app)", RegexOption.IGNORE_CASE), "").trim()
    }

    private fun findMatchingApp(rawText: String, apps: List<AppInfo>): AppInfo? {
        val cleaned = rawText
            .replace(Regex("(खोल|खोल दे|चला|चला दे|चालू कर|दिखा|open|launch|start|रै|लाडले|ताऊ|भाई|चौधरा|ऐप|app|प्लीज|जरा)", RegexOption.IGNORE_CASE), "")
            .trim()
            .lowercase()

        if (cleaned.isBlank()) return null

        // 1. Direct alias match
        for (app in apps) {
            for (alias in app.aliases) {
                if (cleaned == alias.lowercase() || cleaned.contains(alias.lowercase())) {
                    return app
                }
            }
        }

        // 2. App name exact or containment
        for (app in apps) {
            val appLower = app.appName.lowercase()
            if (cleaned == appLower || appLower.contains(cleaned) || cleaned.contains(appLower)) {
                return app
            }
        }

        // 3. Token fuzzy match (e.g. user said "व्हाट्सएप चालू कर" -> token "व्हाट्सएप" matched in app aliases)
        val tokens = cleaned.split(" ", "-", "_").filter { it.length >= 2 }
        for (token in tokens) {
            for (app in apps) {
                if (app.appName.lowercase().contains(token)) return app
                for (alias in app.aliases) {
                    if (alias.lowercase().contains(token) || token.contains(alias.lowercase())) {
                        return app
                    }
                }
            }
        }

        return null
    }
}
