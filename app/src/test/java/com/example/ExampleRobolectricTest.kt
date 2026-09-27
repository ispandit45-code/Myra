package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.controller.PhoneController
import com.example.engine.HaryanviFastEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Tau AI", appName)
    }

    @Test
    fun `fast engine responds to haryanvi greeting under 500ms`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val phoneController = PhoneController(context)
        val fastEngine = HaryanviFastEngine(phoneController)

        val result = fastEngine.executeCommand("राम राम भाई")
        assertNotNull(result)
        assertTrue(result.isSuccess)
        assertTrue(result.message.latencyMs < 500)
    }

    @Test
    fun `fast engine responds to battery query`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val phoneController = PhoneController(context)
        val fastEngine = HaryanviFastEngine(phoneController)

        val result = fastEngine.executeCommand("बैटरी कितनी सै?")
        assertNotNull(result)
        assertTrue(result.message.latencyMs < 500)
    }
}
