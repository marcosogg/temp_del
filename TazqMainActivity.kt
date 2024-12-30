package ie.setu.tazq

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import ie.setu.tazq.ui.screens.home.HomeScreen
import ie.setu.tazq.ui.theme.TazqTheme

@AndroidEntryPoint
class TazqMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TazqTheme {
                    HomeScreen()
            }
        }
    }
}