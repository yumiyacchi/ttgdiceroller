package com.yumiyacchi.diceroller.model

class DiceSet(val maxDice: Int = 6) {

    private val _diceList = mutableListOf<Die>()
    val diceList: List<Die> get() = _diceList.toList() // Read-only public access

    init {
        require(maxDice > 0) { "A dice set must be able to hold at least 1 die." }
    }

    /**
     * Adds a Die to the set.
     * @param die The Die object to add.
     * @return True if the die was added successfully, false if the set is already full.
     */
    fun addDie(die: Die): Boolean {
        if (_diceList.size < maxDice) {
            _diceList.add(die)
            return true
        }
        return false // Set is full
    }

    /**
     * Creates and adds a new Die to the set with a specific number of faces.
     * A unique ID is automatically generated.
     * @param faces The number of faces for the new die.
     * @return The created Die instance if added successfully, null if the set is full or faces are invalid.
     */
    fun addDie(faces: Int): Die? {
        if (_diceList.size >= maxDice) {
            return null // Set is full
        }
        return try {
            // Generate a simple unique ID for now.
            // For a more robust app, you might use UUIDs or a more sophisticated ID generation.
            val newId = (_diceList.maxOfOrNull { it.id } ?: 0) + 1
            val newDie = Die(id = newId, faces = faces)
            _diceList.add(newDie)
            newDie
        } catch (e: IllegalArgumentException) {
            // Invalid number of faces passed to Die constructor
            null
        }
    }

    /**
     * Removes a die from the set by its ID.
     * @param id The ID of the die to remove.
     * @return True if a die with the given ID was found and removed, false otherwise.
     */
    fun removeDie(id: Int): Boolean {
        val dieToRemove = _diceList.find { it.id == id }
        return if (dieToRemove != null) {
            _diceList.remove(dieToRemove)
            true
        } else {
            false
        }
    }

    /**
     * Rolls all dice currently in the set.
     */
    fun rollAll() {
        _diceList.forEach { it.roll() }
    }

    /**
     * Clears all dice from the set.
     */
    fun clear() {
        _diceList.clear()
    }

    /**
     * Gets the current number of dice in the set.
     */
    fun getNumberOfDice(): Int {
        return _diceList.size
    }

    /**
     * Returns the sum of the current values of all dice in the set.
     */
    fun getTotalValue(): Int {
        return _diceList.sumOf { it.currentValue }
    }
}
