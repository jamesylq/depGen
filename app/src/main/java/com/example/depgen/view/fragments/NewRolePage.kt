package com.example.depgen.view.fragments

import BrightnessSelector
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Square
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.depgen.AT_LEAST
import com.example.depgen.AT_MOST
import com.example.depgen.CONDITION_TYPES
import com.example.depgen.Global
import com.example.depgen.model.Condition
import com.example.depgen.model.EventRole
import com.example.depgen.model.Skill
import com.example.depgen.utils.applyBrightness
import com.example.depgen.utils.clearFocusOnKeyboardDismiss
import com.example.depgen.utils.isNotInt
import com.example.depgen.utils.reverseSettings
import com.example.depgen.utils.safeNavigate
import com.example.depgen.utils.save
import com.example.depgen.utils.toColor
import com.example.depgen.utils.toList
import com.example.depgen.view.components.CardButton
import com.example.depgen.view.components.ComboBox
import com.example.depgen.view.components.ConfirmationScreen
import com.example.depgen.view.components.EventRoleRender
import com.example.depgen.view.components.IntegerTextField
import com.example.depgen.view.components.colorPicker.HsvColorPicker
import com.example.depgen.view.components.colorPicker.rememberColorPickerController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewRolePage(roleEditing: Int = -1) {
    var exitConfirmationShowing by remember { mutableStateOf(false) }
    var deleteRequirementConfirmationShowing by remember { mutableStateOf<Pair<Skill, Int>?>(null) }
    var name by remember { mutableStateOf("") }
    var addingPreReq by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf(false) }
    var conditionError by remember { mutableStateOf("") }
    var skillLevelError by remember { mutableStateOf("") }
    var skillError by remember { mutableStateOf("") }
    var selectedCondition by remember { mutableStateOf("") }
    var selectedSkill by remember { mutableStateOf("") }
    var skillLevel by remember { mutableStateOf("") }
    var prerequisites = remember { mutableMapOf<Skill, ArrayList<Condition?>>() }
    var priority by remember { mutableIntStateOf(0) }
    var priorityError by remember { mutableStateOf(false) }
    val controller = rememberColorPickerController()
    var selectedHue by remember { mutableStateOf(Color.Black) }
    var brightness by remember { mutableFloatStateOf(0f) }
    var selectedColor by remember { mutableStateOf(Color.Black) }
    var selectingColor by remember { mutableStateOf(false) }

    BackHandler {
        exitConfirmationShowing = true
    }

    LaunchedEffect(Unit) {
        if (roleEditing != -1 && name != Global.rolesList[roleEditing].eventRole) {
            val role = Global.rolesList[roleEditing]
            name = role.eventRole
            prerequisites = role.prerequisites
            priority = role.priority
            selectedColor = role.color.toColor()
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
                                exitConfirmationShowing = true
                            },
                            modifier = Modifier.height(30.dp)
                        ) {
                            Icon(Icons.Default.Close, "")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Create New Role",
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
        if (exitConfirmationShowing) {
            ConfirmationScreen(
                {
                    safeNavigate("RolesList")
                    exitConfirmationShowing = false
                },
                {
                    exitConfirmationShowing = false
                }
            )

        } else if (deleteRequirementConfirmationShowing != null) {
            ConfirmationScreen(
                {
                    prerequisites[deleteRequirementConfirmationShowing!!.first]!!
                        .removeAt(deleteRequirementConfirmationShowing!!.second)
                    if (prerequisites[deleteRequirementConfirmationShowing!!.first]!!.isEmpty()) {
                        prerequisites.remove(deleteRequirementConfirmationShowing!!.first)
                    }
                    deleteRequirementConfirmationShowing = null
                },
                {
                    deleteRequirementConfirmationShowing = null
                }
            )

        } else if (addingPreReq) {
            BasicAlertDialog(
                onDismissRequest = {
                    addingPreReq = false
                }
            ) {
                ElevatedCard (
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = Color.Black
                    )
                ) {
                    Column (
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(30.dp))
                        Text(
                            text = "Role Prerequisite",
                            fontWeight = FontWeight.Bold,
                            fontSize = 25.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(30.dp))
                        Text(
                            text = "Members must have",
                            fontSize = 19.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Row (
                            modifier = Modifier.width(150.dp)
                        ) {
                            ComboBox(
                                options = CONDITION_TYPES,
                                selectedOption = selectedCondition,
                                onOptionSelected = { selectedCondition = it },
                                errorMessage = conditionError,
                                resetErrorMessage = { conditionError = "" }
                            )
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = "a skill level of",
                            fontSize = 19.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            skillLevel,
                            {
                                skillLevel = it
                                skillLevelError = if (skillLevel.isNotInt()) "Skill Level must be an Integer!" else ""
                            },
                            modifier = Modifier
                                .width(150.dp)
                                .clearFocusOnKeyboardDismiss(),
                            supportingText = {
                                if (skillLevelError != "") {
                                    Text(
                                        modifier = Modifier.fillMaxWidth(),
                                        text = skillLevelError,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            },
                            trailingIcon = {
                                if (skillLevelError != "") {
                                    Icon(
                                        Icons.Rounded.Warning,
                                        "",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                                unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                            )
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = "in the skill",
                            fontSize = 19.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Row (
                            modifier = Modifier.width(150.dp)
                        ) {
                            ComboBox(
                                options = Global.getSkillsString(),
                                selectedOption = selectedSkill,
                                onOptionSelected = { selectedSkill = it },
                                errorMessage = skillError,
                                resetErrorMessage = { skillError = "" }
                            )
                        }
                        Spacer(modifier = Modifier.height(35.dp))
                        Row (
                            modifier = Modifier.width(200.dp)
                        ) {
                            CardButton(
                                text = "Done",
                                onClick = {
                                    var works = true
                                    var skillLevelSafe: Int = -1

                                    if (skillLevel.isBlank()) {
                                        skillLevelError = "Skill Level cannot be blank!"
                                        works = false
                                    } else if (skillLevel.toIntOrNull() == null) {
                                        skillLevelError = "Skill Level must be an Integer!"
                                        works = false
                                    } else {
                                        skillLevelSafe = skillLevel.toInt()
                                    }

                                    val skill = Global.getSkillFromString(selectedSkill)

                                    if (skill == null) {
                                        works = false
                                        skillError = "Invalid Skill!"
                                    }

                                    val condition: Condition? = when (selectedCondition) {
                                        AT_LEAST.typeName -> Condition(
                                            AT_LEAST,
                                            listOf(skillLevelSafe)
                                        )

                                        AT_MOST.typeName -> Condition(
                                            AT_MOST,
                                            listOf(skillLevelSafe)
                                        )

                                        else -> null.also{
                                            works = false
                                            conditionError = "Invalid Condition!"
                                        }
                                    }

                                    if (works) {
                                        if (!prerequisites.containsKey(skill!!)) prerequisites[skill] = ArrayList()
                                        prerequisites[skill]!!.add(condition!!)
                                        addingPreReq = false
                                    }
                                },
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = Color.Black
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(35.dp))
                    }
                }
            }
        } else if (selectingColor) {
            BasicAlertDialog(
                onDismissRequest = {
                    selectingColor = false
                }
            ) {
                ElevatedCard (
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.onBackground
                    )
                ) {
                    Column (
                        modifier = Modifier.padding(50.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "Select Role Color",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Column(
                            modifier = Modifier.fillMaxWidth().height(300.dp)
                        ) {
                            HsvColorPicker(
                                modifier = Modifier,
                                controller = controller,
                                onColorChanged = {
                                    selectedHue = it.color
                                    selectedColor = selectedHue.applyBrightness(1 - brightness)
                                },
                                initialColor = selectedHue,
                                drawOnPosSelected = {
                                    drawCircle(
                                        color = selectedHue,
                                        radius = 20.0f,
                                        center = controller.selectedPoint.value
                                    )
                                    drawCircle(
                                        color = Color.Black,
                                        radius = 20.0f,
                                        center = controller.selectedPoint.value,
                                        style = Stroke(4f)
                                    )
                                }
                            )
                        }
                        BrightnessSelector(
                            modifier = Modifier.fillMaxWidth(),
                            selectedColor = selectedHue,
                            brightness = brightness,
                            onBrightnessChange = {
                                brightness = it
                                selectedColor = selectedHue.applyBrightness(1 - brightness)
                            }
                        )
                        Spacer(modifier = Modifier.height(30.dp))
                        Row (
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Preview:", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(20.dp))
                            EventRoleRender(
                                EventRole("New Role", selectedColor.toList(), 0)
                            )
                        }
                        Spacer(modifier = Modifier.height(30.dp))

                        CardButton(
                            text = "Done",
                            onClick = { selectingColor = false },
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.tertiary
                            )
                        )
                    }
                }
            }
        }

        Column (
            modifier = Modifier
                .padding(16.dp)
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            LazyColumn (
                modifier = Modifier.weight(1f)
            ) {
                item {
                    Text(
                        text = "Role Name",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 15.dp)
                    )
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            nameError = false
                        },
                        placeholder = { Text("Enter Role Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clearFocusOnKeyboardDismiss(),
                        supportingText = {
                            if (nameError) {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = "Name cannot be empty!",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        trailingIcon = {
                            if (nameError) {
                                Icon(
                                    Icons.Rounded.Warning,
                                    "",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                        )
                    )
                    Text(
                        text = "Role Priority",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(bottom = 5.dp, top = 25.dp)
                    )
                    Text(
                        text = "Role priority refers to how important a role is to an event, with 0 being most important.",
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                    IntegerTextField(
                        fieldName = "Role Priority",
                        onFieldEdit = { priority = it },
                        onError = { priorityError = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clearFocusOnKeyboardDismiss(),
                        default = "0"
                    )
                    Text(
                        text = "Role Color",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(bottom = 5.dp, top = 25.dp)
                    )
                    Row {
                        Text("Selected Role Color: ")
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            imageVector = Icons.Default.Square,
                            contentDescription = "",
                            tint = selectedColor,
                            modifier = Modifier.clickable {
                                selectingColor = true
                                val settings = selectedColor.reverseSettings()
                                selectedHue = settings.first
                                brightness = 1 - settings.second
                            }
                        )
                    }
                }
                item {
                    Text(
                        text = "Role Prerequisites",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(bottom = 5.dp, top = 25.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    for (entry in prerequisites) {
                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.tertiary,
                                contentColor = Color.Black
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row {
                                    Text(text = "Skill: ", fontWeight = FontWeight.Bold)
                                    Text(entry.key.skill)
                                }
                                Text(text = "Requirements:", fontWeight = FontWeight.Bold)
                                for (i in entry.value.indices) {
                                    DisplayRequirement(
                                        prerequisites[entry.key]!!,
                                        i,
                                        { deleteRequirementConfirmationShowing = Pair(entry.key, i) }
                                    )
                                }
                            }
                        }
                    }
                    if (prerequisites.isEmpty()) {
                        ElevatedCard(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.tertiary,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier.height(50.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text("No Prerequisites!")
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            CardButton(
                text = "Add Role Prerequisite",
                onClick = {
                    addingPreReq = true
                    skillLevel = ""
                    selectedSkill = ""
                    selectedCondition = ""
                    skillError = ""
                    skillLevelError = ""
                    conditionError = ""
                },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            )
            Spacer(modifier = Modifier.height(20.dp))

            CardButton(
                text = "Done",
                onClick = {
                    if (roleEditing == -1) {
                        Global.rolesList.add(
                            EventRole(
                                eventRole = name.trim(),
                                priority = priority,
                                color = selectedColor.toList(),
                                prerequisites = HashMap(prerequisites)
                            )
                        )
                    } else {
                        val oldRole = Global.rolesList[roleEditing]
                        oldRole.eventRole = name.trim()
                        oldRole.priority = priority
                        oldRole.color = selectedColor.toList()
                        oldRole.prerequisites = HashMap(prerequisites)
                    }
                    save()
                    safeNavigate("RolesList")
                },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    }
}

@Composable
fun DisplayRequirement(conditions: ArrayList<Condition?>, idx: Int, onClick: () -> Unit = {}, deleteShowing: Boolean = true) {
    Row (verticalAlignment = Alignment.CenterVertically) {
        Text(
            when (idx) {
                conditions.size - 1 -> " • ${conditions[idx]}"
                conditions.size - 2 -> " • ${conditions[idx]}, and"
                else -> " • ${conditions[idx]};"
            }
        )
        if (deleteShowing) {
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = onClick
            ) {
                Icon(Icons.Default.Delete, "")
            }
        }
    }
}