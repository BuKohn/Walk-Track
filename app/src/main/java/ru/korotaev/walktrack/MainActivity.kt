package ru.korotaev.walktrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ru.korotaev.walktrack.ui.theme.WalkTrackTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WalkTrackTheme {
                WalkTrackApp()
            }
        }
    }
}

@PreviewScreenSizes
@Composable
fun WalkTrackApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val insets = WindowInsets.systemBars
    val imeInsets = WindowInsets.ime

    // Показываем навигацию только на основных экранах
    val showNavigationBar = currentRoute in listOf(
        ScreenRoutes.WALK,
        ScreenRoutes.PROFILE,
        ScreenRoutes.HISTORY
    )

    if (showNavigationBar) {
        NavigationSuiteScaffold(
            containerColor = Color.White,
            contentColor = Color.Black,
            modifier = Modifier.background(Color.White).windowInsetsPadding(insets),
            navigationSuiteItems = {
                AppDestinations.entries.forEach { destination ->
                    val selected = destination.route == currentRoute
                    item(
                        icon = {
                            Icon(
                                destination.icon,
                                contentDescription = destination.label,
                                tint = if (selected) Color.Red else Color.Gray
                            )
                        },
                        label = { Text(
                            destination.label,
                            color = if (selected) Color.Red else Color.Gray
                        ) },
                        selected = selected,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.startDestinationId){
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        ) {
            WalkTrackContent(navController = navController, currentRoute = currentRoute, insets = insets)
        }
    } else {
        // Для WalkingScreen показываем контент без навигации
        WalkTrackContent(navController = navController, currentRoute = currentRoute, insets = insets)
    }
}

@Composable
fun WalkTrackContent(
    navController: NavController,
    currentRoute: String?,
    insets: WindowInsets
) {
    Box(modifier = Modifier
        .fillMaxSize()
        .windowInsetsPadding(insets)
        .background(
            brush = Brush.verticalGradient(
                colors = listOf(Color.White, Color(0xFFE0E0E0)),
                startY = 0f,
                endY = Float.POSITIVE_INFINITY
            )
        )
    ) {
        NavHost(
            navController = navController as NavHostController,
            startDestination = ScreenRoutes.WALK
        ) {
            composable(ScreenRoutes.WALK) {
                WalkScreen(
                    onStartWalk = { navController.navigate(ScreenRoutes.WALKING) }
                )
            }

            composable(ScreenRoutes.WALKING) {
                WalkingScreen(
                    onWalkEnd = { navController.popBackStack() }
                )
            }

            composable(ScreenRoutes.PROFILE) {
                ProfileScreen()
            }

            composable(ScreenRoutes.HISTORY) {
                HistoryScreen(
                    onWalkItemClick = { item ->
                        navController.navigate("${ScreenRoutes.WALK_INFO}/${item.steps}/${item.goal}/${item.date}/${item.time}")
                    }
                )
            }
            composable(
                route = "${ScreenRoutes.WALK_INFO}/{steps}/{goal}/{date}/{time}",
                arguments = listOf(
                    navArgument("steps") { type = NavType.IntType },
                    navArgument("goal") { type = NavType.IntType },
                    navArgument("date") { type = NavType.StringType },
                    navArgument("time") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val steps = backStackEntry.arguments?.getInt("steps") ?: 0
                val goal = backStackEntry.arguments?.getInt("goal") ?: 0
                val date = backStackEntry.arguments?.getString("date") ?: ""
                val time = backStackEntry.arguments?.getString("time") ?: ""

                WalkInfoScreen(
                    walkItem = WalkHistoryItem(date, time, steps, goal),
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}

object ScreenRoutes {
    const val PROFILE = "profile_screen"
    const val WALK = "walk_screen"
    const val HISTORY = "history_screen"
    const val WALKING = "walking_screen"
    const val WALK_INFO = "walk_info_screen"
}

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
    val route: String
) {
    PROFILE("Профиль", Icons.Default.Home, ScreenRoutes.PROFILE),
    WALK("Прогулка", Icons.AutoMirrored.Filled.DirectionsWalk, ScreenRoutes.WALK),
    HISTORY("История", Icons.AutoMirrored.Filled.ListAlt, ScreenRoutes.HISTORY),
}

@Composable
fun WalkScreen(onStartWalk: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Title(
            modifier = Modifier
                .align(Alignment.Start)
        )
        Spacer(modifier = Modifier.weight(50f))
        Weather(modifier = Modifier)
        Spacer(modifier = Modifier.weight(70f))
        StartButton(modifier = Modifier, onClick = onStartWalk)
        Spacer(modifier = Modifier.weight(90f))
        LastWalk(modifier = Modifier)
        Spacer(modifier = Modifier.weight(15f))
    }
}

@Composable
fun Title(
    modifier: Modifier = Modifier,
    text: String = "Прогулка"
){
    Box(modifier = modifier
        .padding(10.dp, 20.dp)
    ) {
        Text(
            text,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun Weather(
    modifier: Modifier = Modifier,
    degrees: String = "+23°"
){
    Surface(modifier = modifier
        .width(100.dp)
        .height(40.dp),
        shape = RoundedCornerShape(50.dp),
        color = Color.LightGray,
        shadowElevation = 4.dp
    ) {
        Row(modifier = Modifier
            .fillMaxSize()
            .padding(5.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.WbSunny,
                contentDescription = "Погода",
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                degrees,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
fun StartButton(
    modifier: Modifier = Modifier, onClick: () -> Unit
){
    Box(
        modifier = modifier
    ) {
        Button(onClick = onClick,
            shape = CircleShape,
            modifier = Modifier
                .size(200.dp)
                .dropShadow(
                    shape = CircleShape,
                    shadow = Shadow(radius = 15.dp, spread = 5.dp, color = Color.LightGray)
                ),
            colors = ButtonColors(
                Color.LightGray,
                contentColor = Color.Black,
                disabledContainerColor = Color.Gray,
                disabledContentColor = Color.LightGray
            )
        ) {
            Text("Начать", fontSize = 26.sp)
        }
    }
}

@Composable
fun LastWalk(modifier: Modifier = Modifier
){
    Surface(
        modifier = modifier
            .width(230.dp)
            .height(100.dp)
            .dropShadow(
                shape = RoundedCornerShape(4.dp),
                shadow = Shadow(radius = 15.dp, spread = 5.dp, color = Color.LightGray)
            ),
        color = Color.White,
        shape = RoundedCornerShape(5.dp)
    ) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(7.dp)) {
            Text("Прошлая прогулка", fontSize = 13.sp, modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 3.dp),
                fontWeight = FontWeight.Bold)
            Text("3223 / 4000 шагов", fontSize = 13.sp, modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 5.dp))
            Text("февр. 11, 2026", fontSize = 7.sp, modifier = Modifier.align(Alignment.End))
            Text("16:23", fontSize = 7.sp, modifier = Modifier.align(Alignment.End))
        }
    }
}