package com.friday.assistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.friday.assistant.ai.AiBrain
import com.friday.assistant.voice.VoiceManager

class MainActivity : ComponentActivity() {

    private lateinit var voiceManager: VoiceManager
    private lateinit var aiBrain: AiBrain

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        aiBrain = AiBrain()
        voiceManager = VoiceManager(this) { userText ->
            aiBrain.ask(userText) { reply ->
                voiceManager.speak(reply)
            }
        }

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FridayScreen()
                }
            }
        }
    }

    @Composable
    fun FridayScreen() {
        var status by remember { mutableStateOf("Tap to speak") }
        var reply by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Friday",
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Hello Boss, I am Friday")
            Spacer(modifier = Modifier.height(32.dp))
            Text(text = status)
            Spacer(modifier = Modifier.height(16.dp))
            if (reply.isNotEmpty()) {
                Text(text = reply)
                Spacer(modifier = Modifier.height(16.dp))
            }
            Button(
                onClick = {
                    status = "Listening..."
                    voiceManager.startListening()
                }
            ) {
                Text("Tap to speak")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        voiceManager.destroy()
    }
}
