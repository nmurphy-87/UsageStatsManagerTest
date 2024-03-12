package com.niallmurph.usagestatsmanagertest

import android.app.AppOpsManager
import android.app.AppOpsManager.MODE_ALLOWED
import android.app.AppOpsManager.OPSTR_GET_USAGE_STATS
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.niallmurph.usagestatsmanagertest.utils.*
import java.util.*

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val areUsageStatsEnabled = remember {
                mutableStateOf(false)
            }

            if (checkUsageStatsPermission()) {
                areUsageStatsEnabled.value = true
            } else {
                areUsageStatsEnabled.value = false
            }

            TestScreen(usagePermissionsEnabled = areUsageStatsEnabled) {
                startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
            }


        }
    }

    override fun onResume() {
        super.onResume()


    }

    private fun checkUsageStatsPermission(): Boolean {
        var appOpsManager: AppOpsManager? = null
        var mode: Int = 0
        appOpsManager = getSystemService(Context.APP_OPS_SERVICE)!! as AppOpsManager
        mode = appOpsManager.checkOpNoThrow(
            OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            packageName
        )
        return mode == MODE_ALLOWED
    }
}

@Composable
fun TestScreen(
    usagePermissionsEnabled: MutableState<Boolean>,
    enableUsageStats: () -> Unit
) {

    val context = LocalContext.current

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = Color.LightGray
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            if (usagePermissionsEnabled.value) {
                ShowUsageStats(context = context)
            } else {
                Button(onClick = { enableUsageStats.invoke() }) {
                    Text("Enable Usage Permissions")
                }
            }

        }
    }

}

@Composable
fun ShowUsageStats(context: Context){

//    var usageStatsManager : UsageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
//    var cal : Calendar = Calendar.getInstance()
//    cal.add(Calendar.DAY_OF_MONTH, -1)
//    var queryUsageStats : List<UsageStats> = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, cal.timeInMillis, System.currentTimeMillis())
//    var statsData : String = ""

    val totalScreenTimeForCurrentDay = ScreenTimeCalculator(context = context).getScreenTimeForCurrentDay()
    val totalScreenTimeForPreviousWeek = ScreenTimeCalculator(context = context).getScreenTimeForPreviousWeek()
    val totalScreenTimeForPreviousMonth = ScreenTimeCalculator(context = context).getScreenTimeForPreviousMonth()
    val totalScreenTimeBreakdownForPreviousWeek = ScreenTimeCalculator(context = context).getScreenTimeBreakdownForPreviousWeek()

    Text("Total for Current Day : ${formatScreenTime(totalScreenTimeForCurrentDay)}")
    Text("Total for Previous Week : ${formatScreenTime(totalScreenTimeForPreviousWeek)}")
    Text("Total for Previous Month : ${formatScreenTime(totalScreenTimeForPreviousMonth)}")
    Divider(modifier = Modifier.padding(vertical = 8.dp, horizontal = 2.dp), thickness = 2.dp)
    totalScreenTimeBreakdownForPreviousWeek.forEach {
        Text("Day : ${convertLongToDay(it.timestamp)} - Total Time : ${formatScreenTime(it.totalScreenTime)}")
    }


}

