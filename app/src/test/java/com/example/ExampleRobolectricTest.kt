package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.DjezzyOusimRepository
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
        assertEquals("OUSIM Bot Djezzy", appName)
        assertEquals("Youcef Ouagead", context.getString(R.string.creator_name))
    }

    @Test
    fun `verify djezzy ousim repository offers and creator`() {
        assertEquals("Youcef Ouagead", DjezzyOusimRepository.CREATOR_NAME)
        assertTrue(DjezzyOusimRepository.allOffers.isNotEmpty())
        assertTrue(DjezzyOusimRepository.allOffers.any { it.id == "ousim_2gb_welcome" })
        assertTrue(DjezzyOusimRepository.allOffers.any { it.id == "hayla_bezzef_1500" })
        assertTrue(DjezzyOusimRepository.ussdCodes.any { it.code == "*710#" })
    }
}
