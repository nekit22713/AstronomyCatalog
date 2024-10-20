package com.example.actronomicalhandbook

import android.content.Context
import android.opengl.GLSurfaceView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.sp

@Composable
fun InfoScreen(selectedPlanetIndex: Int) {
    val context = LocalContext.current
    val infoText = getObjectInfo(selectedPlanetIndex)

        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            AndroidView(
                factory = { ctx ->
                    ObjectGLSurfaceView(ctx, selectedPlanetIndex)
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(color = Color.Transparent)
        ) {
            Text(
                text = infoText,
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal,
                color = Color.LightGray,
                textAlign = TextAlign.Left,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }


fun getObjectInfo(index: Int): String {
    return when (index) {
        0 -> "Меркурий — наименьшая планета Солнечной системы и самая близкая к Солнцу. Названа в честь древнеримского бога торговли — быстрого Меркурия, поскольку она движется по небу быстрее других планет. Её период обращения вокруг Солнца составляет всего 87,97 земных суток — самый короткий среди всех планет Солнечной системы."
        1 -> "Вене́ра — вторая по удалённости от Солнца и шестая по размеру планета Солнечной системы, наряду с Меркурием, Землёй и Марсом принадлежащая к семейству планет земной группы. Названа в честь древнеримской богини любви Венеры. По ряду характеристик — например, по массе и размерам — Венера считается «сестрой» Земли."
        2 -> "Земля — третья по удалённости от Солнца планета Солнечной системы. Самая плотная, пятая по диаметру и массе среди всех планет Солнечной системы и крупнейшая среди планет земной группы, в которую входят также Меркурий, Венера и Марс. Единственное известное человеку в настоящее время тело во Вселенной, населённое живыми организмами."
        3 -> "Марс — четвёртая по удалённости от Солнца и седьмая по размеру планета Солнечной системы. Наряду с Меркурием, Венерой и Землёй принадлежит к семейству планет земной группы. Названа в честь Марса — древнеримского бога войны, соответствующего древнегреческому Аресу."
        4 -> "Юпитер — крупнейшая планета Солнечной системы, пятая по удалённости от Солнца. Наряду с Сатурном Юпитер классифицируется как газовый гигант."
        5 -> "Сатурн — шестая планета по удалённости от Солнца и вторая по размерам планета в Солнечной системе после Юпитера. Сатурн классифицируется как газовая планета-гигант. Сатурн назван в честь римского бога земледелия.м"
        6 -> "Уран — планета Солнечной системы, седьмая по удалённости от Солнца, третья по диаметру и четвёртая по массе. Была открыта в 1781 году английским астрономом Уильямом Гершелем и названа в честь греческого бога неба Урана. Уран стал первой планетой, обнаруженной в Новое время и при помощи телескопа."
        7 -> "Нептун — восьмая и самая дальняя от Солнца планета Солнечной системы. Его масса превышает массу Земли в 17,2 раза и является третьей среди планет Солнечной системы, а по экваториальному диаметру Нептун занимает четвёртое место, превосходя Землю в 3,9 раза. Планета названа в честь Нептуна — римского бога морей."
        8 -> "Луна — единственный естественный спутник Земли. Самый близкий к Солнцу спутник планеты, так как у ближайших к Солнцу планет их нет. Второй по яркости объект на земном небосводе после Солнца и пятый по величине естественный спутник планеты Солнечной системы. Среднее расстояние между центрами Земли и Луны — 384 467 км."
        9 -> "Солнце — одна из звёзд нашей Галактики и единственная звезда Солнечной системы. Вокруг Солнца обращаются другие объекты этой системы: планеты и их спутники, карликовые планеты и их спутники, астероиды, метеороиды, кометы и космическая пыль. По спектральной классификации Солнце относится к типу G2V."
        else -> "Неизвестный объект"
    }
}

class ObjectGLSurfaceView(context: Context, private val selectedPlanetIndex: Int) : GLSurfaceView(context) {
    init {
        setEGLContextClientVersion(2)
        setRenderer(PhongRenderer(context, selectedPlanetIndex))
    }
}