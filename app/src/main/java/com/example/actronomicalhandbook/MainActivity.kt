package com.example.actronomicalhandbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import android.content.Context
import android.opengl.GLSurfaceView
import androidx.compose.foundation.background
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                MainNavRouter()
            }
        }
    }
}

@Composable
fun MainNavRouter() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "news") {
        composable("news") { NewsBanner(navController) }
        composable("opengl") { OpenGLScreen(navController) }
        composable("moon_info/{selectedPlanetIndex}") { backStackEntry ->
            val selectedPlanetIndex = backStackEntry.arguments?.getString("selectedPlanetIndex")?.toInt() ?: 0
            InfoScreen(selectedPlanetIndex = selectedPlanetIndex)
        }
    }
}

class MyGLSurfaceView(context: Context, private val renderer: OpenGLRenderer) : GLSurfaceView(context) {
    init {
        setEGLContextClientVersion(2)
        setRenderer(renderer)
    }

    fun setSelectedPlanet(index: Int) {
        renderer.setSelectedObjectIndex(index)
    }
}

@Composable
fun OpenGLScreen(navController: NavController) {
    var selectedPlanetIndex by remember { mutableStateOf(0) }
    val planetCount = 10

    val context = LocalContext.current
    val renderer = remember { OpenGLRenderer(context) }

    Box() {
        AndroidView(
            factory = { ctx ->
                MyGLSurfaceView(ctx, renderer).apply {
                    setSelectedPlanet(selectedPlanetIndex)
                }
            },
            update = { view ->
                view.setSelectedPlanet(selectedPlanetIndex)
            },
            modifier = Modifier.fillMaxSize()
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(color = Color.Transparent),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(onClick = {
            selectedPlanetIndex =
                if (selectedPlanetIndex - 1 < 0) planetCount - 1 else selectedPlanetIndex - 1
        }) {
            Text("Влево")
        }

        Button(onClick = {
            navController.navigate("moon_info/$selectedPlanetIndex")
        }) {
            Text("Информация")
        }

        Button(onClick = {
            selectedPlanetIndex = (selectedPlanetIndex + 1) % planetCount
        }) {
            Text("Вправо")
        }
    }
}