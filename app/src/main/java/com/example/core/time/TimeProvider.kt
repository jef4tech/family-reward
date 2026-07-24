package com.example.core.time

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

interface TimeProvider {
    fun currentTimeMillis(): Long
    fun formatDate(timestamp: Long): String
    fun formatTime(timestamp: Long): String
    fun formatRelativeTime(timestamp: Long): String
}

class SystemTimeProvider : TimeProvider {
    override fun currentTimeMillis(): Long = System.currentTimeMillis()

    override fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    override fun formatTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    override fun formatRelativeTime(timestamp: Long): String {
        val diff = currentTimeMillis() - timestamp
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        return when {
            days > 0 -> "${days}d ago"
            hours > 0 -> "${hours}h ago"
            minutes > 0 -> "${minutes}m ago"
            else -> "Just now"
        }
    }
}
