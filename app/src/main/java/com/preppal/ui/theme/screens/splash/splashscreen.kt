package com.preppal.ui.theme.screens.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.preppal.navigation.ROUTE_LOGIN
import com.preppal.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    var startAnimation by remember { mutableStateOf(false) }

    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1000), label = ""
    )


    LaunchedEffect(true) {
        startAnimation = true
        delay(2000)
        navController.navigate(ROUTE_LOGIN)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .alpha(alphaAnim),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.pp2),
            contentDescription = "App Logo",
            modifier = Modifier.size(300.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "PrepPal",
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Blue
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    SplashScreen(navController = rememberNavController())
}
