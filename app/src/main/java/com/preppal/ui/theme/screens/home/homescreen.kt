package com.preppal.ui.theme.screens.home

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.preppal.navigation.ROUTE_CLASS


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(navController: NavHostController) {
    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly) {
        var context= LocalContext.current

        Text(text = "    PrepPal    ",
            color = Color.Cyan,
            fontFamily = FontFamily.Cursive,
            fontSize = 40.sp)
        Spacer(modifier = Modifier.height(100.dp))
        Button(onClick = {
            navController.navigate(ROUTE_CLASS)
        },modifier = Modifier
            .fillMaxWidth()
            .background(Color.Cyan)) {
            Text(text = "  +  ")
        }
        Scaffold (
            topBar = {HomeTopBar ()}
        ) {
            ContentFeed()
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(){
    TopAppBar(
        title =
            {Text("PrepPal")},
        actions = {
            IconButton(onClick = {/*Search*/}) {
                Icon(Icons.Default.Search,
                    contentDescription = null)
            }
            IconButton(onClick = {/*Profile*/}) {
                Icon(Icons.Default.AccountCircle,
                    contentDescription = null)
            }
        }
    )
}
@Composable
fun CategoryTabs(categories: List<String>, selected: Int, onCategorySelected:(Int)-> Unit){
    LazyRow {
        itemsIndexed (categories)
        {index,category ->
            Chip(
                selected= selected == index,
                onClick= {
                    onCategorySelected(index)
                },
                modifier= Modifier.padding(horizontal = 4.dp)
            ) {

            }
        }
    }
}

@Composable
fun ContentFeed(){
    LazyColumn (modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp)
    ){
        item (10){index -> VideoCard(index)}
    }
}
@Composable
fun VideoCard(index:Int){
    Card(
        elevation = CardDefaults.cardElevation(),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth())
    {Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color.DarkGray)
        )
        Text("Video Title $index",
            modifier = Modifier.padding(8.dp))
        Text("Channel Name",
            modifier = Modifier.padding(horizontal = 8.dp))
    }
    }
}

@Preview
@Composable
fun Homepreview() {
    HomeScreen(rememberNavController())
}