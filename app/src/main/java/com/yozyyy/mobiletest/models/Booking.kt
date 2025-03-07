package com.yozyyy.mobiletest.models

data class Booking(
    val shipReference: String,
    val shipToken: String,
    val canIssueTicketChecking: Boolean,
    val expiryTime: Long,
    val duration: Int,
    val segments: List<Segment>
)
