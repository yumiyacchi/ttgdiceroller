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

    private val _lastActionMessageFlow = MutableStateFlow("Welcome! Add or roll some dice.")
    val lastActionMessageFlow: StateFlow<String> = _lastActionMessageFlow.asStateFlow()

    init {
        // --- Populate with some default dice ---
        // addDie(6)  // Add a D6
        // addDie(20) // Add a D20
        // --- End of default dice ---
        // Update flows even if no default dice are added to set initial state correctly
        updateFlows()
        _lastActionMessageFlow.value = "Welcome! Initial dice loaded." // Initial message after setup
        // If you add default dice, the addDie method will update the message.
        // For example, if adding default dice:
        val d6Added = diceSet.addDie(6)
        if (d6Added != null) {
            _lastActionMessageFlow.value = "Added a D${d6Added.faces}."
        }
        val d20Added = diceSet.addDie(20)
        if (d20Added != null) {
            _lastActionMessageFlow.value = "Added a D${d20Added.faces}." // This would overwrite the d6 message
            // A more sophisticated approach might append messages or queue them.
            // For simplicity, the last action dictates the message.
        }
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
            _lastActionMessageFlow.value = "Added a D${addedDie.faces}."
            updateFlows()
            true
        } else {
            _lastActionMessageFlow.value = "Failed to add D$faces. Max dice limit (${diceSet.maxDice}) reached or invalid faces."
            false
        }
    }

    fun removeDie(id: Int): Boolean {
        // To provide a better message, we might need to know what kind of die was removed.
        // This would require DiceSet.removeDie to return the Die object or find it first.
        val dieToRemove = diceSet.diceList.find { it.id == id }
        val removed = diceSet.removeDie(id)
        if (removed) {
            if (dieToRemove != null) {
                _lastActionMessageFlow.value = "Removed Die ${dieToRemove.id} (D${dieToRemove.faces})."
            } else {
                _lastActionMessageFlow.value = "Removed a die." // Fallback
            }
            updateFlows()
        } else {
            _lastActionMessageFlow.value = "Failed to remove die $id. Not found."
        }
        return removed
    }

    fun rollAllDice() {
        diceSet.rollAll()
        updateFlows() // Update flows first to get the new total
        _lastActionMessageFlow.value = "Rolled all dice! New total: ${_totalValueFlow.value}."
    }

    fun rollSingleDie(id: Int): Boolean {
        val rolled = diceSet.rollDie(id)
        if (rolled) {
            updateFlows() // Update all flows
            val die = _diceListFlow.value.find { it.id == id } // Get the updated die from the flow
            if (die != null) {
                _lastActionMessageFlow.value = "Rolled Die ${die.id} (D${die.faces}): Got a ${die.currentValue}!"
            } else {
                _lastActionMessageFlow.value = "Rolled die $id, but couldn't find details." // Should not happen if rollDie succeeded
            }
        } else {
            _lastActionMessageFlow.value = "Failed to roll die $id. Not found."
        }
        return rolled
    }

    fun clearDice() {
        diceSet.clear()
        updateFlows()
        _lastActionMessageFlow.value = "All dice cleared."
    }
}