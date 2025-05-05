package com.preppal.ui.theme.screens.home

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.viewinterop.AndroidView
import com.preppal.navigation.ROUTE_CLASS
import android.webkit.WebView
import android.webkit.WebViewClient
import java.util.regex.Pattern

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(navController: NavHostController) {
    Scaffold(
        topBar = { HomeTopBar() },
        floatingActionButton = {
            Button(
                onClick = {
                    navController.navigate(ROUTE_CLASS)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan),
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "  +  ")
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "    PrepPal    ",
                color = Color.Cyan,
                fontFamily = FontFamily.Cursive,
                fontSize = 40.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            ContentFeed()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar() {
    TopAppBar(
        title = { Text("PrepPal") },
        actions = {
            IconButton(onClick = {/*Search*/ }) {
                Icon(Icons.Default.Search, contentDescription = null)
            }
            IconButton(onClick = {/*Profile*/ }) {
                Icon(Icons.Default.AccountCircle, contentDescription = null)
            }
        }
    )
}

@Composable
fun CategoryTabs(categories: List<String>, selected: Int, onCategorySelected: (Int) -> Unit) {
    LazyRow {
        itemsIndexed(categories) { index, category ->
            FilterChip(
                selected = selected == index,
                onClick = { onCategorySelected(index) },
                label = { Text(category) },
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}

@Composable
fun ContentFeed() {
    val videoList = listOf(
        YouTubeVideo("mL9vnNW2h0Q", "Lofi Quran: Ultimate stress relief - relaxation - Study Session: Healing Frequencies", "Omar Hisham Al Arabi"),
        YouTubeVideo("aPjojnJbyiE", "Kotlin Tutorial: How to play youtube video in android app", "Let's finish this App"),
        YouTubeVideo("5MgBikgcWnY", "How to Learn Faster with the Feynman Technique", "Ali Abdaal"),
        YouTubeVideo("ZVMw9TLrmA", "Form 3 - Kiswahili - Topic : (Fasihi) - Ushairi - Mr Timothy Enzoya", "Dacwa TV"),
        YouTubeVideo("vdE6u50Z8XM", "Form 4 - IRE - Topic Qur'an (PP1 Full Revision), By; Tr Abdirahman Ahmed", "Dacwa TV"),
        YouTubeVideo("58nL_QzvlXo", "Form 4 Business Studies Topic Source Documents & Books of Original Entry Mr Bakari Ayub", "Dacwa TV"),
        YouTubeVideo("eg03Cg8zv78", "Form 3/4 - Math - Quadratic Mr. Joseph Opati", "Dacwa TV"),
        YouTubeVideo("cKvwAmYvBZI", "Form 3 - English - Topic: Poetry_Lesson_1", "Dacwa TV")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(videoList) { video ->
            VideoCard(video)
        }
    }
}

data class YouTubeVideo(
    val videoId: String,
    val title: String,
    val channelName: String
)

@Composable
fun VideoCard(video: YouTubeVideo) {
    // Extract video ID (handles full URLs or just IDs)
    val videoId = extractVideoId(video.videoId)

    Card(
        elevation = CardDefaults.cardElevation(),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            if (videoId != null) {
                // This is where the AndroidView goes
                AndroidView(
                    factory = { context ->
                        WebView(context).apply {
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            settings.mediaPlaybackRequiresUserGesture = false
                            webViewClient = WebViewClient()

                            // Important for embedded videos
                            settings.loadWithOverviewMode = true
                            settings.useWideViewPort = true
                        }
                    },
                    update = { webView ->
                        webView.loadUrl("https://www.youtube.com/embed/$videoId?autoplay=0")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp) // Adjust height as needed
                )
            } else {
                // Show placeholder if video ID is invalid
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Video not available", color = Color.White)
                }
            }

            // Video title and channel info
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = video.channelName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}


private fun extractVideoId(url: String): String? {
    // If it's already a clean ID
    if (!url.contains("youtube") && !url.contains("youtu.be") && !url.contains("&") && !url.contains("?")) {
        return url
    }

    // Extract from various URL formats
    val pattern = "(?:youtube\\.com\\/(?:[^\\/]+\\/.+\\/|(?:v|e(?:mbed)?)\\/|.*[?&]v=)|youtu\\.be\\/)([^\"&?\\/\\s]{11})"
    val compiledPattern = Pattern.compile(pattern)
    val matcher = compiledPattern.matcher(url)

    return if (matcher.find()) {
        matcher.group(1)?.take(11) // YouTube IDs are always 11 characters
    } else {
        // Fallback: Take everything before first '&' or '?'
        url.split('&', '?').firstOrNull()
    }
}


val videoList = listOf(
    YouTubeVideo("mL9vnNW2h0Q", "Lofi Quran: Ultimate stress relief - relaxation - Study Session: Healing Frequencies", "Omar Hisham Al Arabi"),
    YouTubeVideo("aPjojnJbyiE", "Kotlin Tutorial: How to play youtube video in android app", "Let's finish this App"),
    YouTubeVideo("5MgBikgcWnY", "How to Learn Faster with the Feynman Technique", "Ali Abdaal"),
    YouTubeVideo("ZVMw9TLrmA", "Form 3 - Kiswahili - Topic : (Fasihi) - Ushairi - Mr Timothy Enzoya", "Dacwa TV"),
    YouTubeVideo("vdE6u50Z8XM", "Form 4 - IRE - Topic Qur'an (PP1 Full Revision), By; Tr Abdirahman Ahmed", "Dacwa TV"),
    YouTubeVideo("58nL_QzvlXo", "Form 4 Business Studies Topic Source Documents & Books of Original Entry Mr Bakari Ayub", "Dacwa TV"),
    YouTubeVideo("eg03Cg8zv78", "Form 3/4 - Math - Quadratic Mr. Joseph Opati", "Dacwa TV"),
    YouTubeVideo("cKvwAmYvBZI", "Form 3 - English - Topic: Poetry_Lesson_1", "Dacwa TV")
)
@Preview
@Composable
fun Homepreview() {
    HomeScreen(rememberNavController())
}
