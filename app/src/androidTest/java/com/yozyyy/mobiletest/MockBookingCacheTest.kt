package com.yozyyy.mobiletest

import android.content.Context
import android.content.SharedPreferences
import com.yozyyy.mobiletest.cache.MockBookingCache
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MockBookingCacheTest {
    private lateinit var context: Context
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private lateinit var mockBookingCache: MockBookingCache

    @Before
    fun setup() {
        sharedPreferences = mockk<SharedPreferences>()
        context = mockk<Context>().apply {
            every { getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) } returns sharedPreferences
        }

        mockBookingCache = MockBookingCache(context)
    }

    @Test
    fun getBooking_should_return_success_result() = runTest {
        every { sharedPreferences.getString(KEY_BOOKING, null) } returns validBookingJson
        val bookingList = mockBookingCache.getBooking()

        assertTrue(bookingList != null)
        assertTrue(bookingList?.bookings != null)
    }

    @Test
    fun getBooking_should_return_null() = runTest {
        every { sharedPreferences.getString(KEY_BOOKING, null) } returns null
        val bookingList = mockBookingCache.getBooking()

        assertTrue(bookingList == null)
    }

    private val validBookingJson = "{\n" +
            "  \"bookings\": [\n" +
            "    {\n" +
            "      \"shipReference\": \"ABCDEF\",\n" +
            "      \"shipToken\": \"AAAABBBCCCCDDD\",\n" +
            "      \"canIssueTicketChecking\": false,\n" +
            "      \"expiryTime\": \"1722409261\",\n" +
            "      \"duration\": 2430,\n" +
            "      \"segments\": [\n" +
            "        {\n" +
            "          \"id\": 1,\n" +
            "          \"originAndDestinationPair\": {\n" +
            "            \"destination\": {\n" +
            "              \"code\": \"BBB\",\n" +
            "              \"displayName\": \"BBB DisplayName\",\n" +
            "              \"url\": \"www.ship.com\"\n" +
            "            },\n" +
            "            \"destinationCity\": \"AAA\",\n" +
            "            \"origin\": {\n" +
            "              \"code\": \"AAA\",\n" +
            "              \"displayName\": \"AAA DisplayName\",\n" +
            "              \"url\": \"www.ship.com\"\n" +
            "            },\n" +
            "            \"originCity\": \"BBB\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": 2,\n" +
            "          \"originAndDestinationPair\": {\n" +
            "            \"destination\": {\n" +
            "              \"code\": \"CCC\",\n" +
            "              \"displayName\": \"CCC DisplayName\",\n" +
            "              \"url\": \"www.ship.com\"\n" +
            "            },\n" +
            "            \"destinationCity\": \"CCC\",\n" +
            "            \"origin\": {\n" +
            "              \"code\": \"BBB\",\n" +
            "              \"displayName\": \"BBB DisplayName\",\n" +
            "              \"url\": \"www.ship.com\"\n" +
            "            },\n" +
            "            \"originCity\": \"BBB\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": 3,\n" +
            "          \"originAndDestinationPair\": {\n" +
            "            \"destination\": {\n" +
            "              \"code\": \"DDD\",\n" +
            "              \"displayName\": \"DDD DisplayName\",\n" +
            "              \"url\": \"www.ship.com\"\n" +
            "            },\n" +
            "            \"destinationCity\": \"DDD\",\n" +
            "            \"origin\": {\n" +
            "              \"code\": \"CCC\",\n" +
            "              \"displayName\": \"CCC DisplayName\",\n" +
            "              \"url\": \"www.ship.com\"\n" +
            "            },\n" +
            "            \"originCity\": \"CCC\"\n" +
            "          }\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  ]\n" +
            "}"

    companion object {
        private const val PREFS_NAME = "booking_cache"
        private const val KEY_BOOKING = "booking"
        private const val KEY_LAST_UPDATED = "last_updated"
    }
}