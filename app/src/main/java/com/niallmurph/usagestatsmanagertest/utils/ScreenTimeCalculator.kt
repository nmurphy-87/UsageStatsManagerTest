package com.niallmurph.usagestatsmanagertest.utils

import android.app.usage.UsageStatsManager
import android.content.Context
import com.niallmurph.usagestatsmanagertest.models.ScreenTimeBreakdown
import java.util.*

class ScreenTimeCalculator(private val context : Context) {

    private val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    private val calendar: Calendar = Calendar.getInstance()

    private fun getTotalScreenTime(intervalType : Int, startTime: Long, endTime : Long) : Long {
//        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val queryUsageStats = usageStatsManager.queryUsageStats(intervalType, startTime, endTime)

        var totalScreenTime = 0L

        for (usageStats in queryUsageStats) {
            totalScreenTime += usageStats.totalTimeInForeground
        }

        return totalScreenTime
    }

    fun getScreenTimeForCurrentDay(): Long {
//        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val startTime = calendar.timeInMillis
        val endTime = System.currentTimeMillis()

        return getTotalScreenTime(UsageStatsManager.INTERVAL_DAILY, startTime, endTime)
    }

    fun getScreenTimeForPreviousWeek(): Long {
        val breakdownList = mutableListOf<ScreenTimeBreakdown>()

        for (i in 0 until 7) { // Previous 7 days
            val endTime = calendar.timeInMillis
            calendar.add(Calendar.DAY_OF_YEAR, -1)
            val startTime = calendar.timeInMillis

            val totalScreenTime = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                startTime,
                endTime
            ).sumBy { it.totalTimeInForeground.toInt() }

            breakdownList.add(ScreenTimeBreakdown(endTime, totalScreenTime.toLong()))
        }

        return breakdownList.sumBy { it.totalScreenTime.toInt() }.toLong()
    }

    fun getScreenTimeForPreviousMonth(): Long {
        val breakdownList = mutableListOf<ScreenTimeBreakdown>()

        for (i in 0 until calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
            val endTime = calendar.timeInMillis
            calendar.add(Calendar.DAY_OF_MONTH, -1)
            val startTime = calendar.timeInMillis

            val totalScreenTime = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                startTime,
                endTime
            ).sumBy { it.totalTimeInForeground.toInt() }

            breakdownList.add(ScreenTimeBreakdown(endTime, totalScreenTime.toLong()))
        }

        return breakdownList.sumBy { it.totalScreenTime.toInt() }.toLong()
    }

    fun getScreenTimeBreakdownForPreviousWeek(): List<ScreenTimeBreakdown> {
        val calendar = Calendar.getInstance()
        val breakdownList = mutableListOf<ScreenTimeBreakdown>()

        for (i in 0 until 7) { // Previous 7 days
            val endTime = calendar.timeInMillis
            calendar.add(Calendar.DAY_OF_YEAR, -1)
            val startTime = calendar.timeInMillis

            val totalScreenTime = getTotalScreenTime(UsageStatsManager.INTERVAL_DAILY, startTime, endTime)

            breakdownList.add(ScreenTimeBreakdown(endTime, totalScreenTime))
        }

        return breakdownList.reversed()
    }

}