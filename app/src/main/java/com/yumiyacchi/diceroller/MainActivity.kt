package com.yumiyacchi.diceroller

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yumiyacchi.diceroller.model.Die
import com.yumiyacchi.diceroller.viewmodel.DiceViewModel

class MainActivity : ComponentActivity() {

    private val diceViewModel: DiceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                DiceRollerScreen(diceViewModel)
            }
        }
        Log.d("MainActivity", "Initial Dice: ${diceViewModel.diceListFlow.value.joinToString { "${it.id}: D${it.faces}=${it.currentValue}" }}")
        Log.d("MainActivity", "Initial Total: ${diceViewModel.totalValueFlow.value}")
        Log.d("MainActivity", "Initial Count: ${diceViewModel.numberOfDiceFlow.value}")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiceRollerScreen(viewModel: DiceViewModel) {
    val diceList by viewModel.diceListFlow.collectAsState()
    val totalValue by viewModel.totalValueFlow.collectAsState()
    val numberOfDice by viewModel.numberOfDiceFlow.collectAsState()
    val lastActionMessage by viewModel.lastActionMessageFlow.collectAsState() // Added this line

    var showAddCustomDieDialog by remember { mutableStateOf(false) }

    if (showAddCustomDieDialog) {
        AddCustomDieDialog(
            onDismissRequest = { showAddCustomDieDialog = false },
            onConfirm = { faces ->
                if (!viewModel.addDie(faces)) {
                    Log.w("DiceRollerScreen", "Failed to add D$faces, set might be full or input invalid.")
                    // Optionally, show a Snackbar/Toast here to inform the user
                }
                showAddCustomDieDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Dice Roller") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddCustomDieDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add custom die")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Number of Dice: $numberOfDice", fontSize = 20.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Total Value: $totalValue", fontSize = 20.sp)
            Spacer(modifier = Modifier.height(8.dp)) // Added Spacer
            Text( // Added Text for lastActionMessage
                text = lastActionMessage,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary
            )
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
                        DieItem(
                            die = die,
                            onRemove = { viewModel.removeDie(die.id) },
                            onRollSingle = { viewModel.rollSingleDie(it) }
                        )
                        Divider()
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { viewModel.clearDice() },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Clear All Dice")
            }
        }
    }
}

@Composable
fun AddCustomDieDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var facesInput by remember { mutableStateOf("") }
    var facesError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Add Custom Die") },
        text = {
            Column {
                TextField(
                    value = facesInput,
                    onValueChange = {
                        facesInput = it
                        facesError = null // Clear error when user types
                    },
                    label = { Text("Number of Faces (2-100)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = facesError != null
                )
                if (facesError != null) {
                    Text(
                        text = facesError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val faces = facesInput.toIntOrNull()
                    if (faces != null && faces in 2..100) {
                        onConfirm(faces)
                    } else {
                        facesError = "Enter a number between 2 and 100."
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun DieItem(
    die: Die,
    onRemove: () -> Unit,
    onRollSingle: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text( // Updated text format
            text = "Dice ${die.id}: D${die.faces}: Got a ${die.currentValue}!",
            fontSize = 18.sp,
            modifier = Modifier.weight(1f)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { onRollSingle(die.id) },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text("Roll")
            }
            Button(
                onClick = onRemove,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text("Remove")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MaterialTheme {
        // Updated preview to reflect new UI elements if necessary, or kept simple
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Number of Dice: 2", fontSize = 20.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Total Value: 7", fontSize = 20.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Dice 1 (D6): Got a 4!", fontSize = 16.sp, color = MaterialTheme.colorScheme.primary) // Example message
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { /* dummy */ }) {
                Text("Roll All Dice")
            }
            Spacer(modifier = Modifier.height(16.dp))
            DieItem(
                die = Die(id = 1, faces = 6, currentValue = 4),
                onRemove = {},
                onRollSingle = {}
            )
            Divider()
            DieItem(
                die = Die(id = 2, faces = 20, currentValue = 13),
                onRemove = {},
                onRollSingle = {}
            )
        }
    }
}
