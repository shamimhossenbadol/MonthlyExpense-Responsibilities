package com.codesphere.monthlyexpense.util

/**
 * Guesses an icon key from an expense title. A keyword matches the START of a word, so
 * "Parents' cash" doesn't hit "rent" and "Credit card" doesn't hit "car". First rule wins.
 */
object IconGuesser {
    private val rules: List<Pair<List<String>, String>> = listOf(
        listOf("parent", "family", "mother", "father", "mom", "dad", "abba", "amma") to "family",
        listOf("sister", "brother", "wife", "husband", "son", "daughter") to "person",
        listOf("baby", "child", "kid", "daycare") to "child",
        listOf("elder", "grand") to "elderly",
        listOf("electric", "power", "current", "desco", "nesco") to "bolt",
        listOf("netflix", "spotify", "youtube", "subscription", "prime") to "subscriptions",
        listOf("internet", "wifi", "broadband") to "wifi",
        listOf("water", "wasa") to "water",
        listOf("gas", "titas", "cylinder") to "gas",
        listOf("mobile", "phone", "recharge", "sim") to "phone",
        listOf("dish", "cable", "tv") to "tv",
        listOf("loan", "emi", "credit", "card", "installment") to "card",
        listOf("bank") to "bank",
        listOf("saving", "dps", "deposit", "fdr") to "savings",
        listOf("salary", "maid", "driver", "helper", "staff") to "payments",
        listOf("rent", "house", "home", "flat", "apartment") to "home",
        listOf("grocer", "bazar", "bazaar", "market") to "grocery",
        listOf("food", "meal", "lunch", "dinner", "restaurant") to "food",
        listOf("coffee", "tea", "cafe", "snack") to "cafe",
        listOf("personal", "shopping", "shop") to "shopping",
        listOf("cloth", "dress", "fashion") to "clothes",
        listOf("bike", "motorcycle") to "bike",
        listOf("taxi", "uber", "pathao", "rickshaw", "cng", "ride") to "taxi",
        listOf("bus", "train", "metro") to "train",
        listOf("car", "fuel", "petrol", "octane", "parking", "transport", "commute") to "car",
        listOf("flight", "travel", "trip", "tour", "vacation") to "flight",
        listOf("school", "tuition", "education", "course", "college", "university", "fee") to "school",
        listOf("book") to "book",
        listOf("pharma", "medicine", "drug") to "pharmacy",
        listOf("medic", "doctor", "health", "hospital", "clinic", "insurance") to "medical",
        listOf("gym", "fitness") to "fitness",
        listOf("sport", "football", "cricket") to "sport",
        listOf("salon", "beauty", "spa", "parlour", "parlor", "haircut") to "beauty",
        listOf("laundry", "wash") to "laundry",
        listOf("clean") to "cleaning",
        listOf("repair", "maintenance", "service", "fix") to "repair",
        listOf("laptop", "computer", "software", "cloud", "domain", "hosting") to "laptop",
        listOf("office", "work", "business") to "work",
        listOf("pet", "cat", "dog") to "pets",
        listOf("movie", "cinema", "entertain") to "movie",
        listOf("music", "concert") to "music",
        listOf("mosque", "zakat", "sadaqah", "masjid") to "mosque",
        listOf("charity", "donation", "help") to "handshake",
        listOf("gift", "present") to "gift",
        listOf("party", "wedding", "eid", "festival", "birthday", "celebration") to "celebration"
    )

    fun guess(title: String): String? {
        val words = title.lowercase().split(Regex("[^\\p{L}\\p{N}]+")).filter { it.isNotEmpty() }
        return rules.firstOrNull { (keywords, _) ->
            keywords.any { k -> words.any { it.startsWith(k) } }
        }?.second
    }
}
