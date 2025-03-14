package com.yozyyy.mobiletest.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Booking(
    @PrimaryKey val shipReference: String,
    @ColumnInfo(name = "ship_token") val shipToken: String,
    @ColumnInfo(name = "can_issue_ticket_checking") val canIssueTicketChecking: Boolean,
    @ColumnInfo(name = "expiry_time") val expiryTime: Long,
    val duration: Int,
    val segments: List<Segment>
)
