package com.codesphere.monthlyexpense

import com.codesphere.monthlyexpense.util.IconGuesser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class IconGuesserTest {
    @Test
    fun defaultExpensesGetTheRightIcons() {
        assertEquals("home", IconGuesser.guess("House Rent"))
        assertEquals("family", IconGuesser.guess("Parents' Hand Cash"))
        assertEquals("person", IconGuesser.guess("Sister's Hand Cash"))
        assertEquals("bolt", IconGuesser.guess("Electricity Bill"))
        assertEquals("wifi", IconGuesser.guess("Internet Bill"))
        assertEquals("shopping", IconGuesser.guess("Personal Expenses"))
    }

    @Test
    fun keywordsOnlyMatchWordStarts() {
        assertEquals("card", IconGuesser.guess("Credit card bill")) // not "car"
        assertEquals("subscriptions", IconGuesser.guess("Netflix")) // not internet
        assertEquals("water", IconGuesser.guess("WASA water bill"))
        assertEquals("bike", IconGuesser.guess("Bike fuel")) // bike wins over fuel/car
    }

    @Test
    fun unknownTitleHasNoGuess() {
        assertNull(IconGuesser.guess("Misc"))
        assertNull(IconGuesser.guess(""))
    }
}
