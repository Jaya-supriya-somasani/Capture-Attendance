package com.example.userattendence

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.userattendence.ui.theme.UserAttendenceTheme
import com.example.userattendence.user_details.UserDetails

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UserAttendenceTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    UserDetails(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}