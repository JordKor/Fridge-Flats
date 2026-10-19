package com.fridgeflats.data

/**
 * Everything the residents say. Tone: medium-mean. Disappointed, never cruel.
 * Notification stages: 2 = two days left, 1 = tomorrow, 0 = today, -1 = expired.
 */
object Lines {

    fun intro(p: Personality): String = when (p) {
        Personality.DRAMATIC -> "Treats every day as their final performance."
        Personality.STOIC -> "Will never tell you they're struggling."
        Personality.NEEDY -> "Just wants to be chosen. By you. For dinner."
        Personality.SMUG -> "Knows they're the most expensive thing on the shelf."
        Personality.ANXIOUS -> "Has been checking their own date since the shop."
    }

    /** What they say when you tap into their room. */
    fun bubble(p: Personality, mood: Mood): String = when (p) {
        Personality.DRAMATIC -> when (mood) {
            Mood.FINE -> "I am FRESH. Look upon me."
            Mood.MILD -> "I sense a wilting. In my soul."
            Mood.WORRIED -> "Tomorrow I perish. Tell my story."
            Mood.CRITICAL -> "This is my final act. Will you even watch?"
            Mood.GONE -> "I haunt this shelf now. Forever."
        }
        Personality.STOIC -> when (mood) {
            Mood.FINE -> "I'm fine."
            Mood.MILD -> "It's fine."
            Mood.WORRIED -> "No, really. It's fine."
            Mood.CRITICAL -> "I've accepted it. Have you?"
            Mood.GONE -> "..."
        }
        Personality.NEEDY -> when (mood) {
            Mood.FINE -> "Do you think you'll eat me soon? No pressure!"
            Mood.MILD -> "You looked at the yogurt today. Not me. The yogurt."
            Mood.WORRIED -> "Remember when you bought me? You seemed so excited."
            Mood.CRITICAL -> "I waited by the crisper drawer all day."
            Mood.GONE -> "It's okay. You were busy. You're always busy."
        }
        Personality.SMUG -> when (mood) {
            Mood.FINE -> "Cooked properly, I'm the best thing in here."
            Mood.MILD -> "I'd hurry, if I were you. I'm not, obviously."
            Mood.WORRIED -> "Shame to waste quality like me. Your call."
            Mood.CRITICAL -> "Last chance to make one good decision this week."
            Mood.GONE -> "Well. That was predictable."
        }
        Personality.ANXIOUS -> when (mood) {
            Mood.FINE -> "Is that a brown spot? Is that a brown spot??"
            Mood.MILD -> "I counted the days. Twice. Then again."
            Mood.WORRIED -> "Okay okay okay. Tomorrow. Okay."
            Mood.CRITICAL -> "EVERYTHING IS FINE. PLEASE EAT ME."
            Mood.GONE -> "It happened. I knew it would happen."
        }
    }

    /** Push notification text: title to body. */
    fun nag(p: Personality, stage: Int, name: String): Pair<String, String> {
        val body = when (p) {
            Personality.DRAMATIC -> when (stage) {
                2 -> "$name has begun writing their memoirs. 2 days left."
                1 -> "$name is rehearsing a farewell speech. You have until tomorrow."
                0 -> "$name: \"Today I perish, and you are scrolling.\""
                else -> "$name has died. The fridge is in mourning. You're not invited to the funeral."
            }
            Personality.STOIC -> when (stage) {
                2 -> "$name has 2 days left. They won't mention it, so we are."
                1 -> "$name says it's fine. It is not fine. 1 day left."
                0 -> "$name expires today. They're not angry. Just quiet."
                else -> "$name has expired without complaint. That's worse, isn't it?"
            }
            Personality.NEEDY -> when (stage) {
                2 -> "$name saw you open the fridge and close it again. 2 days left."
                1 -> "$name is practising \"I'm not mad\" in the mirror. Tomorrow's the last day."
                0 -> "$name is still holding out hope. Today. Only today."
                else -> "$name waited for you. You know how that ended."
            }
            Personality.SMUG -> when (stage) {
                2 -> "$name would like to remind you they cost money. 2 days left."
                1 -> "$name has bet the milk you'll forget. Prove them wrong by tomorrow."
                0 -> "$name expires today. They're already drafting the \"I told you so\"."
                else -> "$name has expired. They'd like you to know they called it."
            }
            Personality.ANXIOUS -> when (stage) {
                2 -> "$name has started a countdown. It's at 2 days. They're breathing into a bag."
                1 -> "$name is spiralling. 1 day left. Please."
                0 -> "$name expires TODAY and has not stopped vibrating."
                else -> "$name has expired. They predicted this, which somehow didn't help."
            }
        }
        val title = when (stage) {
            2 -> "A note from $name"
            1 -> "$name needs to talk"
            0 -> "$name: last day"
            else -> "Fridge Flats incident report"
        }
        return title to body
    }

    fun farewell(eaten: Boolean, name: String): String {
        val options = if (eaten) listOf(
            "$name has been eaten. They died doing what they loved: being food.",
            "$name fulfilled their purpose. The other residents are inspired. Mildly.",
            "$name moved out the good way. Into you.",
        ) else listOf(
            "$name has been moved to the bin. The other residents watched.",
            "$name is gone. The yogurt says nothing, but it's clearly rattled.",
            "$name's room is available. Nobody wants to talk about what happened.",
        )
        return options.random()
    }
}
