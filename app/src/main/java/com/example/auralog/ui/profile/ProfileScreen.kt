package com.example.auralog.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.auralog.data.UserStats
import com.example.auralog.data.UserStatsDataStore

import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
) {

    // --- State fields ---
    var birthYear by remember { mutableStateOf(UserStats.birthYear?.toString() ?: "") }
    var gender by remember { mutableStateOf(UserStats.gender ?: "") }
    var diagnosed by remember { mutableStateOf(UserStats.isDiagnosedMigraine) }
    var freq by remember { mutableStateOf(UserStats.episodeFrequency?.toString() ?: "") }
    var duration by remember { mutableStateOf(UserStats.avgDurationDays?.toString() ?: "") }
    var smokes by remember { mutableStateOf(UserStats.cigarettesPerDay?.toString() ?: "") }
    var caffeine by remember { mutableStateOf(UserStats.caffeinePerDay?.toString() ?: "") }

    val genderOptions = listOf("Male", "Female")

    // Dropdown menu state
    var expanded by remember { mutableStateOf(false) }

    // Blank = valid. Non-blank = must pass validation.

    val birthYearValid =
        birthYear.isBlank() || birthYear.toIntOrNull()?.let { it in 1900..2025 } == true

    val freqValid =
        freq.isBlank() || freq.toIntOrNull()?.let { it >= 0 } == true

    val durationValid =
        duration.isBlank() || duration.toIntOrNull()?.let { it >= 0 } == true

    val smokesValid =
        smokes.isBlank() || smokes.toIntOrNull()?.let { it >= 0 } == true

    val caffeineValid =
        caffeine.isBlank() || caffeine.toIntOrNull()?.let { it >= 0 } == true

// Gender dropdown: blank = valid (unselected), but must match allowed options if not blank
    val genderValid =
        gender.isBlank() || gender in listOf("Male", "Female", "Other")


    val formValid = birthYearValid &&
            freqValid &&
            durationValid &&
            smokesValid &&
            caffeineValid &&
            genderValid

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text("Profile", style = MaterialTheme.typography.headlineMedium)

        // --- Birth Year ---
        ProfileTextField(
            label = "Birth Year",
            value = birthYear,
            onValue = { birthYear = it },
            isError = !birthYearValid,
            errorMessage = "Enter a valid year"
        )

        // --- Gender Dropdown ---
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = gender,
                onValueChange = {},
                readOnly = true,
                label = { Text("Gender") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                isError = !genderValid
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                genderOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            gender = option
                            expanded = false
                        }
                    )
                }
            }
        }

        // --- Diagnosed Checkbox ---
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = diagnosed, onCheckedChange = { diagnosed = it })
            Text("Diagnosed with migraine?")
        }

        // --- Other fields ---
        ProfileTextField(
            "Migraine Frequency (per month)",
            freq,
            { freq = it },
            isError = !freqValid,
            errorMessage = "Enter a non-negative number"
        )

        ProfileTextField(
            "Avg Episode Duration (days)",
            duration,
            { duration = it },
            isError = !durationValid,
            errorMessage = "Enter a non-negative number"
        )

        ProfileTextField(
            "Cigarettes per day",
            smokes,
            { smokes = it },
            isError = !smokesValid,
            errorMessage = "Enter a non-negative number"
        )

        ProfileTextField(
            "Caffeinated drinks per day",
            caffeine,
            { caffeine = it },
            isError = !caffeineValid,
            errorMessage = "Enter a non-negative number"
        )

        Spacer(Modifier.height(20.dp))

        val context = LocalContext.current
        val scope = rememberCoroutineScope() // <-- Use this instead of lifecycleScope

        Button(
            onClick = {
                // Update UserStats
                UserStats.birthYear = birthYear.toIntOrNull()
                UserStats.gender = gender.ifBlank { null }
                UserStats.isDiagnosedMigraine = diagnosed

                UserStats.episodeFrequency = freq.toIntOrNull()
                UserStats.avgDurationDays = duration.toIntOrNull()
                UserStats.cigarettesPerDay = smokes.toIntOrNull()
                UserStats.caffeinePerDay = caffeine.toIntOrNull()

                // Save to DataStore
                scope.launch {
                    UserStatsDataStore.save(context)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text("Save")
        }
    }
}

@Composable
fun ProfileTextField(
    label: String,
    value: String,
    onValue: (String) -> Unit,
    isError: Boolean = false,
    errorMessage: String = ""
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValue,
            label = { Text(label) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            isError = isError
        )
        if (isError) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
            )
        }
    }
}
