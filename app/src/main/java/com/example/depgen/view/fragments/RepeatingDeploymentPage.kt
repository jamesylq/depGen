package com.example.depgen.view.fragments

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.depgen.Global
import com.example.depgen.model.EventComponent
import com.example.depgen.model.EventRole
import com.example.depgen.toast
import com.example.depgen.utils.NATURAL_FORMATTER
import com.example.depgen.utils.NO_DATE
import com.example.depgen.utils.RDCompleteSearchHelper
import com.example.depgen.utils.daysOfWeekToString
import com.example.depgen.utils.findRole
import com.example.depgen.utils.getDays
import com.example.depgen.utils.safeNavigate
import com.example.depgen.view.components.BoldTextParser
import com.example.depgen.view.components.CardButton
import com.example.depgen.view.components.ComboBox
import com.example.depgen.view.components.ConfirmationScreen
import com.example.depgen.view.components.DateInputRow
import com.example.depgen.view.components.DisplayEventComponent
import com.example.depgen.view.components.QuantityPicker
import com.example.depgen.view.components.RepeatDaySelector
import java.time.DateTimeException
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepeatingDeploymentPage() {
    val roleNums = remember { mutableStateListOf<Int>() }
    val rolesNeeded = remember { mutableStateMapOf<EventRole, Int>() }
    val rolesNotSelected = remember { mutableStateListOf<String>() }
    var searchingRole by remember { mutableStateOf(false) }
    var confirmationShowing by remember { mutableStateOf(false) }
    var selectedRole by remember { mutableStateOf("") }
    var roleError by remember { mutableStateOf("") }
    var recompile by remember { mutableIntStateOf(1) }
    var screen by remember { mutableStateOf("customisation-1") }

    var startDay by remember { mutableStateOf("") }
    var startMonth by remember { mutableStateOf("") }
    var startYear by remember { mutableStateOf("") }
    var endDay by remember { mutableStateOf("") }
    var endMonth by remember { mutableStateOf("") }
    var endYear by remember { mutableStateOf("") }
    val selectedDOW = remember {
        mutableStateListOf<Boolean>().apply {
            repeat(7) {
                add(false)
            }
        }
    }
    var valid by remember { mutableStateOf(false) }
    var start by remember { mutableStateOf<LocalDate?>(null) }
    var end by remember { mutableStateOf<LocalDate?>(null) }

    val generatedComponents = remember { mutableStateListOf<EventComponent>() }
    val expanded = remember { mutableStateListOf<Boolean>() }
    val days = remember { mutableStateListOf<LocalDate>() }

    if (roleNums.isEmpty()) for (role in Global.rolesList) roleNums.add(role.minCount)

    Scaffold (
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                when (screen) {
                                    "customisation-1" -> {
                                        confirmationShowing = true
                                    }

                                    "customisation-2" -> {
                                        screen = "customisation-1"
                                    }

                                    "generated" -> {
                                        screen = "customisation-2"
                                    }

                                    else -> {}
                                }
                            },
                            modifier = Modifier.height(30.dp)
                        ) {
                            Icon(
                                imageVector = when (screen) {
                                    "customisation-2", "generated" -> Icons.AutoMirrored.Filled.ArrowBack
                                    "customisation-1" -> Icons.Default.Close
                                    else -> Icons.Default.QuestionMark
                                },
                                contentDescription = ""
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Repeating Deployment",
                            fontSize = 24.sp,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    MaterialTheme.colorScheme.tertiary
                )
            )
        }
    ) { innerPadding ->
        LaunchedEffect (screen) {
            if (screen == "regenerate") {
                generatedComponents.clear()
                generatedComponents.addAll(
                    RDCompleteSearchHelper(
                        EventComponent(
                            HashMap(),
                            HashMap(rolesNeeded),
                            NO_DATE,
                            NO_DATE
                        ),
                        days
                    ).generate()
                )
                expanded.apply {
                    repeat(generatedComponents.size) {
                        add(false)
                    }
                }
                screen = "generated"
            }
        }

        Column (
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (confirmationShowing) {
                ConfirmationScreen(
                    {
                        confirmationShowing = false
                        safeNavigate("Master")
                    },
                    {
                        confirmationShowing = false
                    }
                )
            }

            when (screen) {
                "customisation-1" -> {
                    if (searchingRole) {
                        BasicAlertDialog(
                            onDismissRequest = {
                                searchingRole = false
                            }
                        ) {
                            ElevatedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(500.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = "Select Role",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 20.sp,
                                            color = Color.Black
                                        )
                                    }
                                    Text(
                                        text = "Start typing to search for the role!",
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                    Spacer(modifier = Modifier.height(20.dp))
                                    Row(
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        ComboBox(
                                            rolesNotSelected,
                                            selectedRole,
                                            { selectedRole = it },
                                            errorMessage = roleError,
                                            resetErrorMessage = { roleError = "" }
                                        )
                                    }
                                    Spacer(modifier = Modifier.weight(1f))
                                    CardButton(
                                        text = "Select",
                                        onClick = {
                                            val role = findRole(selectedRole)
                                            if (role == null) {
                                                roleError = "Undefined Role!"
                                            } else {
                                                selectedRole = ""
                                                rolesNeeded[role] = 1
                                                searchingRole = false
                                            }
                                        },
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.primaryContainer
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Text(
                        text = "Deployment",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Select the roles needed for your event!",
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        if (recompile > 0) {
                            for (entry in rolesNeeded) {
                                item {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(entry.key.eventRole)
                                        Spacer(modifier = Modifier.weight(1f))
                                        QuantityPicker(
                                            {
                                                rolesNeeded[entry.key] = it
                                                if (rolesNeeded[entry.key] == 0) {
                                                    rolesNeeded.remove(entry.key)
                                                    recompile++
                                                }
                                            },
                                            initialQty = minOf(
                                                maxOf(
                                                    entry.key.minCount,
                                                    rolesNeeded[entry.key]!!
                                                ), entry.key.maxCount
                                            ),
                                            minQty = entry.key.minCount,
                                            maxQty = entry.key.maxCount,
                                            scale = 0.8f
                                        )
                                    }
                                }
                            }

                            if (rolesNeeded.isEmpty()) {
                                item {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Spacer(
                                            modifier = Modifier.height(
                                                maxOf(
                                                    0,
                                                    LocalConfiguration.current.screenHeightDp / 2 - 180
                                                ).dp
                                            )
                                        )
                                        Text(
                                            "No Roles Yet!",
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 18.sp,
                                            modifier = Modifier.padding(bottom = 10.dp)
                                        )
                                        Text("Add the roles required using the button below!")
                                    }
                                }
                            }
                        }
                    }
                    CardButton(
                        text = "Add Role Needed",
                        onClick = {
                            searchingRole = true
                            rolesNotSelected.clear()
                            for (role in Global.rolesList) {
                                if (!rolesNeeded.containsKey(role)) {
                                    rolesNotSelected.add(role.eventRole)
                                }
                            }
                        },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    CardButton(
                        text = "Next",
                        onClick = {
                            screen = "customisation-2"
                        },
                        enabled = rolesNeeded.isNotEmpty()
                    )
                }

                "customisation-2" -> {
                    Text(
                        text = "Repeating Frequency",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 15.dp)
                    )
                    Text("Select how often this event repeats!")
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "Repeats Every:",
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    RepeatDaySelector (
                        onUpdate = { selectedDOW[it] = !selectedDOW[it] },
                        selected = ArrayList(selectedDOW)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    DateInputRow(
                        "From",
                        startDay, startMonth, startYear,
                        { startDay = it }, { startMonth = it }, { startYear = it }
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    DateInputRow(
                        "Until",
                        endDay, endMonth, endYear,
                        { endDay = it }, { endMonth = it }, { endYear = it }
                    )

                    try {
                        start = LocalDate.of(startYear.toInt(), startMonth.toInt(), startDay.toInt())
                        end = LocalDate.of(endYear.toInt(), endMonth.toInt(), endDay.toInt())
                        var x = false
                        selectedDOW.forEach { x = x || it }
                        days.clear()
                        days.addAll(getDays(start!!, end!!, selectedDOW))
                        valid = x && days.size > 0

                    } catch (_: DateTimeException) {
                        valid = false

                    } catch (_: NumberFormatException) {
                        valid = false

                    } catch (_: IllegalArgumentException) {
                        valid = false
                    }

                    if (valid) {
                        Spacer(modifier = Modifier.height(40.dp))
                        Text(
                            text = "Preview",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 15.dp)
                        )
                        BoldTextParser(
                            text = "This will generate deployment **${
                                daysOfWeekToString(selectedDOW)
                            }** between **${
                                NATURAL_FORMATTER.format(start)
                            }** and **${
                                NATURAL_FORMATTER.format(end)
                            }** for a total of **${days.size} ${
                                if (days.size == 1) "day" else "days"
                            }** of deployment."
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))
                    CardButton(
                        text = "Generate!",
                        onClick = {
                            if (valid) {
                                screen = "regenerate"

                            } else {
                                try {
                                    start = LocalDate.of(startYear.toInt(), startMonth.toInt(), startDay.toInt())
                                    end = LocalDate.of(endYear.toInt(), endMonth.toInt(), endDay.toInt())
                                    days.clear()
                                    days.addAll(getDays(start!!, end!!, selectedDOW))
                                    if (days.isEmpty()) {
                                        toast("No Matching Deployment Dates!")
                                    }

                                } catch (_: DateTimeException) {
                                    toast("Invalid Date Format!")

                                } catch (_: NumberFormatException) {
                                    toast("Invalid Date Format!")

                                } catch (e: IllegalArgumentException) {
                                    toast(e.message!!)
                                }
                            }
                        },
                        colors = CardDefaults.cardColors(
                            containerColor =
                                if (valid) MaterialTheme.colorScheme.primaryContainer
                                else Color.LightGray
                        )
                    )
                }

                "generated" -> {
                    Column {
                        LazyColumn (
                            modifier = Modifier.weight(1f)
                        ) {
                            for (i in generatedComponents.indices) {
                                item {
                                    DisplayEventComponent(
                                        component = generatedComponents[i],
                                        onToggleExpand = { expanded[i] = !expanded[i] },
                                        expanded = expanded[i]
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        CardButton(
                            text = "Regenerate!",
                            onClick = {
                                screen = "regenerate"
                            }
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        CardButton(
                            text = "Export as .xlsx",
                            onClick = {

                            }
                        )
                    }
                }
            }
        }
    }
}
