package com.starsoft.skeleton.compose.baseui

import androidx.annotation.IntRange
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import androidx.compose.runtime.State


/**
 * Created by Dmitry Starkin on 04.03.2025 12:05.
 */

@Composable
fun Counter(
        remember: Boolean = false,
        startValue: Int,
        endValue: Int,
        delayMs: Long,
        onEnd: () -> Unit,
        ui:  @Composable (currentValue : Int, counting : Boolean) -> Unit){
    val curValue = if(remember){
        rememberSaveable{ mutableIntStateOf(startValue) }
    } else {
        remember { mutableIntStateOf(startValue) }
    }
    val down = if(remember){
        rememberSaveable{ mutableStateOf(startValue > endValue) }
    } else {
        remember { mutableStateOf(startValue > endValue) }
    }
    fun counting() = if(down.value){curValue.intValue > endValue}else{curValue.intValue < endValue}
    ui(curValue.intValue, counting())
    LaunchedEffect(key1 = startValue, key2 = endValue) {
            while(counting()){
                delay(delayMs)
                if(down.value){
                    curValue.value -= 1
                } else {
                    curValue.value += 1
                }
            }
        onEnd()
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SimpleCircularProgressSpinner(modifier: Modifier = Modifier,
                                  size: Dp = 50.dp,
                                  color: Color = MaterialTheme.colors.secondary,
                                  trackColor: Color = MaterialTheme.colors.surface,
                                  strokeWidth: Dp = ProgressIndicatorDefaults.StrokeWidth,
                                  strokeCap: StrokeCap = StrokeCap.Square,
                                  isVisible: Boolean = false){
    
    if(isVisible){
        Box(
            modifier.background(Color.Transparent)
                .fillMaxSize()
                .pointerInteropFilter {
                    true
                }
        ){
            CircularProgressIndicator(
                modifier
                    .width(size)
                    .align(Alignment.Center),
                color = color,
                strokeWidth = strokeWidth,
                strokeCap = strokeCap,
                backgroundColor = trackColor,
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CircularProgressSpinner(modifier: Modifier = Modifier,
                            size: Dp = 50.dp,
                            color: Color = MaterialTheme.colors.secondary,
                            trackColor: Color = MaterialTheme.colors.surface,
                            strokeWidth: Dp = ProgressIndicatorDefaults.StrokeWidth,
                            strokeCap: StrokeCap = StrokeCap.Square,
                            @IntRange(10 , 1000)
                            speed: Long = 910,
                            isVisible: Boolean = false){
    
    var currentProgress by remember { mutableFloatStateOf(0f) }
    var currentRotation by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(key1 = isVisible) {
        while(isVisible){
            delay(1010L - speed)
            if(currentRotation >= 360f){
                currentRotation = 0f
            }else{
                currentRotation += 30f
            }
            if(currentProgress >= 0.9f){
                currentProgress = 0f
            } else {
                currentProgress += 0.02f
            }
        }
    }
    
    if(isVisible){
        Box(
            modifier.background(Color.Transparent)
                .fillMaxSize()
                .pointerInteropFilter {
                    true
                }
        ){
                CircularProgressIndicator(
                    progress =  currentProgress ,
                    modifier = modifier
                        .width(size)
                        .height(size)
                        .rotate(currentRotation)
                        .align(Alignment.Center),
                    color = color,
                    strokeWidth = strokeWidth,
                    strokeCap = strokeCap,
                    backgroundColor = trackColor,
                )
        }
    }
}

@Composable
fun CircularProgressSpinner(isVisible: State<Boolean>,
                            modifier: Modifier = Modifier,
                            size: Dp = 50.dp,
                            color: Color = MaterialTheme.colors.secondary,
                            trackColor: Color = MaterialTheme.colors.surface,
                            strokeWidth: Dp = ProgressIndicatorDefaults.StrokeWidth,
                            strokeCap: StrokeCap = StrokeCap.Square,
                            @IntRange(10 , 1000)
                            speed: Long = 910) =
    
    
    CircularProgressSpinner(
        modifier,
        size,
        color,
        trackColor,
        strokeWidth,
        strokeCap,
        speed,
        isVisible.value
    )



