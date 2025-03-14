package com.yozyyy.mobiletest.manager

import android.content.Context
import android.util.Log
import com.yozyyy.mobiletest.cache.BookingCache
import com.yozyyy.mobiletest.cache.MockBookingDbCache
import com.yozyyy.mobiletest.models.BookingList
import com.yozyyy.mobiletest.service.BookingService
import com.yozyyy.mobiletest.service.MockBookingService
import java.util.Date

class BookingDataProvider(
    private val context: Context,
    private val bookingCache: BookingCache = MockBookingDbCache(context),
    private val bookingService: BookingService = MockBookingService(context),
    private val errorHandler: ErrorHandler = ErrorHandler(),
) {

    suspend fun fetch(): BookingState {
        // fetch data from cache
        val cacheBookingList = fetchFromCache()
        return if (cacheBookingList != null) {
            // cache is valid
            BookingState.Success(cacheBookingList)
        } else {
            // cache is invalid, fetch data from network
            fetchFromNetwork()
        }
    }

    suspend fun refresh(): BookingState {
        val state = fetchFromNetwork()
        if (state is BookingState.Success) {
            return state
        } else {
            val cache = bookingCache.getBooking()
            val errorState = state as BookingState.Error
            return BookingState.Error(errorState.errMsg, cache)
        }
    }

    private suspend fun fetchFromCache(): BookingList? {
        val cacheBookingList = bookingCache.getBooking()
        val lastUpdateTime = bookingCache.getLastUpdateTime()
        val isCacheValid = (cacheBookingList != null) && (System.currentTimeMillis() - lastUpdateTime < CACHE_EXPIRATION_TIME)
        if (isCacheValid) {
            // if cache is valid, return cache data
            printBookingDetails(cacheBookingList, "Cache")
            return cacheBookingList
        }
        return null
    }

    private suspend fun fetchFromNetwork(): BookingState {
        val result = bookingService.fetchBookingData()
        val bookingList = result.getOrNull()
        return if (result.isSuccess && bookingList != null) {
            printBookingDetails(bookingList, "Network")
            bookingCache.saveBooking(bookingList)
            BookingState.Success(bookingList)
        } else {
            val errMsg = errorHandler.handleException(result.exceptionOrNull() ?: Exception("Unknown error"))
            BookingState.Error(errMsg, null)
        }
    }

    private fun printBookingDetails(bookingList: BookingList?, from: String) {
        bookingList?.bookings?.forEach {
            Log.d(TAG, "========= Booking Details(from $from) =========")
            Log.d(TAG, "Ship Reference: ${it.shipReference}")
            Log.d(TAG, "Ship Token: ${it.shipToken}")
            Log.d(TAG, "Can Issue Ticket: ${it.canIssueTicketChecking}")
            Log.d(TAG, "Expiry Time: ${Date(it.expiryTime * 1000)}")
            Log.d(TAG, "Duration: ${it.duration}")
            Log.d(TAG, "Number of Segments: ${it.segments.size}")

            it.segments.forEachIndexed { index, segment ->
                Log.d(TAG, "  Segment #${index + 1} (ID: ${segment.id})")
                Log.d(TAG, "    Origin: ${segment.originAndDestinationPair.origin.code} - ${segment.originAndDestinationPair.origin.displayName}")
                Log.d(TAG, "    Destination: ${segment.originAndDestinationPair.destination.code} - ${segment.originAndDestinationPair.destination.displayName}")
            }
            Log.d(TAG, "=================================================")
        }
    }

    companion object {
        private const val TAG: String = "BookingDataProvider"
        private const val CACHE_EXPIRATION_TIME = 1 * 60 * 1000L // 1 minute
    }
}