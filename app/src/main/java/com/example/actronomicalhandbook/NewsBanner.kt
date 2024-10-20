package com.example.actronomicalhandbook

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.AbsoluteCutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import androidx.navigation.NavHostController

@Composable
internal fun NewsBanner(navController: NavHostController, viewModel: NewsBannerViewModel = viewModel()) {
    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            viewModel.reorderNews()
        }
    }
    Column(modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectHorizontalDragGestures { _, dragAmount ->
                if (dragAmount < 0) {
                    navController.navigate("opengl")
                }
            }
        }
    ) {
        Row(Modifier.weight(1f)) {
            NewsCard(modifier = Modifier.weight(1f), newsItem = viewModel.displayedNews[0], onLike = { viewModel.likeNewsCb(it) })
            NewsCard(modifier = Modifier.weight(1f), newsItem = viewModel.displayedNews[1], onLike = { viewModel.likeNewsCb(it) })
        }
        Row(Modifier.weight(1f)) {
            NewsCard(modifier = Modifier.weight(1f), newsItem = viewModel.displayedNews[2], onLike = { viewModel.likeNewsCb(it) })
            NewsCard(modifier = Modifier.weight(1f), newsItem = viewModel.displayedNews[3], onLike = { viewModel.likeNewsCb(it) })
        }
    }
}

@Composable
private fun NewsCard(
    modifier: Modifier = Modifier,
    newsItem: NewsItem,
    onLike: (NewsItem) -> Unit
) {
    Card(
        shape = AbsoluteCutCornerShape(10.dp),
        modifier = modifier
            .fillMaxSize()
            .padding(2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.DarkGray)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Box(
                    modifier = Modifier.weight(9f)
                ) {
                    Column {
                        Text(
                            text = newsItem.summary,
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(10.dp)
                        )
                        Text(
                            text = newsItem.description,
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .background(Color.Transparent, RoundedCornerShape(13.dp))
                        .border(5.dp, Color.LightGray, RoundedCornerShape(15.dp))
                        .width(80.dp)
                        .weight(1f)
                        .clickable { onLike(newsItem) }
                        .padding(10.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = "${newsItem.likeCount} \uD83D\uDC7D",
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
