package com.example.depgen.view.components

import android.os.Build
import android.util.Log
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NewLabel
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.depgen.Global
import com.example.depgen.R
import com.example.depgen.ctxt
import com.example.depgen.model.ComponentType
import com.example.depgen.EVENT_TYPES
import com.example.depgen.model.EventComponent
import com.example.depgen.model.EventRole
import com.example.depgen.lazyTime
import com.example.depgen.model.InvalidEventTypeException
import com.example.depgen.model.Profile
import com.example.depgen.save
import com.example.depgen.toHHMMTime
import com.example.depgen.toNaturalDateTime
import com.example.depgen.toast
import com.example.depgen.view.fragments.removeLetters
import java.time.format.DateTimeParseException
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditEventComponent(eventComponent: EventComponent, onExit: (ComponentType?) -> Unit, comType: ComponentType?) {
    var screen by remember { mutableStateOf("addComponent-1") }
    var picking by remember { mutableIntStateOf(0) }
    var currEventTypeSelection by remember { mutableIntStateOf(-1) }

    val selectedEventType = remember { mutableStateListOf<Boolean>() }
    if (selectedEventType.isEmpty()) {
        for (i in EVENT_TYPES.indices) {
            selectedEventType.add(
                comType != null &&
                EVENT_TYPES[i].componentType == comType.componentType
            )
            if (selectedEventType.last()) {
                currEventTypeSelection = i
            }
        }
    }

    val calendar = Calendar.getInstance()

    var title by remember { mutableStateOf("") }
    var day by remember {
        mutableStateOf(
            if (eventComponent.start != "") "${eventComponent.getStart().dayOfMonth}".padStart(
                2,
                '0'
            )
            else ""
        )
    }
    var month by remember {
        mutableStateOf(
            if (eventComponent.start != "") "${eventComponent.getStart().monthValue}".padStart(
                2,
                '0'
            )
            else ""
        )
    }
    var year by remember {
        mutableStateOf(
            if (eventComponent.start != "") "${eventComponent.getStart().year}"
            else ""
        )
    }

    var startTime by remember {
        mutableStateOf(
            if (eventComponent.start != "") toHHMMTime(
                eventComponent.getStart()
            ) else ""
        )
    }
    var endTime by remember { mutableStateOf(if (eventComponent.end != "") toHHMMTime(eventComponent.getEnd()) else "") }
    var componentType: ComponentType? by remember { mutableStateOf(comType) }

    var alertShowing by remember { mutableStateOf(false) }
    var confirmationShowing by remember { mutableStateOf(false) }
    val selectedRoles = remember { mutableStateListOf<Boolean>() }
    var roleProfile by remember { mutableStateOf<Profile?>(null) }
    var compile by remember { mutableStateOf(true) }

    if (selectedRoles.isEmpty()) for (role in Global.rolesList) selectedRoles.add(false)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                when (screen) {
                                    "addMember" -> {
                                        screen = "addComponent-2"
                                    }

                                    "addComponent-1" -> {
                                        confirmationShowing = true
                                    }

                                    "addComponent-2" -> {
                                        screen = "addComponent-1"
                                    }

                                    else -> {}
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
        if (confirmationShowing) {
            ConfirmationScreen(
                {
                    onExit(null)
                    confirmationShowing = false
                },
                {}
            )

        }

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (picking == 1) {
                TimePickerScreen(
                    setPicking = { picking = it },
                    setText = { startTime = it },
                    "Select Starting Time"
                )
                return@Column

            } else if (picking == 2) {
                TimePickerScreen(
                    setPicking = { picking = it },
                    setText = { endTime = it },
                    "Select Ending Time"
                )
                return@Column
            }

            when (screen) {
                "addMember" -> {
                    title = "Select Member to Deploy"
                    Spacer(modifier = Modifier.height(20.dp))
                    val exclude = mutableSetOf(1)
                    for (entry in eventComponent.deployment) exclude.add(entry.key.getIdx())
                    MemberSearchScreen(
                        onClickMember = {
                            screen = "addComponent-2"
                            eventComponent.deployment[Global.profileList[it]] = ArrayList()
                        },
                        errorMessage = "No members found!",
                        exclude = exclude
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
                    Row(
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
                                        ->
                                        run {
                                            day = selectedDay.toString().padStart(2, '0')
                                            month = "${selectedMonth + 1}".padStart(2, '0')
                                            year = selectedYear.toString().padStart(4, '0')
                                        }
                                    },
                                    calendar.get(Calendar.YEAR),
                                    calendar.get(Calendar.MONTH),
                                    calendar.get(Calendar.DAY_OF_MONTH)
                                )
                                datePickerDialog.show()
                            },
                            modifier = Modifier.width(50.dp)
                        ) {
                            Icon(Icons.Default.DateRange, "")
                        }
                    }
                    Row(
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
                    Row(
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                selectedEventType[i] = !selectedEventType[i]
                                if (currEventTypeSelection != -1) selectedEventType[currEventTypeSelection] =
                                    false

                                if (selectedEventType[i]) {
                                    currEventTypeSelection = i
                                    componentType = EVENT_TYPES[i]
                                } else {
                                    currEventTypeSelection = -1
                                    componentType = null
                                }
                            }
                        ) {
                            Checkbox(
                                checked = selectedEventType[i],
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

                                if (currEventTypeSelection == -1) {
                                    throw InvalidEventTypeException("")
                                }

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
                    if (alertShowing) {
                        BasicAlertDialog(
                            onDismissRequest = {
                                alertShowing = false
                            }
                        ) {
                            ElevatedCard(
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(220, 200, 200),
                                    contentColor = Color.Black
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(550.dp)
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Select Roles",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 20.sp,
                                        modifier = Modifier.padding(vertical = 30.dp)
                                    )
                                    Column {
                                        for (i in Global.rolesList.indices) {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.clickable {
                                                    selectedRoles[i] = !selectedRoles[i]
                                                }
                                            ) {
                                                Checkbox(
                                                    selectedRoles[i], {
                                                        selectedRoles[i] = it
                                                    }
                                                )
                                                Text(Global.rolesList[i].eventRole)
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(30.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            Button(
                                                onClick = {
                                                    eventComponent.deployment[roleProfile]!!.clear()
                                                    for (i in Global.rolesList.indices) {
                                                        if (selectedRoles[i]) {
                                                            eventComponent.deployment[roleProfile]!!.add(
                                                                Global.rolesList[i]
                                                            )
                                                        }
                                                    }
                                                    alertShowing = false
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
                    }

                    Text(
                        text = "Deployment for Component",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                    LazyColumn(
                        modifier = Modifier.weight(1f)
                    ) {
                        item {
                            if (eventComponent.deployment.isEmpty()) {
                                Column(
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
                                if (compile) {
                                    for (entry in eventComponent.deployment) {
                                        ElevatedCard(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .heightIn(114.dp)
                                                .padding(vertical = 7.dp),
                                            onClick = {
//                                    onClickMember(profilesFiltered[i])
                                            }
                                        ) {
                                            Column {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(
                                                        10.dp
                                                    )
                                                ) {
                                                    Image(
                                                        //TODO: Profile Picture
                                                        painter = painterResource(R.drawable.icon_512),
                                                        contentDescription = "",
                                                        modifier = Modifier
                                                            .size(70.dp)
                                                            .clip(RoundedCornerShape(13.dp))
                                                    )
                                                    Text(
                                                        entry.key.username,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 19.sp
                                                    )
                                                    Spacer(modifier = Modifier.weight(1f))
                                                    FilledIconButton(
                                                        onClick = {
                                                            compile = false
                                                            eventComponent.deployment.remove(
                                                                entry.key
                                                            )
                                                            compile = true
                                                        },
                                                        colors = IconButtonColors(
                                                            contentColor = Color.Black,
                                                            containerColor = MaterialTheme.colorScheme.secondary,
                                                            disabledContentColor = Color.Black,
                                                            disabledContainerColor = MaterialTheme.colorScheme.secondary
                                                        )
                                                    ) {
                                                        Icon(Icons.Default.Delete, "")
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(10.dp))
                                                Column(
                                                    modifier = Modifier.padding(8.dp),
                                                    verticalArrangement = Arrangement.spacedBy(5.dp)
                                                ) {
                                                    Text(
                                                        "Roles: ",
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                    if (entry.value.isEmpty()) {
                                                        Text("No Roles Yet!")
                                                    } else {
                                                        FlowRow(
                                                            horizontalArrangement = Arrangement.spacedBy(
                                                                10.dp
                                                            ),
                                                            verticalArrangement = Arrangement.spacedBy(
                                                                5.dp
                                                            )
                                                        ) {
                                                            for (role in entry.value) {
                                                                EventRoleRender(role)
                                                            }
                                                        }
                                                    }
                                                }
                                                Spacer(modifier = Modifier.weight(1f))
                                                Row {
                                                    Spacer(modifier = Modifier.weight(1f))
                                                    FilledIconButton(
                                                        onClick = {
                                                            alertShowing = true
                                                            roleProfile = entry.key
                                                            for (i in Global.rolesList.indices) {
                                                                selectedRoles[i] =
                                                                    (Global.rolesList[i] in entry.value)
                                                            }
                                                        },
                                                        colors = IconButtonColors(
                                                            MaterialTheme.colorScheme.tertiary,
                                                            Color.Black,
                                                            MaterialTheme.colorScheme.secondary,
                                                            Color.Black,
                                                        )
                                                    ) {
                                                        Icon(Icons.Filled.NewLabel, "")
                                                    }
                                                }
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

                                val deploymentHashMap = HashMap<Profile, ArrayList<EventRole>>()
                                for (entry in eventComponent.deployment) deploymentHashMap[entry.key] = entry.value

                                EventComponent(
                                    deployment = deploymentHashMap,
                                    start = startString,
                                    end = endString
                                )

                                eventComponent.start = startString
                                eventComponent.end = endString

                                onExit(componentType!!)
                                save()

                            } catch (_: DateTimeParseException) { }
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
