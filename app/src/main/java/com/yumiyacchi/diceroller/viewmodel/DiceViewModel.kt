package com.yumiyacchi.diceroller.viewmodel

import androidx.lifecycle.ViewModel
import com.yumiyacchi.diceroller.model.Die
import com.yumiyacchi.diceroller.model.DiceSet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DiceViewModel : ViewModel() {

    private val diceSet = DiceSet(maxDice = 6) // Max 6 dice

    private val _diceListFlow = MutableStateFlow<List<Die>>(emptyList())
    val diceListFlow: StateFlow<List<Die>> = _diceListFlow.asStateFlow()

    private val _totalValueFlow = MutableStateFlow(0)
    val totalValueFlow: StateFlow<Int> = _totalValueFlow.asStateFlow()

    private val _numberOfDiceFlow = MutableStateFlow(0)
    val numberOfDiceFlow: StateFlow<Int> = _numberOfDiceFlow.asStateFlow()

    init {
        // --- Populate with some default dice ---
        addDie(6)  // Add a D6
        addDie(20) // Add a D20
        // --- End of default dice ---

        updateFlows() // Ensure flows are updated after adding initial dice
    }

    private fun updateFlows() {
        _diceListFlow.value = diceSet.diceList.toList()
        _totalValueFlow.value = diceSet.getTotalValue()
        _numberOfDiceFlow.value = diceSet.getNumberOfDice()
    }

    fun addDie(faces: Int): Boolean {
        val addedDie = diceSet.addDie(faces)
        return if (addedDie != null) {
            updateFlows()
            true
        } else {
            false
        }
    }

    fun removeDie(id: Int): Boolean {
        val removed = diceSet.removeDie(id)
        if (removed) {
            updateFlows()
        }
        return removed
    }

    fun rollAllDice() {
        diceSet.rollAll()
        updateFlows()
    }

    fun clearDice() {
        diceSet.clear()
        updateFlows()
    }
}
