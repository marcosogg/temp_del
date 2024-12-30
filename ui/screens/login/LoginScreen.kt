package ie.setu.tazq.ui.screens.login

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ie.setu.tazq.R
import ie.setu.tazq.firebase.auth.Response
import ie.setu.tazq.navigation.TaskList
import ie.setu.tazq.ui.components.auth.AuthButton
import ie.setu.tazq.ui.components.auth.AuthTextField
import ie.setu.tazq.ui.components.auth.GoogleSignInButton
import ie.setu.tazq.ui.components.auth.PasswordTextField

@Composable
fun LoginScreen(
    onLogin: () -> Unit = {},
    navController: NavController,
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    var isEnabled by remember { mutableStateOf(false) }
    val loginFlow = loginViewModel.loginFlow.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(28.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = stringResource(id = R.string.welcome),
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(40.dp))

                AuthTextField(
                    value = loginViewModel.loginUIState.value.email,
                    onValueChange = {
                        loginViewModel.onEvent(LoginUIEvent.EmailChanged(it))
                    },
                    label = stringResource(id = R.string.email),
                    isError = loginViewModel.loginUIState.value.emailError,
                    errorMessage = stringResource(id = R.string.email_error)
                )

                Spacer(modifier = Modifier.height(16.dp))

                PasswordTextField(
                    value = loginViewModel.loginUIState.value.password,
                    onValueChange = {
                        loginViewModel.onEvent(LoginUIEvent.PasswordChanged(it))
                    },
                    label = stringResource(id = R.string.password),
                    isError = loginViewModel.loginUIState.value.passwordError,
                    errorMessage = stringResource(id = R.string.password_error)
                )

                Spacer(modifier = Modifier.height(32.dp))

                AuthButton(
                    text = stringResource(id = R.string.login),
                    onClick = {
                        loginViewModel.onEvent(LoginUIEvent.LoginButtonClicked)
                        onLogin()
                    },
                    enabled = loginViewModel.allValidationsPassed.value
                )

                Spacer(modifier = Modifier.height(16.dp))

                val context = LocalContext.current
                GoogleSignInButton(
                    onClick = {
                        loginViewModel.signInWithGoogleCredentials(context)
                    }
                )
            }
        }

        loginFlow.value?.let {
            when (it) {
                is Response.Failure -> {
                    val context = LocalContext.current
                    Toast.makeText(context, it.e.message, Toast.LENGTH_LONG).show()
                }
                is Response.Loading -> {
                    CircularProgressIndicator()
                }
                is Response.Success -> {
                    LaunchedEffect(Unit) {
                        navController.navigate(TaskList.route) {
                            popUpTo("login") {
                                inclusive = true
                            }
                        }
                    }
                }
            }
        }
    }
}