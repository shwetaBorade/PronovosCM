package com.pronovoscm.model.view

import com.pronovoscm.model.response.issueTracking.issues.Assignee
import com.pronovoscm.utils.DateFormatter
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


data class IssueListItem(
    var pjIssueId: Long = 0,
    var pjIssueIdMobile: Long,
    var pjProjectsId: Long,
    var usersId: Long,
    var tenantId: Long,
    var issueNumber: String = "New",
    var title: String = "",
    var dateCreated: String,
    var dateResolved: String? = "",
    var resolvedStatus: Boolean = false,
    var description: String = "",
    var impactsAndRootCause: List<IssueImpactAndRootCause> = emptyList(),
    var issuesBreakdown: List<IssueBreakdown> = emptyList(),
    var createdAt: String? = null,
    var updatedAt: String? = null,
    var deletedAt: String? = null,
    var createdBy: Long,
    var createdByName: String,
    var cacheId: Long?,
    var neededBy: String? = null,
    var assignee: Assignee?,
    var neededByTimeZone: String? = ""
) {
    fun getNeedBy(): String {
        try {
            val byTime = getNeedByTime()
            if (neededBy.isNullOrEmpty()) {
                return ""
            }
            return DateFormatter.getDisplayDate(
                neededBy?.split(" ")?.get(0),
                DateFormatter.SERVICE_DATE_FORMAT,
                DateFormatter.DATE_FORMAT_MMDDYYYY
            ).plus(" ").plus(byTime).plus(" ").plus(neededByTimeZone)
        } catch (ex: Exception) {
            return ""
        }
    }

    fun getNeedByTime(): String {

        val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val inputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        try {
            val date: Date? =
                inputFormat.parse(neededBy?.split(" ")?.get(1) ?: "")
            return outputFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return ""
//        return try {
//
//            neededBy?.split(" ")?.get(1) ?: ""
//        } catch (ex: Exception) {
//            ""
//        }

    }

    private fun convertTo24HourFormat(time: String): String {
        // Input format for AM/PM time
        val inputFormat = SimpleDateFormat("hh:mm a", Locale.US)
        // Output format for 24-hour time
        val outputFormat = SimpleDateFormat("HH:mm:ss", Locale.US)
        try {
            // Parse the input time
            val date: Date = inputFormat.parse(time)
            // Format the date to 24-hour time
            return outputFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        // Return the original time if there's an error
        return time
    }
}

