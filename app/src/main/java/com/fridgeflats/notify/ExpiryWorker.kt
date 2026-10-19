package com.fridgeflats.notify

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.fridgeflats.data.Grocery
import com.fridgeflats.data.GroceryStore
import com.fridgeflats.data.Lines
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

/** Runs once a day (around 9am) and lets residents near their date speak up. */
class ExpiryWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        checkAndNag(applicationContext)
        return Result.success()
    }

    companion object {
        private const val WORK_NAME = "daily-resident-check"
        private const val NAG_HOUR = 9

        /**
         * Each resident gets at most one message per stage (2 days, 1 day, today,
         * expired), so nobody gets the same guilt trip twice.
         */
        fun checkAndNag(context: Context) {
            val today = LocalDate.now()
            GroceryStore.mutate(context) { items ->
                items.map { g -> nagIfDue(context, g, today) }
            }
        }

        private fun nagIfDue(context: Context, g: Grocery, today: LocalDate): Grocery {
            val d = g.daysLeft(today)
            val stage = when {
                d < 0 -> -1
                d <= 2 -> d.toInt()
                else -> return g
            }
            if (stage >= g.lastNotifiedStage) return g
            val (title, body) = Lines.nag(g.personality, stage, g.name)
            Notifier.send(context, (g.id % Int.MAX_VALUE).toInt(), title, body)
            return g.copy(lastNotifiedStage = stage)
        }

        fun schedule(context: Context) {
            val now = LocalDateTime.now()
            var next = now.toLocalDate().atTime(NAG_HOUR, 0)
            if (!next.isAfter(now)) next = next.plusDays(1)
            val delayMinutes = Duration.between(now, next).toMinutes()

            val request = PeriodicWorkRequestBuilder<ExpiryWorker>(1, TimeUnit.DAYS)
                .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request,
            )
        }
    }
}
