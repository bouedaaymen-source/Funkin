package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.TownRepositoryData
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
        assertEquals("The 2090 Town", appName)
        assertEquals("https://discord.gg/ENa878794", context.getString(R.string.discord_invite_url))
    }

    @Test
    fun `verify town repository has reel and discord telemetry`() {
        assertEquals("https://discord.gg/ENa878794", TownRepositoryData.DISCORD_JOIN_URL)
        assertEquals("The 2090 Club", TownRepositoryData.discordInfo.serverName)
        assertEquals(6, TownRepositoryData.allEpisodes.size)
        assertTrue(TownRepositoryData.allEpisodes.any { it.shortcode == "DdzIfF2gIDD" })
        assertTrue(TownRepositoryData.initialCitizens.any { it.name.contains("Satoru Gojo") })
        assertTrue(TownRepositoryData.initialCitizens.any { it.name.contains("Stone Golem") })
    }
}
