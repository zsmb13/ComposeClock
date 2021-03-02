package co.zsmb.composeclock

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.zsmb.composeclock.ui.theme.ComposeClockTheme
import kotlinx.coroutines.delay
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeClockTheme {
                Surface(color = MaterialTheme.colors.background) {
                    ClockScreen()
                }
            }
        }
    }
}

data class Time(val hours: Int, val minutes: Int, val seconds: Int)

@Composable
fun ClockScreen() {
    fun currentTime(): Time {
        val cal = Calendar.getInstance()
        return Time(
            hours = cal.get(Calendar.HOUR_OF_DAY),
            minutes = cal.get(Calendar.MINUTE),
            seconds = cal.get(Calendar.SECOND),
        )
    }

    var time by remember { mutableStateOf(currentTime()) }
    LaunchedEffect(0) {
        while (true) {
            time = currentTime()
            delay(1000)
        }
    }

    Clock(time)
}

@Composable
fun Clock(time: Time) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val padding = Modifier.padding(horizontal = 4.dp)

        NumberColumn(time.hours / 10, 0..2, padding)
        NumberColumn(time.hours % 10, 0..9, padding)

        Spacer(Modifier.size(16.dp))

        NumberColumn(time.minutes / 10, 0..5, padding)
        NumberColumn(time.minutes % 10, 0..9, padding)

        Spacer(Modifier.size(16.dp))

        NumberColumn(time.seconds / 10, 0..5, padding)
        NumberColumn(time.seconds % 10, 0..9, padding)
    }
}

@Composable
fun NumberColumn(
    current: Int,
    range: IntRange,
    modifier: Modifier = Modifier,
) {
    val size = 40.dp

    val mid = (range.last - range.first) / 2f
    val reset = current == range.first
    val offset by animateDpAsState(
        targetValue = size * (mid - current),
        animationSpec = if (reset) {
            spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessLow,
            )
        } else {
            tween(
                durationMillis = 300,
                easing = LinearOutSlowInEasing,
            )
        }
    )

    Column(
        modifier
            .offset(y = offset)
            .clip(RoundedCornerShape(percent = 25))
    ) {
        range.forEach { num ->
            Number(num == current, num, Modifier.size(size))
        }
    }
}

@Composable
fun Number(active: Boolean, value: Int, modifier: Modifier = Modifier) {
    val backgroundColor by animateColorAsState(
        if (active) MaterialTheme.colors.primary else MaterialTheme.colors.primaryVariant,
    )

    Box(
        modifier = modifier.background(backgroundColor),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = value.toString(),
            fontSize = 20.sp,
            color = Color.White,
        )
    }
}
