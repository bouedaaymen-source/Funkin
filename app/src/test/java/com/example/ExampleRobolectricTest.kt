package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.DefaultCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("FunkinMods", appName)
    }

    @Test
    fun `verify catalog has iconic mods`() {
        val mods = DefaultCatalog.mods
        assertTrue(mods.isNotEmpty())
        assertTrue(mods.any { it.title.contains("Mario's Madness", ignoreCase = true) })
        assertTrue(mods.any { it.title.contains("Indie Cross", ignoreCase = true) })
        assertTrue(mods.any { it.title.contains("Whitty", ignoreCase = true) })
    }
}
