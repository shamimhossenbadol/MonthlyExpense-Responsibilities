package com.codesphere.monthlyexpense.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.automirrored.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.CardGiftcard
import androidx.compose.material.icons.rounded.Celebration
import androidx.compose.material.icons.rounded.Checkroom
import androidx.compose.material.icons.rounded.ChildCare
import androidx.compose.material.icons.rounded.CleaningServices
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.ElectricBolt
import androidx.compose.material.icons.rounded.Elderly
import androidx.compose.material.icons.rounded.FamilyRestroom
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Flight
import androidx.compose.material.icons.rounded.Handshake
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Laptop
import androidx.compose.material.icons.rounded.LocalCafe
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.LocalGroceryStore
import androidx.compose.material.icons.rounded.LocalHospital
import androidx.compose.material.icons.rounded.LocalLaundryService
import androidx.compose.material.icons.rounded.LocalPharmacy
import androidx.compose.material.icons.rounded.LocalTaxi
import androidx.compose.material.icons.rounded.Mosque
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.Savings
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material.icons.rounded.SportsSoccer
import androidx.compose.material.icons.rounded.Subscriptions
import androidx.compose.material.icons.rounded.Train
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material.icons.rounded.TwoWheeler
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material.icons.rounded.Work
import androidx.compose.ui.graphics.vector.ImageVector
import com.codesphere.monthlyexpense.util.IconGuesser

/**
 * Icons an expense can use, keyed by the string stored in the database.
 * Order = picker order; the first ones are the most common and show without expanding.
 * Never rename a key: stored rows reference it.
 */
object ExpenseIcons {
    const val DEFAULT = "receipt"

    val all: List<Pair<String, ImageVector>> = listOf(
        // Most common
        "home" to Icons.Rounded.Home,
        "family" to Icons.Rounded.FamilyRestroom,
        "person" to Icons.Rounded.Person,
        "bolt" to Icons.Rounded.ElectricBolt,
        "wifi" to Icons.Rounded.Wifi,
        "water" to Icons.Rounded.WaterDrop,
        "gas" to Icons.Rounded.LocalFireDepartment,
        "phone" to Icons.Rounded.PhoneAndroid,
        "shopping" to Icons.Rounded.ShoppingBag,
        "grocery" to Icons.Rounded.LocalGroceryStore,
        "car" to Icons.Rounded.DirectionsCar,
        // Bills & money
        DEFAULT to Icons.AutoMirrored.Rounded.ReceiptLong,
        "card" to Icons.Rounded.CreditCard,
        "bank" to Icons.Rounded.AccountBalance,
        "savings" to Icons.Rounded.Savings,
        "payments" to Icons.Rounded.Payments,
        "tv" to Icons.Rounded.Tv,
        "subscriptions" to Icons.Rounded.Subscriptions,
        "laptop" to Icons.Rounded.Laptop,
        // People
        "child" to Icons.Rounded.ChildCare,
        "elderly" to Icons.Rounded.Elderly,
        "pets" to Icons.Rounded.Pets,
        // Food & daily
        "food" to Icons.Rounded.Restaurant,
        "cafe" to Icons.Rounded.LocalCafe,
        "clothes" to Icons.Rounded.Checkroom,
        "laundry" to Icons.Rounded.LocalLaundryService,
        "cleaning" to Icons.Rounded.CleaningServices,
        "repair" to Icons.Rounded.Build,
        // Transport
        "bike" to Icons.Rounded.TwoWheeler,
        "taxi" to Icons.Rounded.LocalTaxi,
        "train" to Icons.Rounded.Train,
        "flight" to Icons.Rounded.Flight,
        // Health & learning
        "medical" to Icons.Rounded.LocalHospital,
        "pharmacy" to Icons.Rounded.LocalPharmacy,
        "fitness" to Icons.Rounded.FitnessCenter,
        "beauty" to Icons.Rounded.Spa,
        "school" to Icons.Rounded.School,
        "book" to Icons.AutoMirrored.Rounded.MenuBook,
        "work" to Icons.Rounded.Work,
        // Leisure & giving
        "movie" to Icons.Rounded.Movie,
        "music" to Icons.Rounded.MusicNote,
        "sport" to Icons.Rounded.SportsSoccer,
        "celebration" to Icons.Rounded.Celebration,
        "gift" to Icons.Rounded.CardGiftcard,
        "mosque" to Icons.Rounded.Mosque,
        "handshake" to Icons.Rounded.Handshake,
        "heart" to Icons.Rounded.Favorite
    )

    private val byKey = all.toMap()

    fun get(key: String): ImageVector = byKey[key] ?: byKey.getValue(DEFAULT)

    fun guess(name: String): String? = IconGuesser.guess(name)?.takeIf { it in byKey }
}
