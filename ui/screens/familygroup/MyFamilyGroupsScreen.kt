package ie.setu.tazq.ui.screens.familygroup

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import ie.setu.tazq.data.model.FamilyGroup
import ie.setu.tazq.data.model.TaskModel
import ie.setu.tazq.data.model.User
import ie.setu.tazq.ui.viewmodel.FamilyGroupTaskViewModel
import ie.setu.tazq.ui.viewmodel.FamilyGroupViewModel
import kotlinx.coroutines.launch

@Composable
fun MyFamilyGroupsScreen(
    viewModel: FamilyGroupViewModel = hiltViewModel(),
    taskViewModel: FamilyGroupTaskViewModel = hiltViewModel()
) {
    val familyGroups by viewModel.familyGroups.collectAsState()
    val currentUserId = viewModel.currentUser?.uid ?: ""
    var selectedGroup by remember { mutableStateOf<FamilyGroup?>(null) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "My Family Groups", style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold))

        LazyColumn {
            items(familyGroups) { group ->
                FamilyGroupItem(group = group, currentUserId = currentUserId, viewModel = viewModel) {
                    selectedGroup = if (selectedGroup == group) null else group
                }
            }
        }

        // Display tasks for the selected group
        selectedGroup?.let { group ->
            Text("Tasks for ${group.groupName}", modifier = Modifier.padding(top = 16.dp))
            FamilyGroupTaskList(groupId = group.groupId, taskViewModel = taskViewModel)
        }
    }
}

@Composable
fun FamilyGroupItem(
    group: FamilyGroup,
    currentUserId: String,
    viewModel: FamilyGroupViewModel,
    onGroupClick: () -> Unit
) {
    var showRenameDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var showLeaveConfirmation by remember { mutableStateOf(false) }
    var showInviteDialog by remember { mutableStateOf(false) }
    var showViewMembersDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf(group.groupName) }
    var emailToInvite by remember { mutableStateOf("") }
    var members by remember { mutableStateOf<List<User>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable(onClick = onGroupClick),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = group.groupName, style = TextStyle(fontSize = 18.sp))

            Row {
                if (group.adminId == currentUserId) {
                    // Edit (Rename) Button
                    IconButton(onClick = { showRenameDialog = true }) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit")
                    }

                    // Delete Button
                    IconButton(onClick = { showDeleteConfirmation = true }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete")
                    }

                    // Invite Button
                    IconButton(onClick = { showInviteDialog = true }) {
                        Icon(Icons.Filled.PersonAdd, contentDescription = "Invite")
                    }

                    // View Members Button
                    IconButton(onClick = {
                        showViewMembersDialog = true
                        coroutineScope.launch {
                            members = viewModel.getMemberDetails(group.groupId)
                        }
                    }) {
                        Icon(Icons.Filled.People, contentDescription = "View Members")
                    }

                } else {
                    // Leave Group Button
                    IconButton(onClick = { showLeaveConfirmation = true }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Leave Group")
                    }
                }
            }
        }
        // Rename Dialog
        if (showRenameDialog) {
            AlertDialog(
                onDismissRequest = { showRenameDialog = false },
                title = { Text("Rename Group") },
                text = {
                    Column {
                        TextField(
                            value = newName,
                            onValueChange = { newName = it },
                            label = { Text("New Name") }
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        viewModel.updateFamilyGroupName(group.groupId, newName)
                        showRenameDialog = false
                    }) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    Button(onClick = { showRenameDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Delete Confirmation Dialog
        if (showDeleteConfirmation) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmation = false },
                title = { Text("Delete Group") },
                text = { Text("Are you sure you want to delete this group? This action cannot be undone.") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.deleteFamilyGroup(group.groupId)
                            showDeleteConfirmation = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDeleteConfirmation = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Leave Group Confirmation Dialog
        if (showLeaveConfirmation) {
            AlertDialog(
                onDismissRequest = { showLeaveConfirmation = false },
                title = { Text("Leave Group") },
                text = { Text("Are you sure you want to leave this group?") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.leaveFamilyGroup(group.groupId, currentUserId)
                            showLeaveConfirmation = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Leave")
                    }
                },
                dismissButton = {
                    Button(onClick = { showLeaveConfirmation = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Invite Member Dialog
        if (showInviteDialog) {
            AlertDialog(
                onDismissRequest = { showInviteDialog = false },
                title = { Text("Invite Member") },
                text = {
                    Column {
                        TextField(
                            value = emailToInvite,
                            onValueChange = { emailToInvite = it },
                            label = { Text("Email Address") },
                            singleLine = true
                        )
                        // You might want to add email validation here
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.inviteUserToGroup(group.groupId, emailToInvite)
                            showInviteDialog = false
                            emailToInvite = "" // Clear the email field
                        }
                    ) {
                        Text("Send Invitation")
                    }
                },
                dismissButton = {
                    Button(onClick = { showInviteDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // View Members Dialog
        if (showViewMembersDialog) {
            AlertDialog(
                onDismissRequest = { showViewMembersDialog = false },
                title = { Text("Group Members") },
                text = {
                    if (members.isEmpty()) {
                        CircularProgressIndicator() // Show a loading indicator while fetching
                    } else {
                        LazyColumn {
                            items(members) { member ->
                                // Display member information here
                                Text(member.displayName)
                                // Add more details as needed
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = { showViewMembersDialog = false }) {
                        Text("Close")
                    }
                }
            )
        }
    }
}

@Composable
fun FamilyGroupTaskList(groupId: String, taskViewModel: FamilyGroupTaskViewModel) {
    val tasks by taskViewModel.tasks.collectAsState()

    // Fetch tasks for the selected group
    LaunchedEffect(groupId) {
        taskViewModel.fetchTasksForFamilyGroup(groupId)
    }

    if (tasks.isEmpty()) {
        // Show a message if there are no tasks
        Text("No tasks for this group yet.", style = TextStyle(fontSize = 16.sp))
    } else {
        // Display the tasks in a LazyColumn
        LazyColumn(modifier = Modifier.padding(top = 8.dp)) {
            items(tasks) { task ->
                TaskItem(task)
            }
        }
    }
}

@Composable
fun TaskItem(task: TaskModel) {
    // Basic display of task information
    Text(task.title, style = TextStyle(fontSize = 16.sp))
    Spacer(modifier = Modifier.height(4.dp))
}
