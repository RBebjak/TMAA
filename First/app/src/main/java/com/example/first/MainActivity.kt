package com.example.first

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.first.ui.theme.FirstTheme
import kotlinx.serialization.Serializable
import com.google.maps.android.compose.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Log.i("TAG", "onCreate")
        setContent {
            MyAppNavigation()
        }
    }
}

@Serializable
object LoginScreenRoute

@Serializable
data class FirstScreenRoute(val userId: String)

@Serializable
data class SecondScreenRoute(val userId: String)

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MyAppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = LoginScreenRoute) {

        composable<LoginScreenRoute> {
            LoginScreen(
                onNavigate = { userId ->
                    navController.navigate(FirstScreenRoute(userId))
                }
            )
        }

        composable<FirstScreenRoute> { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            FirstScreen(userId = userId, { userId ->

                navController.navigate(SecondScreenRoute(userId))
            },onLogout = {navController.navigate(LoginScreenRoute)})
        }

        composable<SecondScreenRoute> {
            SecondScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun LoginScreen(onNavigate: (String) -> Unit) {
    var textInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.app_name) ,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = textInput,
            onValueChange = { textInput = it },
            label = { Text("Zadejte Meno") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (textInput.isNotBlank()) {
                    onNavigate(textInput)
                }
            }
        ) {
            Text("Prihlasit")
        }
    }
}

@Composable
fun FirstScreen(userId: String, onNavigate: (String) -> Unit, onLogout: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Meno:",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = userId,
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.primary
        )

        Button(
            onClick = { onNavigate(userId) }
        ) {
            Text(
                text = ("Zmenit meno")
            )
        }

        Button(
            onClick = { onLogout() }
        ) {
            Text(
                text = "Odhlasit",
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
@Composable
fun SecondScreen(onBack: () -> Unit) {

    val context = LocalContext.current

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    val location = LatLng(49.5938, 17.2509)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(location, 12f)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        GoogleMap(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = true
            )
        ) {

            Marker(
                state = MarkerState(position = location),
                title = "Example location"
            )
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            onClick = { onBack() }
        ) {
            Text("Back")
        }
    }
}