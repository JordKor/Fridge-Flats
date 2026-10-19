package com.fridgeflats.data

import java.time.LocalDate

/** Rough default shelf lives. The user can always adjust the date when adding. */
enum class Category(val label: String, val defaultDays: Int, val color: Long) {
    LEAFY("Leafy greens", 5, 0xFF6BBF59),
    VEG("Veg", 10, 0xFFB5C94A),
    FRUIT("Fruit", 6, 0xFFF29E4C),
    DAIRY("Dairy", 7, 0xFF8EC5E8),
    MEAT("Meat & fish", 3, 0xFFF08A8A),
    BREAD("Bread", 5, 0xFFD9A86C),
    LEFTOVERS("Leftovers", 3, 0xFFB18CD9),
    OTHER("Other", 7, 0xFFB0B0B0),
}

enum class Personality(val label: String) {
    DRAMATIC("Dramatic"),
    STOIC("Stoic"),
    NEEDY("Needy"),
    SMUG("Smug"),
    ANXIOUS("Anxious"),
}

/** Ordered from calm to dead, so `mood >= Mood.WORRIED` reads naturally. */
enum class Mood { FINE, MILD, WORRIED, CRITICAL, GONE }

fun moodForDays(daysLeft: Long): Mood = when {
    daysLeft < 0 -> Mood.GONE
    daysLeft == 0L -> Mood.CRITICAL
    daysLeft == 1L -> Mood.WORRIED
    daysLeft <= 3 -> Mood.MILD
    else -> Mood.FINE
}

data class Grocery(
    val id: Long,
    val name: String,
    val category: Category,
    val personality: Personality,
    val expiresEpochDay: Long,
    /** Lowest notification stage already sent: 2, 1, 0, or -1 (expired). */
    val lastNotifiedStage: Int = NOT_NOTIFIED,
) {
    val expiryDate: LocalDate get() = LocalDate.ofEpochDay(expiresEpochDay)

    fun daysLeft(today: LocalDate = LocalDate.now()): Long = expiresEpochDay - today.toEpochDay()

    fun mood(today: LocalDate = LocalDate.now()): Mood = moodForDays(daysLeft(today))

    companion object {
        const val NOT_NOTIFIED = Int.MAX_VALUE
    }
}
