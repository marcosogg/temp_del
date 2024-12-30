package ie.setu.tazq.ui.screens.home

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ie.setu.tazq.navigation.About
import ie.setu.tazq.navigation.Invitations
import ie.setu.tazq.navigation.Login
import ie.setu.tazq.navigation.MyFamilyGroups
import ie.setu.tazq.navigation.NavHostProvider
import ie.setu.tazq.navigation.Profile
import ie.setu.tazq.navigation.TaskList
import ie.setu.tazq.navigation.allDestinations
import ie.setu.tazq.ui.components.general.BottomAppBarProvider
import ie.setu.tazq.ui.components.general.TopAppBarProvider
import ie.setu.tazq.ui.theme.TazqTheme

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController(),
) {
    val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentNavBackStackEntry?.destination
    val currentBottomScreen = allDestinations.find {
        it.route == currentDestination?.route
    } ?: Login // Default to Login if no destination is found
    val userId = homeViewModel.currentUser?.uid ?: ""

    var startDestination = currentBottomScreen
    val isActiveSession = homeViewModel.isAuthenticated()

    if (isActiveSession) startDestination = TaskList

    var showMenu by remember { mutableStateOf(false) } // Add this for the overflow menu

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBarProvider(
                currentScreen = currentBottomScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
                actions = { // Add actions parameter here to add items to the TopAppBar
                    if (isActiveSession && currentBottomScreen != About && currentBottomScreen != Profile) {
                        IconButton(onClick = { showMenu = !showMenu }) {
                            Icon(
                                imageVector = Icons.Filled.MoreVert,
                                contentDescription = "More",
                                tint = Color.White
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Invitations", color = MaterialTheme.colorScheme.onSurface) },
                                onClick = {
                                    navController.navigate(Invitations.route)
                                    showMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Profile", color = MaterialTheme.colorScheme.onSurface) },
                                onClick = {
                                    navController.navigate(Profile.route)
                                    showMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("My Family Groups", color = MaterialTheme.colorScheme.onSurface) },
                                onClick = {
                                    navController.navigate(MyFamilyGroups.route)
                                    showMenu = false
                                }
                            )
                        }
                    }
                }
            )
        },
        content = { paddingValues ->
            NavHostProvider(
                modifier = modifier,
                navController = navController,
                startDestination = startDestination,
                paddingValues = paddingValues,
                userId = userId
            )
        },
        bottomBar = {
            if (isActiveSession) {
                BottomAppBarProvider(
                    navController = navController,
                    currentScreen = currentBottomScreen,
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun MyAppPreview() {
    TazqTheme {
        HomeScreen(modifier = Modifier)
    }
}
