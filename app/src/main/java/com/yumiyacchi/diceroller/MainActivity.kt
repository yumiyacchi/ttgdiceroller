package com.yumiyacchi.diceroller

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity // Changed from AppCompatActivity for a simpler Compose setup
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yumiyacchi.diceroller.model.Die
import com.yumiyacchi.diceroller.viewmodel.DiceViewModel

// Using a predefined theme (ensure you have one, e.g., Theme.Material3.DayNight.NoActionBar or your custom one)
// If you create a ui.theme package with Theme.kt, you'd use that.
// For now, let's assume a basic MaterialTheme {} wrapper.
// import com.yumiyacchi.diceroller.ui.theme.DiceRollerTheme // If you create this

class MainActivity : ComponentActivity() { // Changed to ComponentActivity

    private val diceViewModel: DiceViewModel by viewModels()
    // Shake detection related fields (removed for now, can be re-added)
    // private lateinit var sensorManager: SensorManager
    // private var accelerometer: Sensor? = null
    // private var lastAcceleration = SensorManager.GRAVITY_EARTH
    // private var lastShakeTime: Long = 0
    // private val shakeThreshold = 10.0f
    // private val shakeCooldownMs = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Shake sensor setup (removed for now)

        setContent {
            // If you have a custom theme in ui.theme/Theme.kt, wrap DiceRollerScreen with it:
            // DiceRollerTheme {
            MaterialTheme { // Using a default MaterialTheme for now
                DiceRollerScreen(diceViewModel)
            }
            // }
        }

        // Log initial dice from ViewModel (can be removed if UI shows them)
        Log.d("MainActivity", "Initial Dice: ${diceViewModel.diceListFlow.value.joinToString { "${it.id}: D${it.faces}=${it.currentValue}" }}")
        Log.d("MainActivity", "Initial Total: ${diceViewModel.totalValueFlow.value}")
        Log.d("MainActivity", "Initial Count: ${diceViewModel.numberOfDiceFlow.value}")
    }

    // onResume, onPause, onAccuracyChanged, onSensorChanged (related to shake) are removed for now.
    // onCreateOptionsMenu, onOptionsItemSelected, onSupportNavigateUp (related to XML ActionBar/Nav) are removed.
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiceRollerScreen(viewModel: DiceViewModel) {
    // Collect StateFlows as State objects for Compose
    val diceList by viewModel.diceListFlow.collectAsState()
    val totalValue by viewModel.totalValueFlow.collectAsState()
    val numberOfDice by viewModel.numberOfDiceFlow.collectAsState()

    Scaffold( // Provides basic Material Design structure
        topBar = {
            TopAppBar(
                title = { Text("Dice Roller") }
                // colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer) // Example theming
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                // Example: Add a D6, can be expanded with an input dialog later
                if (!viewModel.addDie(6)) {
                    // Optionally show a Snackbar or Toast if adding failed (e.g., max dice reached)
                    Log.w("DiceRollerScreen", "Failed to add D6, set might be full.")
                }
            }) {
                // Icon(Icons.Filled.Add, "Add Die") // Requires androidx.compose.material.icons
                Text("+D6") // Simple text FAB for now
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues) // Apply padding from Scaffold
                .padding(16.dp) // Add our own padding
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Number of Dice: $numberOfDice", fontSize = 20.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Total Value: $totalValue", fontSize = 20.sp)
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { viewModel.rollAllDice() }) {
                Text("Roll All Dice")
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (diceList.isEmpty()) {
                Text("No dice yet. Add some!")
            } else {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(diceList, key = { die -> die.id }) { die ->
                        DieItem(die = die, onRemove = { viewModel.removeDie(die.id) })
                        Divider()
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { viewModel.clearDice() },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Clear All Dice")
            }
        }
    }
}

@Composable
fun DieItem(die: Die, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("ID: ${die.id} - D${die.faces}: ${die.currentValue}", fontSize = 18.sp)
        Button(onClick = onRemove, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)) {
            Text("Remove")
        }
    }
}

// Basic Preview (won't have actual ViewModel logic, but good for layout)
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MaterialTheme {
        // Create a dummy ViewModel or pass null/empty data for preview purposes
        // This preview won't run the actual ViewModel logic but helps visualize the Composables
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Number of Dice: 2", fontSize = 20.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Total Value: 7", fontSize = 20.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { /* dummy */ }) {
                Text("Roll All Dice")
            }
            Spacer(modifier = Modifier.height(16.dp))
            DieItem(die = Die(id = 1, faces = 6, currentValue = 4), onRemove = {})
            Divider()
            DieItem(die = Die(id = 2, faces = 20, currentValue = 13), onRemove = {})
        }
    }
}
