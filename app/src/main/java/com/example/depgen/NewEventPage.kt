package com.example.depgen

import android.os.Build
import android.util.Log
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.format.DateTimeParseException
import java.util.Calendar
import java.util.HashMap

fun removeLetters(str: String): String {
    var removed = ""
    for (char in str) {
        if (char in '0'..'9') {
            removed += char
        }
    }
    return removed
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerScreen(setPicking: (Int) -> Unit, setText: (String) -> Unit, title: String) {
    val calendar = Calendar.getInstance()

    val timePickerState = rememberTimePickerState(
        initialHour = calendar.get(Calendar.HOUR_OF_DAY),
        initialMinute = calendar.get(Calendar.MINUTE),
        is24Hour = true
    )

    BasicAlertDialog(
        onDismissRequest = {
            setPicking(0)
        }
    ) {
        ElevatedCard (
            colors = CardDefaults.cardColors(
                containerColor = Color(220, 200, 200),
                contentColor = Color.Black
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(550.dp)
        ) {
            Column (
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(vertical = 30.dp)
                )
                TimePicker(state = timePickerState)
                Spacer(modifier = Modifier.weight(1f))
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            val hr = timePickerState.hour.toString().padStart(2, '0')
                            val min = timePickerState.minute.toString().padStart(2, '0')
                            setText("$hr:$min")
                            setPicking(0)
                        },
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Done")
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewEventPage() {
    var name by remember { mutableStateOf("") }
    var screen by remember { mutableStateOf("main") }
    var picking by remember { mutableIntStateOf(0) }
    var currSelection by remember { mutableIntStateOf(-1) }
    val deployment = remember { mutableMapOf<Profile, ArrayList<EventRole>>() }

    val selected = remember { mutableStateListOf<Boolean>() }
    if (selected.isEmpty()) {
        for (i in EVENT_TYPES.indices) {
            selected.add(false)
        }
    }

    val newEvent = remember { mutableStateOf(Event("", hashMapOf())) }
    val calendar = Calendar.getInstance()

    var title by remember { mutableStateOf("") }
    var day by remember { mutableStateOf("") }
    var month by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("${calendar.get(Calendar.YEAR)}") }

    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }
    var componentType : ComponentType? by remember { mutableStateOf(null) }

    Scaffold (
        topBar = {
            TopAppBar(
                title = {
                    Row (
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                when (screen) {
                                    "main" -> {
                                        navController.navigate("EventList")
                                    }
                                    "addMember" -> {
                                        //TODO: Alert
                                        screen = "addComponent-2"
                                    }
                                    "addComponent-1" -> {
                                        //TODO: Alert
                                        screen = "main"
                                    }
                                    "addComponent-2" -> {
                                        //TODO: Alert
                                        screen = "addComponent-1"
                                    }
                                    else -> {
                                        screen = "main"
                                    }
                                }
                            },
                            modifier = Modifier.height(30.dp)
                        ) {
                            Icon(Icons.Default.Close, "")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = title,
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
        Column (
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (picking == 1) {
                TimePickerScreen (
                    setPicking = { picking = it },
                    setText = { startTime = it },
                    "Select Starting Time"
                )
                return@Column

            } else if (picking == 2) {
                TimePickerScreen (
                    setPicking = { picking = it },
                    setText = { endTime = it },
                    "Select Ending Time"
                )
                return@Column
            }

            when (screen) {
                "main" -> {
                    title = "Create New Event"
                    Text(
                        text = "Event Name",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 15.dp)
                    )
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            newEvent.value.name = name
                        },
                        placeholder = { Text("Enter Event Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "Event Components",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 30.dp, bottom = 20.dp)
                    )
                    if (newEvent.value.components.isEmpty()) {
                        Column (modifier = Modifier.weight(1f)) {
                            Text("No Components Yet!")
                        }

                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            item {
                                Column {
                                    var rem = newEvent.value.components.size
                                    for (entry in newEvent.value.components) {
                                        Text(
                                            text = entry.key.componentType,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.padding(bottom = 10.dp)
                                        )
                                        entry.value.sortBy { it.getStart() }
                                        for (component in entry.value) {
                                            ElevatedCard(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(100.dp)
                                                    .padding(bottom = 12.dp),
                                                colors = CardDefaults.cardColors(
                                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                                                )
                                            ) {
                                                Column(
                                                    modifier = Modifier.fillMaxSize()
                                                ) {
                                                    Row {
                                                        Column(
                                                            modifier = Modifier
                                                                .padding(vertical = 10.dp)
                                                                .padding(start = 8.dp)
                                                        ) {
                                                            Text(
                                                                text = "Start Time:",
                                                                modifier = Modifier.padding(bottom = 5.dp),
                                                                fontWeight = FontWeight.SemiBold
                                                            )
                                                            Text(
                                                                text = "  End Time:",
                                                                modifier = Modifier.padding(bottom = 5.dp),
                                                                fontWeight = FontWeight.SemiBold
                                                            )
                                                        }
                                                        Column(
                                                            modifier = Modifier
                                                                .padding(vertical = 10.dp)
                                                                .padding(start = 5.dp)
                                                        ) {
                                                            Text(
                                                                text = component.getNaturalStart(),
                                                                modifier = Modifier.padding(
                                                                    bottom = 5.dp
                                                                )
                                                            )
                                                            Text(
                                                                text = component.getNaturalEnd(),
                                                                modifier = Modifier.padding(
                                                                    bottom = 5.dp
                                                                )
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        if (--rem > 0) Spacer(modifier = Modifier.height(15.dp))
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    CardButton(
                        text = "Add Event Component",
                        onClick = {
                            screen = "addComponent-1"

                            deployment.clear()
                            day = ""
                            month = ""
                            year = "${Calendar.getInstance().get(Calendar.YEAR)}"
                            startTime = ""
                            endTime = ""

                            if (currSelection != -1) selected[currSelection] = false
                            currSelection = -1
                        }
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    CardButton(
                        text = "Done",
                        onClick = {
                            if (name.isBlank()) {
                                toast("The Event Name Cannot be Empty!")
                            } else {
                                Global.eventList.add(newEvent.value)
                                navController.navigate("EventList")
                                save()
                            }
                        },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
                "addMember" -> {
                    title = "Select Member to Deploy"
                    Spacer(modifier = Modifier.height(20.dp))
                    MemberSearchScreen(
                        onClickMember = {
                            screen = "addComponent-2"
                            deployment[Global.profileList[it]] = ArrayList()
                        },
                        errorMessage = "No members found!",
                        exclude = setOf(1)
                    )
                }
                "addComponent-1" -> {
                    title = "New Event Component"
                    Text(
                        text = "Date and Time of Component",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 15.dp)
                    )
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Date: ",
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        TextField(
                            value = day,
                            onValueChange = {
                                day = removeLetters(it)
                                if (day.length > 2) day = day.substring(0, 2)

                                if (month.isNotBlank()) month = month.padStart(2, '0')
                                if (year.isNotBlank()) year = year.padStart(4, '0')
                            },
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .weight(0.3f),
                            placeholder = {
                                Text("DD")
                            }
                        )
                        TextField(
                            value = month,
                            onValueChange = {
                                month = removeLetters(it)
                                if (month.length > 2) month = month.substring(0, 2)

                                if (day.isNotBlank()) day = day.padStart(2, '0')
                                if (year.isNotBlank()) year = year.padStart(4, '0')
                            },
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .weight(0.3f),
                            placeholder = {
                                Text("MM")
                            }
                        )
                        TextField(
                            value = year,
                            onValueChange = {
                                year = removeLetters(it)
                                if (year.length > 4) year = year.substring(0, 4)

                                if (day.isNotBlank()) day = day.padStart(2, '0')
                                if (month.isNotBlank()) month = month.padStart(2, '0')
                            },
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .weight(0.4f),
                            placeholder = {
                                Text("YYYY")
                            }
                        )
                        IconButton(
                            onClick = {
                                val datePickerDialog = android.app.DatePickerDialog(
                                    ctxt,
                                    { _:
                                        DatePicker,
                                        selectedYear: Int,
                                        selectedMonth: Int,
                                        selectedDay: Int
                                    -> run {
                                        day = selectedDay.toString().padStart(2, '0')
                                        month = "${selectedMonth + 1}".padStart(2, '0')
                                        year = selectedYear.toString().padStart(4, '0')
                                    }
                                    },
                                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
                                )
                                datePickerDialog.show()
                            },
                            modifier = Modifier.width(50.dp)
                        ) {
                            Icon(Icons.Default.DateRange, "")
                        }
                    }
                    Row (
                        modifier = Modifier.padding(top = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Start Time: ", fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.width(30.dp))
                        TextField(
                            value = startTime,
                            onValueChange = { startTime = it },
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp),
                            placeholder = {
                                Text("Enter Start Time")
                            }
                        )
                        IconButton(
                            onClick = { picking = 1 },
                            modifier = Modifier.width(50.dp)
                        ) {
                            Icon(Icons.Default.AccessTime, "")
                        }
                    }
                    Row (
                        modifier = Modifier.padding(top = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("End Time: ", fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.width(38.dp))
                        TextField(
                            value = endTime,
                            onValueChange = { endTime = it },
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp),
                            placeholder = {
                                Text("Enter End Time")
                            }
                        )
                        IconButton(
                            onClick = { picking = 2 },
                            modifier = Modifier.width(50.dp)
                        ) {
                            Icon(Icons.Default.AccessTime, "")
                        }
                    }
                    Text(
                        text = "Type of Event Component",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 40.dp, bottom = 10.dp)
                    )

                    for (i in EVENT_TYPES.indices) {
                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                selected[i] = !selected[i]
                                if (currSelection != -1) selected[currSelection] = false

                                if (selected[i]) {
                                    currSelection = i
                                    componentType = EVENT_TYPES[i]
                                } else {
                                    currSelection = -1
                                    componentType = null
                                }
                            }
                        ) {
                            Checkbox(
                                checked = selected[i],
                                onCheckedChange = null,
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                                    .padding(end = 5.dp)
                            )
                            Text(text = EVENT_TYPES[i].componentType)
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))
                    CardButton(
                        text = "Next",
                        onClick = {
                            val startString = "$year-$month-${day}T${lazyTime(startTime)}:00"
                            val endString = "$year-$month-${day}T${lazyTime(endTime)}:00"

                            try {
                                toNaturalDateTime(startString)
                                toNaturalDateTime(endString)

                                startTime = lazyTime(startTime)
                                endTime = lazyTime(endTime)

                                if (currSelection == -1) throw InvalidEventTypeException("")

                                screen = "addComponent-2"

                            } catch (_: DateTimeParseException) {
                                toast("Invalid Date/Time!")
                                Log.d("INVALID", "Strings:\n$startString\n$endString")

                            } catch (_: InvalidEventTypeException) {
                                toast("Invalid Event Component Type")

                            } catch (e: Exception) {
                                toast("An Error Occurred!")
                                Log.d("UNKNOWN", e.toString())
                            }
                        },
                        colors = CardDefaults.cardColors(
                            MaterialTheme.colorScheme.primary
                        )
                    )
                }
                "addComponent-2" -> {
                    Text(
                        text = "Deployment for Component",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                    LazyColumn (
                        modifier = Modifier.weight(1f)
                    ) {
                        item {
                            if (deployment.isEmpty()) {
                                Column (
                                    modifier = Modifier.fillParentMaxHeight(0.5f)
                                ) {
                                    Spacer(modifier = Modifier.weight(0.5f))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = "No Members are Currently Deployed!",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(bottom = 10.dp)
                                        )
                                    }
                                }
                            }

                            Column {
                                for (entry in deployment) {
                                    ElevatedCard(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(114.dp)
                                            .padding(vertical = 7.dp),
                                        onClick = {
//                                    onClickMember(profilesFiltered[i])
                                        }
                                    ) {
                                        Column {
                                            Row {
                                                Image(
                                                    //TODO: Profile Picture
                                                    painter = painterResource(R.drawable.icon_512),
                                                    contentDescription = "",
                                                    modifier = Modifier.size(100.dp)
                                                )
                                                Text(
                                                    entry.key.username,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 19.sp,
                                                    modifier = Modifier.padding(8.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    CardButton(
                        text = "Add Member to Deployment",
                        onClick = {
                            screen = "addMember"
                        }
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    CardButton(
                        text = "Done",
                        onClick = {
                            try {
                                val startString = "$year-$month-${day}T$startTime:00"
                                val endString = "$year-$month-${day}T$endTime:00"

                                if (!newEvent.value.components.containsKey(componentType)) {
                                    newEvent.value.components[componentType!!] = ArrayList()
                                }

                                val deploymentHashMap = HashMap<Profile, ArrayList<EventRole>>()
                                for (entry in deployment) deploymentHashMap[entry.key] = entry.value

                                newEvent.value.components[componentType!!]!!.add(
                                    EventComponent(
                                        deployment = deploymentHashMap,
                                        start = startString,
                                        end = endString
                                    )
                                )

                                screen = "main"

                            } catch (_: DateTimeParseException) {
                                // TODO: Ensure Deployment Not Empty
                            }
                        },
                        colors = CardDefaults.cardColors(
                            MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }
    }
}
