package ie.setu.tazq.ui.screens.familygroup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ie.setu.tazq.data.model.Invitation
import ie.setu.tazq.ui.viewmodel.InvitationViewModel

@Composable
fun InvitationsScreen(
    invitationViewModel: InvitationViewModel = hiltViewModel()
) {
    val invitations by invitationViewModel.invitations.collectAsState()
    var isFetching by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        invitationViewModel.fetchInvitations()
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Your Invitations", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                isFetching = true
                invitationViewModel.fetchInvitations()
                isFetching = false
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Refresh")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isFetching) {
            CircularProgressIndicator()
        } else if (invitations.isEmpty()) {
            Text("No pending invitations.")
        } else {
            LazyColumn {
                items(invitations) { invitation ->
                    InvitationItem(invitation, invitationViewModel)
                }
            }
        }
    }
}

@Composable
fun InvitationItem(invitation: Invitation, viewModel: InvitationViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Invitation to join: ${invitation.groupId}") // Replace with group name if available
            Text("From: ${invitation.senderId}") // Replace with sender's name if available

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(
                    onClick = { viewModel.acceptInvitation(invitation.invitationId, invitation.groupId) },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text("Accept")
                }
                Button(
                    onClick = { viewModel.declineInvitation(invitation.invitationId) },
                    modifier = Modifier.padding(end = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                ) {
                    Text("Decline", color = Color.Black)
                }
            }
        }
    }
}
