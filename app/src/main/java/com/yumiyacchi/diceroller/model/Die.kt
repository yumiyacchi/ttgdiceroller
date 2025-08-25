package com.yumiyacchi.diceroller.model

import kotlin.random.Random

data class Die(
    val id: Int, // To uniquely identify a die if you have multiple instances of same-faced dice
    val faces: Int = 6, // Default to a 6-sided die
    var currentValue: Int = 1
) {
    init {
        // Ensure faces are within the 1-100 range
        require(faces in 1..100) { "A die must have between 1 and 100 faces." }
        roll() // Initialize with a random value
    }

    fun roll() {
        currentValue = Random.nextInt(1, faces + 1)
    }
}
