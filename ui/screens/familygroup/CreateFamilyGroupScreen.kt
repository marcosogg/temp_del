package ie.setu.tazq.ui.screens.familygroup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ie.setu.tazq.ui.viewmodel.FamilyGroupViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateFamilyGroupScreen(
    onGroupCreated: () -> Unit = {},
    viewModel: FamilyGroupViewModel = hiltViewModel(),
) {
    var groupName by remember { mutableStateOf("") }
    var isNameValid by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = groupName,
            onValueChange = {
                groupName = it
                isNameValid = it.isNotBlank() // Basic validation: not empty
                showError = it.isBlank()
            },
            label = { Text("Family Group Name") },
            isError = showError,
            modifier = Modifier.fillMaxWidth()
        )
        if (showError) {
            Text("Name cannot be empty", color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (isNameValid) {
                    viewModel.createFamilyGroup(groupName)
                    onGroupCreated()
                } else {
                    showError = true
                }
            },
            enabled = isNameValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Group")
        }
    }
}
