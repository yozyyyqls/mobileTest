package com.yozyyy.mobiletest

import android.content.Context
import android.content.res.AssetManager
import com.yozyyy.mobiletest.service.MockBookingService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.IOException

@ExperimentalCoroutinesApi
class MockBookingServiceTest {
    // Mock dependencies
    private lateinit var mockContext: Context
    private lateinit var mockAssetManager: AssetManager
    private lateinit var bookingService: MockBookingService

    @Before
    fun setup() {
        // Set up mocks
        mockAssetManager = mockk<AssetManager>()
        mockContext = mockk<Context>().apply {
            every { assets } returns mockAssetManager
        }

        bookingService = MockBookingService(mockContext)
    }

    @Test
    fun fetchBookingData_should_return_success_result_when_json_is_valid() = runTest {
        // Arrange
        val inputStream = ByteArrayInputStream(validBookingJson.toByteArray())
        every { mockAssetManager.open("booking.json") } returns inputStream

        // Act
        val result = bookingService.fetchBookingData()

        // Assert
        assertTrue(result.isSuccess)
        val bookingList = result.getOrNull()
        assertEquals(1, bookingList?.bookings?.size)

        // Verify that assets.open was called
        verify { mockAssetManager.open("booking.json") }
    }

    @Test
    fun fetchBookingData_should_return_failure_result_when_file_con_not_open() = runTest {
        every { mockAssetManager.open("booking.json") } throws IOException("File not found")
        val result = bookingService.fetchBookingData()

        assertTrue(result.isFailure)
        assertEquals("File not found", result.exceptionOrNull()?.message)
    }

    @Test
    fun fetchBookingData_should_return_failure_result_when_json_invalid() = runTest {
        val inputStream = ByteArrayInputStream(invalidBookingJson.toByteArray())
        every { mockAssetManager.open("booking.json") } returns inputStream
        val result = bookingService.fetchBookingData()

        assertTrue(result.isFailure)
        assertTrue(result.getOrNull()?.bookings == null)
        assertTrue(result.exceptionOrNull() is Exception)
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
    private val invalidBookingJson = "[ invalid json ]"
}