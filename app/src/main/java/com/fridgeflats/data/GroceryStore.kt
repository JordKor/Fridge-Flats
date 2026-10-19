package com.fridgeflats.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/**
 * Residents live in SharedPreferences as a JSON array. Every change is
 * load → modify → save under one lock, so the background worker and the
 * UI never overwrite each other's updates.
 */
object GroceryStore {
    private const val PREFS = "fridge_flats"
    private const val KEY = "residents"

    @Synchronized
    fun load(context: Context): List<Grocery> = read(context)

    @Synchronized
    fun add(context: Context, grocery: Grocery): List<Grocery> = mutate(context) { it + grocery }

    @Synchronized
    fun remove(context: Context, id: Long): List<Grocery> =
        mutate(context) { list -> list.filterNot { it.id == id } }

    @Synchronized
    fun mutate(context: Context, block: (List<Grocery>) -> List<Grocery>): List<Grocery> {
        val next = block(read(context))
        write(context, next)
        return next
    }

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    private fun read(context: Context): List<Grocery> {
        val raw = prefs(context).getString(KEY, null) ?: return emptyList()
        return try {
            val arr = JSONArray(raw)
            (0 until arr.length()).mapNotNull { i -> parse(arr.getJSONObject(i)) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun write(context: Context, items: List<Grocery>) {
        val arr = JSONArray()
        items.forEach { arr.put(toJson(it)) }
        prefs(context).edit().putString(KEY, arr.toString()).commit()
    }

    private fun toJson(g: Grocery) = JSONObject().apply {
        put("id", g.id)
        put("name", g.name)
        put("category", g.category.name)
        put("personality", g.personality.name)
        put("expires", g.expiresEpochDay)
        put("notified", g.lastNotifiedStage)
    }

    private fun parse(o: JSONObject): Grocery? = try {
        Grocery(
            id = o.getLong("id"),
            name = o.getString("name"),
            category = Category.valueOf(o.getString("category")),
            personality = Personality.valueOf(o.getString("personality")),
            expiresEpochDay = o.getLong("expires"),
            lastNotifiedStage = o.optInt("notified", Grocery.NOT_NOTIFIED),
        )
    } catch (e: Exception) {
        null
    }
}
