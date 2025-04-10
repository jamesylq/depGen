package com.example.depgen.view.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
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
import com.example.depgen.EVENT_TYPES
import com.example.depgen.Global
import com.example.depgen.R
import com.example.depgen.model.ComponentType
import com.example.depgen.model.EventComponent
import com.example.depgen.model.EventRole
import com.example.depgen.model.InvalidEventTypeException
import com.example.depgen.model.Profile
import com.example.depgen.toast
import com.example.depgen.utils.clearFocusOnKeyboardDismiss
import com.example.depgen.utils.findRole
import com.example.depgen.utils.lazyTime
import com.example.depgen.utils.save
import com.example.depgen.utils.toHHMMTime
import com.example.depgen.utils.toNaturalDateTime
import com.example.depgen.view.fragments.removeLetters
import java.time.format.DateTimeParseException

@RequiresApi(Build.VERSION_CODES.O)
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
            if (eventComponent.start != "") toHHMMTime(
                eventComponent.getStart()
            ) else ""
        )
    }
    var endTime by remember {
        mutableStateOf(
            if (eventComponent.end != "") toHHMMTime(
                eventComponent.getEnd()
            ) else ""
        )
    }

    var componentType: ComponentType? by remember { mutableStateOf(comType) }

    var pickingDate by remember { mutableStateOf(false) }
    var confirmationShowing by remember { mutableStateOf(false) }
    val selectedRoles = remember { mutableStateListOf<Boolean>() }
    var recompile by remember { mutableIntStateOf(1) }
    var deployedMembersCount by remember { mutableIntStateOf(0) }

    val rolesNeeded = remember { mutableStateMapOf<EventRole, Int>() }
    val membersDeployed = remember { mutableStateMapOf<EventRole, ArrayList<Profile>>() }
    val rolesNotSelected = remember { mutableStateListOf<String>() }
    val delta = remember { mutableStateMapOf<Profile, Int>() }
    var searchingRole by remember { mutableStateOf(false) }
    var selectedRole by remember { mutableStateOf("") }
    var roleError by remember { mutableStateOf("") }
    var appendingRole by remember { mutableStateOf<EventRole?>(null) }

    if (selectedRoles.isEmpty()) {
        membersDeployed.clear()
        rolesNeeded.clear()

        for (role in Global.rolesList) selectedRoles.add(false)
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

            if (pickingDate) {
                DatePickerScreen (
                    onDateSelected = {
                        day = it.dayOfMonth.toString().padStart(2, '0')
                        month = it.monthValue.toString().padStart(2, '0')
                        year = it.year.toString().padStart(4, '0')
                    },
                    onDismiss = {
                        pickingDate = false
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
                            .height(500.dp)
                    ) {
                        Column (
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row (
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
                            Row (
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
                                .weight(0.3f)
                                .clearFocusOnKeyboardDismiss(),
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
                                .weight(0.3f)
                                .clearFocusOnKeyboardDismiss(),
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
                                .weight(0.4f)
                                .clearFocusOnKeyboardDismiss(),
                            placeholder = {
                                Text("YYYY")
                            }
                        )
                        IconButton(
                            onClick = {
                                pickingDate = true
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
                                .padding(end = 8.dp)
                                .clearFocusOnKeyboardDismiss(),
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
                                .padding(end = 8.dp)
                                .clearFocusOnKeyboardDismiss(),
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
                                    unselectedColor = Color.Gray,
                                    disabledUnselectedColor = Color.Gray
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

                            } catch (_: InvalidEventTypeException) {
                                toast("Select an Event Component Type!")

                            } catch (e: Exception) {
                                toast("An Error Occurred!")
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
                    Spacer(modifier = Modifier.height(20.dp))
                    LazyColumn (
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
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
                                                .padding(vertical = 7.dp),
                                            onClick = {
                                                //TODO: Decide what to do here
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
