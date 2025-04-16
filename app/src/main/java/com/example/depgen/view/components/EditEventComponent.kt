package com.example.depgen.view.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonColors
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.depgen.EVENT_TYPES
import com.example.depgen.Global
import com.example.depgen.luxuryManager
import com.example.depgen.model.ComponentType
import com.example.depgen.model.EventComponent
import com.example.depgen.model.EventRole
import com.example.depgen.model.Profile
import com.example.depgen.toast
import com.example.depgen.utils.clearFocusOnKeyboardDismiss
import com.example.depgen.utils.findRole
import com.example.depgen.utils.lazyTime
import com.example.depgen.utils.save
import com.example.depgen.utils.toHHMMTime
import com.example.depgen.utils.toNaturalDateTime
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeParseException


@OptIn(ExperimentalMaterial3Api::class)
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
            if (eventComponent.start != "") eventComponent.getStart().toHHMMTime() else ""
        )
    }
    var endTime by remember {
        mutableStateOf(
            if (eventComponent.end != "") eventComponent.getEnd().toHHMMTime() else ""
        )
    }

    var componentType: ComponentType? by remember { mutableStateOf(comType) }

    var confirmationShowing by remember { mutableStateOf(false) }
    var selectedRoles by remember { mutableStateOf(setOf<String>()) }
    var recompile by remember { mutableIntStateOf(1) }
    var deployedMembersCount by remember { mutableIntStateOf(0) }

    val rolesNeeded = remember { mutableStateMapOf<EventRole, Int>() }
    val membersDeployed = remember { mutableStateMapOf<EventRole, ArrayList<Profile>>() }
    val rolesNotSelected = remember { mutableStateListOf<String>() }
    val delta = remember { mutableStateMapOf<Profile, Int>() }
    var searchingRole by remember { mutableStateOf(false) }
    var appendingRole by remember { mutableStateOf<EventRole?>(null) }

    LaunchedEffect (Unit) {
        membersDeployed.clear()
        rolesNeeded.clear()

        for (entry in eventComponent.rolesRequired) rolesNeeded[entry.key] = entry.value
        for (entry in eventComponent.deployment) {
            for (role in entry.value) {
                if (!membersDeployed.contains(role)) membersDeployed[role] = ArrayList()
                membersDeployed[role]!!.add(entry.key)
            }
        }
    }


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
                                        screen = "addComponent-3"
                                    }

                                    "addComponent-1" -> {
                                        confirmationShowing = true
                                    }

                                    "addComponent-2" -> {
                                        screen = "addComponent-1"
                                    }

                                    "addComponent-3" -> {
                                        screen = "addComponent-2"
                                    }

                                    else -> {}
                                }
                            },
                            modifier = Modifier.height(30.dp)
                        ) {
                            Icon(
                                imageVector = when (screen) {
                                    "addComponent-2", "addComponent-3" -> Icons.AutoMirrored.Filled.ArrowBack
                                    "addComponent-1", "addMember" -> Icons.Default.Close
                                    else -> Icons.Default.QuestionMark
                                },
                                contentDescription = ""
                            )
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
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (confirmationShowing) {
                ConfirmationScreen(
                    {
                        onExit(null)
                        confirmationShowing = false
                    },
                    {
                        confirmationShowing = false
                    }
                )
            }

            if (searchingRole) {
                BasicAlertDialog(
                    onDismissRequest = {
                        searchingRole = false
                    }
                ) {
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(500.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            contentColor = MaterialTheme.colorScheme.onBackground
                        )
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
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                            Text(
                                text = "Start typing to search for the role!",
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                            MultiSelectComboBox(
                                rolesNotSelected,
                                selectedRoles,
                                {
                                    if (selectedRoles.contains(it)) {
                                        selectedRoles -= it
                                        rolesNeeded.remove(findRole(it))
                                    } else {
                                        selectedRoles += it
                                        rolesNeeded[findRole(it)!!] = 1
                                    }
                                }
                            )

                            Spacer(modifier = Modifier.weight(1f))
                            CardButton(
                                text = "Select",
                                onClick = {
                                    searchingRole = false
                                },
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            )
                        }
                    }
                }
            }

            if (picking == 1) {
                var initH = 0
                var initM = 0

                try {
                    initH = LocalTime.parse(startTime).hour
                    initM = LocalTime.parse(startTime).minute
                } catch (_: DateTimeParseException) { }

                TimePickerScreen(
                    setPicking = { picking = it },
                    setText = { startTime = it },
                    title = "Select Starting Time",
                    initialHour = initH,
                    initialMinute = initM
                )
                return@Column

            } else if (picking == 2) {
                var initH = 0
                var initM = 0

                try {
                    initH = LocalTime.parse(endTime).hour
                    initM = LocalTime.parse(endTime).minute
                } catch (_: DateTimeParseException) { }

                TimePickerScreen(
                    setPicking = { picking = it },
                    setText = { endTime = it },
                    title = "Select Ending Time",
                    initialHour = initH,
                    initialMinute = initM
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
                            val member = Global.profileList[it]
                            screen = "addComponent-3"
                            membersDeployed[appendingRole]!!.add(member)
                            if (!delta.contains(member)) delta[member] = 1
                            else delta[member] = delta[member]!! + 1
                            deployedMembersCount++
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

                    DateInputRow(
                        "Date",
                        day, month, year,
                        { day = it }, { month = it }, { year = it }
                    )

                    Row(
                        modifier = Modifier.padding(top = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Start Time: ", fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.width(30.dp))
                        OutlinedTextField(
                            value = startTime,
                            onValueChange = { startTime = it },
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                                .clearFocusOnKeyboardDismiss(),
                            placeholder = {
                                Text("Enter Start Time")
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                                unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                            )
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
                        OutlinedTextField(
                            value = endTime,
                            onValueChange = { endTime = it },
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                                .clearFocusOnKeyboardDismiss(),
                            placeholder = {
                                Text("Enter End Time")
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                                unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                            )
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
                                if (currEventTypeSelection != -1) selectedEventType[currEventTypeSelection] = false

                                if (selectedEventType[i]) {
                                    currEventTypeSelection = i
                                    componentType = EVENT_TYPES[i]
                                } else {
                                    currEventTypeSelection = -1
                                    componentType = null
                                }
                            }
                        ) {
                            RadioButton(
                                selected = selectedEventType[i],
                                enabled = false,
                                onClick = null,
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                                    .padding(end = 5.dp),
                                colors = RadioButtonColors(
                                    selectedColor = MaterialTheme.colorScheme.primary,
                                    disabledSelectedColor = MaterialTheme.colorScheme.primary,
                                    unselectedColor = MaterialTheme.colorScheme.onBackground,
                                    disabledUnselectedColor = MaterialTheme.colorScheme.onBackground
                                )
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
                                startString.toNaturalDateTime()
                                endString.toNaturalDateTime()

                                startTime = lazyTime(startTime)
                                endTime = lazyTime(endTime)

                                if (currEventTypeSelection == -1) {
                                    toast("Select an Event Component Type!")
                                } else if (LocalDateTime.parse(startString).isAfter(LocalDateTime.parse(endString))) {
                                    toast("Start Time cannot be after End Time!")
                                } else {
                                    screen = "addComponent-2"
                                }

                            } catch (_: DateTimeParseException) {
                                toast("Invalid Date/Time Format!")

                            } catch (e: Exception) {
                                toast("An Unexpected Error Occurred!")
                            }
                        },
                        colors = CardDefaults.cardColors(
                            MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }

                "addComponent-2" -> {
                    Text(
                        text = "Roles Required for Component",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                    Text(
                        text = "Select the roles needed for your event!",
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    FadedLazyColumn (
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(10.dp))
                        }
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
                                                selectedRoles -= entry.key.eventRole
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
                            if (rolesNeeded.isEmpty()) {
                                toast("There must be at least 1 role needed!")
                            } else {
                                screen = "addComponent-3"
                            }
                        },
                        colors = CardDefaults.cardColors(
                            MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }

                "addComponent-3" -> {
                    for (entry in rolesNeeded) {
                        if (!membersDeployed.containsKey(entry.key)) {
                            membersDeployed[entry.key] = ArrayList()
                        }
                    }

                    Text(
                        text = "Deployment for Roles",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                    Text(
                        text = "You may choose to manually input all the deployment in this page, or leave this page blank and generate deployment automatically.",
                        modifier = Modifier.padding(bottom = 30.dp)
                    )
                    LazyColumn (
                        modifier = Modifier.weight(1f)
                    ) {
                        for (entry in rolesNeeded) {
                            item {
                                Column {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${entry.key.eventRole} (Required: ${entry.value})",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                        )
                                        Spacer(modifier = Modifier.weight(1f))
                                        IconButton(
                                            onClick = {
                                                if (membersDeployed[entry.key]!!.size < rolesNeeded[entry.key]!!) {
                                                    appendingRole = entry.key
                                                    screen = "addMember"
                                                }
                                            }
                                        ) {
                                            if (membersDeployed[entry.key]!!.size < rolesNeeded[entry.key]!!) {
                                                Icon(Icons.Default.Add, "")
                                            } else {
                                                Icon(Icons.Default.Check, "")
                                            }
                                        }
                                    }
                                    for (member in membersDeployed[entry.key]!!) {
                                        ElevatedCard(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .heightIn(114.dp)
                                                .padding(vertical = 7.dp)
                                        ) {
                                            Column {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(
                                                        10.dp
                                                    )
                                                ) {
                                                    luxuryManager.getLuxury(member).ProfilePicture(
                                                        clip = RoundedCornerShape(13.dp),
                                                        size = 70.dp
                                                    )
                                                    Text(
                                                        member.username,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 19.sp
                                                    )
                                                    Spacer(modifier = Modifier.weight(1f))
                                                    FilledIconButton(
                                                        onClick = {
                                                            val updatedList = membersDeployed[entry.key]!!.toMutableList()
                                                            updatedList.remove(member)
                                                            membersDeployed[entry.key] = ArrayList(updatedList)
                                                            eventComponent.deployment.remove(member)

                                                            if (!delta.containsKey(member)) delta[member] = -1
                                                            delta[member] = delta[member]!! - 1
                                                            deployedMembersCount--
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
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    CardButton(
                        text = "Save",
                        onClick = {
                            try {
                                val startString = "$year-$month-${day}T$startTime:00"
                                val endString = "$year-$month-${day}T$endTime:00"

                                val deploymentHashMap = HashMap<Profile, ArrayList<EventRole>>()
                                for (entry in eventComponent.deployment) {
                                    deploymentHashMap[entry.key] = entry.value
                                }

                                EventComponent(
                                    deployment = HashMap(),
                                    rolesRequired = HashMap(),
                                    start = startString,
                                    end = endString
                                )

                                eventComponent.start = startString
                                eventComponent.end = endString

                                eventComponent.rolesRequired.clear()
                                eventComponent.deployment.clear()
                                for (entry in rolesNeeded) {
                                    eventComponent.rolesRequired[entry.key] = entry.value
                                }
                                for (entry in membersDeployed) {
                                    for (member in entry.value) {
                                        if (!eventComponent.deployment.contains(member)) {
                                            eventComponent.deployment[member] = ArrayList()
                                        }
                                        eventComponent.deployment[member]!!.add(entry.key)
                                    }
                                }
                                for (entry in delta) {
                                    when (entry.value) {
                                        1 -> entry.key.addDeployment(eventComponent)
                                        -1 -> entry.key.removeDeployment(eventComponent)
                                    }
                                }

                                onExit(componentType!!)
                                save()

                            } catch (_: DateTimeParseException) {}
                        },
                        colors = CardDefaults.cardColors(
                            MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }
        }
    }
}
