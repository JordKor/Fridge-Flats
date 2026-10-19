@file:OptIn(ExperimentalMaterial3Api::class)

package com.fridgeflats.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fridgeflats.data.Category
import com.fridgeflats.data.Grocery
import com.fridgeflats.data.Lines
import com.fridgeflats.data.Mood
import com.fridgeflats.data.Personality
import com.fridgeflats.data.moodForDays
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val dateFmt = DateTimeFormatter.ofPattern("EEE d MMM", Locale.getDefault())

fun daysLabel(d: Long): String = when {
    d < -1 -> "expired ${-d} days ago"
    d == -1L -> "expired yesterday"
    d == 0L -> "last day!"
    d == 1L -> "1 day left"
    else -> "$d days left"
}

private fun labelColor(mood: Mood): Color = when (mood) {
    Mood.FINE, Mood.MILD -> Muted
    Mood.WORRIED, Mood.CRITICAL -> Danger
    Mood.GONE -> Color(0xFF8A8F98)
}

/* ---------------------------- The building ---------------------------- */

@Composable
fun BuildingScreen(
    residents: List<Grocery>,
    onOpen: (Long) -> Unit,
    onAdd: () -> Unit,
    onTestNag: () -> Unit,
) {
    val today = LocalDate.now()
    val sorted = residents.sortedBy { it.expiresEpochDay }

    Scaffold(
        containerColor = Cream,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAdd,
                containerColor = Coral,
                contentColor = Color.White,
            ) {
                Text("+  Move someone in", fontWeight = FontWeight.Bold)
            }
        },
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize(),
        ) {
            Header(sorted, today, onTestNag)
            if (sorted.isEmpty()) {
                EmptyBuilding(Modifier.weight(1f))
            } else {
                // roof
                Box(
                    Modifier
                        .padding(horizontal = 8.dp)
                        .fillMaxWidth()
                        .height(16.dp)
                        .background(Roof, RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                )
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                        .background(Brick),
                    contentPadding = PaddingValues(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 96.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    itemsIndexed(sorted, key = { _, g -> g.id }) { i, g ->
                        Room(g, roomNumber(i), today) { onOpen(g.id) }
                    }
                }
            }
        }
    }
}

private fun roomNumber(index: Int): String = "${index / 3 + 1}0${index % 3 + 1}"

@Composable
private fun Header(residents: List<Grocery>, today: LocalDate, onTestNag: () -> Unit) {
    val moods = residents.map { it.mood(today) }
    val urgent = moods.count { it == Mood.WORRIED || it == Mood.CRITICAL }
    val ghosts = moods.count { it == Mood.GONE }
    val summary = buildString {
        append(
            when (residents.size) {
                0 -> "No residents"
                1 -> "1 resident"
                else -> "${residents.size} residents"
            },
        )
        if (urgent > 0) append(" · $urgent need you soon")
        if (ghosts > 0) append(" · $ghosts haunting")
    }

    Row(
        Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 8.dp, top = 16.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text("Fridge Flats", fontSize = 30.sp, fontWeight = FontWeight.Black, color = Ink)
            Text(summary, fontSize = 14.sp, color = if (urgent + ghosts > 0) Danger else Muted)
        }
        TextButton(onClick = onTestNag) { Text("Test nag") }
    }
}

@Composable
private fun EmptyBuilding(modifier: Modifier = Modifier) {
    Column(
        modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        ResidentFace(Category.LEAFY, Mood.MILD, Modifier.size(110.dp))
        Spacer(Modifier.height(16.dp))
        Text(
            "The building is empty.",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Ink,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            "Somewhere, a lettuce is waiting at the shop for someone who will actually eat it.",
            textAlign = TextAlign.Center,
            color = Muted,
        )
    }
}

@Composable
private fun Room(g: Grocery, number: String, today: LocalDate, onClick: () -> Unit) {
    val mood = g.mood(today)
    val base = if (mood == Mood.GONE) Color(0xFFB0B5BD) else Color(g.category.color)
    val shape = RoundedCornerShape(14.dp)

    Box(
        Modifier
            .aspectRatio(0.78f)
            .clip(shape)
            .background(lerp(base, Color.White, 0.78f))
            .border(2.dp, lerp(base, Color.Black, 0.2f), shape)
            .clickable(onClick = onClick)
            .padding(6.dp),
    ) {
        Text(number, fontSize = 10.sp, color = Muted, modifier = Modifier.align(Alignment.TopStart))
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            ResidentFace(g.category, mood, Modifier.size(54.dp))
            Text(
                g.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = Ink,
            )
            Text(daysLabel(g.daysLeft(today)), fontSize = 11.sp, color = labelColor(mood), maxLines = 1)
        }
        if (mood >= Mood.WORRIED) {
            ThoughtBubble(
                if (mood == Mood.GONE) "…" else "!",
                Modifier.align(Alignment.TopEnd),
            )
        }
    }
}

@Composable
private fun ThoughtBubble(symbol: String, modifier: Modifier = Modifier) {
    Box(
        modifier
            .size(22.dp)
            .clip(CircleShape)
            .background(Color.White)
            .border(1.5.dp, Ink, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(symbol, fontSize = 12.sp, fontWeight = FontWeight.Black, color = Danger)
    }
}

/* ---------------------------- A resident's room ---------------------------- */

@Composable
fun DetailScreen(
    g: Grocery,
    onBack: () -> Unit,
    onEaten: () -> Unit,
    onTossed: () -> Unit,
) {
    val today = LocalDate.now()
    val mood = g.mood(today)

    Column(
        Modifier
            .fillMaxSize()
            .background(Cream)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TextButton(onClick = onBack, modifier = Modifier.align(Alignment.Start)) {
            Text("← Back to the building")
        }
        Spacer(Modifier.height(12.dp))
        SpeechBubble(Lines.bubble(g.personality, mood))
        Spacer(Modifier.height(8.dp))
        ResidentFace(g.category, mood, Modifier.size(170.dp))
        Text(g.name, fontSize = 30.sp, fontWeight = FontWeight.Black, color = Ink)
        Text("${g.personality.label} · ${g.category.label}", color = Muted)
        Spacer(Modifier.height(10.dp))
        Text(
            "Expires ${g.expiryDate.format(dateFmt)} (${daysLabel(g.daysLeft(today))})",
            fontSize = 16.sp,
            color = labelColor(mood),
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(28.dp))
        Button(
            onClick = onEaten,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Coral),
        ) {
            Text("I ate it", fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = onTossed, modifier = Modifier.fillMaxWidth()) {
            Text("I threw it out", color = Ink)
        }
    }
}

@Composable
private fun SpeechBubble(line: String) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        border = BorderStroke(2.dp, Ink),
        shadowElevation = 2.dp,
    ) {
        Text(
            "“$line”",
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
            fontSize = 17.sp,
            textAlign = TextAlign.Center,
            color = Ink,
        )
    }
}

/* ---------------------------- Moving someone in ---------------------------- */

@Composable
fun AddScreen(onCancel: () -> Unit, onSave: (Grocery) -> Unit) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(Category.LEAFY) }
    var days by remember { mutableIntStateOf(Category.LEAFY.defaultDays) }
    var personality by remember { mutableStateOf(Personality.entries.random()) }
    val today = LocalDate.now()

    Column(
        Modifier
            .fillMaxSize()
            .background(Cream)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
    ) {
        TextButton(onClick = onCancel) { Text("← Cancel") }
        Text("New resident", fontSize = 28.sp, fontWeight = FontWeight.Black, color = Ink)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it.take(24) },
            label = { Text("What is it?") },
            placeholder = { Text("Spinach") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(20.dp))
        Text("Type", fontWeight = FontWeight.SemiBold, color = Ink)
        Spacer(Modifier.height(6.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(Category.entries) { c ->
                FilterChip(
                    selected = c == category,
                    onClick = {
                        category = c
                        days = c.defaultDays
                    },
                    label = { Text(c.label) },
                )
            }
        }

        Spacer(Modifier.height(20.dp))
        Text("Expires", fontWeight = FontWeight.SemiBold, color = Ink)
        Spacer(Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedButton(onClick = { if (days > 0) days-- }) { Text("−", fontSize = 20.sp) }
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    when (days) {
                        0 -> "today"
                        1 -> "tomorrow"
                        else -> "in $days days"
                    },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Ink,
                )
                Text(today.plusDays(days.toLong()).format(dateFmt), color = Muted)
            }
            OutlinedButton(onClick = { if (days < 365) days++ }) { Text("+", fontSize = 20.sp) }
        }
        Text(
            "Check the date on the pack. The default is a rough guess for ${category.label.lowercase()}.",
            fontSize = 12.sp,
            color = Muted,
            modifier = Modifier.padding(top = 6.dp),
        )

        Spacer(Modifier.height(20.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            ResidentFace(category, moodForDays(days.toLong()), Modifier.size(68.dp))
            Column(
                Modifier
                    .weight(1f)
                    .padding(horizontal = 10.dp),
            ) {
                Text("They seem…", fontSize = 12.sp, color = Muted)
                Text(personality.label, fontWeight = FontWeight.Bold, color = Ink)
                Text(Lines.intro(personality), fontSize = 13.sp, color = Muted)
            }
            TextButton(onClick = { personality = Personality.entries.random() }) { Text("Reroll") }
        }

        Spacer(Modifier.height(28.dp))
        Button(
            enabled = name.isNotBlank(),
            onClick = {
                onSave(
                    Grocery(
                        id = System.currentTimeMillis(),
                        name = name.trim().replaceFirstChar { it.uppercase() },
                        category = category,
                        personality = personality,
                        expiresEpochDay = today.plusDays(days.toLong()).toEpochDay(),
                    ),
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Coral),
        ) {
            Text("Move them in", fontWeight = FontWeight.Bold)
        }
    }
}
