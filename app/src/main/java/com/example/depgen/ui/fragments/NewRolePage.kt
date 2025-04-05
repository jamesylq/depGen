package com.example.depgen.ui.fragments

import android.util.Log
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
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.depgen.AT_LEAST
import com.example.depgen.AT_MOST
import com.example.depgen.CONDITION_TYPES
import com.example.depgen.Global
import com.example.depgen.isNotInt
import com.example.depgen.model.Condition
import com.example.depgen.model.EventRole
import com.example.depgen.model.Skill
import com.example.depgen.navController
import com.example.depgen.save
import com.example.depgen.ui.components.CardButton
import com.example.depgen.ui.components.ComboBox
import com.example.depgen.ui.components.ConfirmationScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewRolePage() {
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
    val prerequisites = remember { mutableMapOf<Skill, ArrayList<Condition?>>() }

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
                    navController.navigate("RolesList")
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(650.dp)
                ) {
                    Column (
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(30.dp))
                        Text(
                            text = "Role Prerequisite",
                            fontWeight = FontWeight.Bold,
                            fontSize = 25.sp
                        )
                        Spacer(modifier = Modifier.height(50.dp))
                        Text("Members must have", fontSize = 19.sp)
                        Spacer(modifier = Modifier.height(20.dp))
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
                        Spacer(modifier = Modifier.height(20.dp))
                        Text("a skill level of", fontSize = 19.sp)
                        Spacer(modifier = Modifier.height(20.dp))
                        TextField(
                            skillLevel,
                            {
                                skillLevel = it
                                skillLevelError = if (isNotInt(skillLevel)) "Skill Level must be an Integer!" else ""
                            },
                            modifier = Modifier.width(150.dp),
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
                            }
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Text("in the skill", fontSize = 19.sp)
                        Spacer(modifier = Modifier.height(20.dp))
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
                        Spacer(modifier = Modifier.height(50.dp))
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
                                        Log.wtf("wahoo", "wahoo")
                                        works = false
                                        skillError = "Invalid Skill!"
                                    } else {
                                        Log.wtf("SELECTEDSKILL", selectedSkill)
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
                                    }
                                },
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = Color.Black
                                )
                            )
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
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
                modifier = Modifier.fillMaxWidth(),
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
                }
            )
            Text(
                text = "Role Prerequisites",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(bottom = 5.dp, top = 25.dp)
            )
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                for (entry in prerequisites) {
                    item {
                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.tertiary,
                                contentColor = Color.Black
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Row {
                                    Text(text = "Skill: ", fontWeight = FontWeight.Bold)
                                    Text(entry.key.skill)
                                }
                                Text(text = "Requirements:", fontWeight = FontWeight.Bold)
                                for (i in entry.value.indices) {
                                    DeleteRequirement(
                                        prerequisites[entry.key]!!,
                                        i
                                    ) {
                                        deleteRequirementConfirmationShowing = Pair(entry.key, i)
                                    }
                                }
                            }
                        }
                    }
                }
            }
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
                    Global.rolesList.add(
                        EventRole(
                            eventRole = name,
                            priority = 0,
                            prerequisites = HashMap(prerequisites)
                        )
                    )
                    save()
                },
                colors = CardColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.Black,
                    disabledContainerColor = MaterialTheme.colorScheme.primary,
                    disabledContentColor = Color.Black
                )
            )
        }
    }
}

@Composable
fun DeleteRequirement(conditions: ArrayList<Condition?>, idx: Int, onClick: () -> Unit) {
    Row (verticalAlignment = Alignment.CenterVertically) {
        //TODO: Replace with CircleShape
        Text(
            when (idx) {
                conditions.size - 1 -> " • ${conditions[idx]}"
                conditions.size - 2 -> " • ${conditions[idx]}, and"
                else -> " • ${conditions[idx]};"
            }
        )
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            onClick = onClick
        ) {
            Icon(Icons.Default.Delete, "")
        }
    }
}