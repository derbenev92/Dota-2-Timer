package com.example.dota2timer

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.dota2timer.ui.theme.HeroInfo
import com.example.dota2timer.ui.theme.heroesInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Устанавливаем режим, чтобы контент занимал всю область, включая статус-бар
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Делаем статус-бар прозрачным
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = android.graphics.Color.TRANSPARENT
        }

        // Настраиваем видимость текста и иконок в статус-баре
        val windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = false // Белый текст и иконки

        // Скрываем нижнюю панель
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            windowInsetsController.hide(android.view.WindowInsets.Type.navigationBars()) // Скрыть нижнюю панель
        } else {
            window.decorView.systemUiVisibility = android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }



        setContent {
            RowWithAddButton()
        }
    }
}

@Preview
@Composable
fun RowWithAddButton() {

    var timerState by remember { mutableStateOf(480) }
    var timerText by remember { mutableStateOf("Рошан есть") }
    var displayedTime by remember { mutableStateOf("") }
    var isTimerRunning by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    val rows = remember { mutableStateListOf(Unit) }
    val snackbarHostState = remember { SnackbarHostState() }



    var selectedHero by remember { mutableStateOf(heroesInfo.first()) } // Выбранный герой
    var expanded by remember { mutableStateOf(false) }

    fun resetState() {
        // Сброс таймера на начальное значение
        timerState = 0

        // Сброс строки героев на состояние первого запуска
        rows.clear()


        // Сброс выбранного героя на первого
        selectedHero = heroesInfo.first()


        // Сброс отображаемого времени (например, таймера)
        displayedTime = " "

        // Если есть другие состояния, которые должны сбрасываться (например, состояние кнопок или чекбоксов):
        isTimerRunning = false
        // и т.д.

    }



    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_fon7),
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        // Кнопка сброса в правом верхнем углу
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(30.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            Button(
                onClick = { resetState() },
                ) {
                Text(text = "Сброс", color = Color.White)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    ImageButton(
                        imageResId = R.drawable.ic_ros,
                        onClick = {
                            if (!isTimerRunning) {
                                timerState = 480
                                timerText = "Рошан есть"
                                displayedTime = "08:00"
                                isTimerRunning = true

                                coroutineScope.launch {
                                    while (timerState > 0) {
                                        delay(1000)
                                        timerState--

                                        timerText = when {
                                            timerState > 3 * 60 -> "Рошана нет"
                                            timerState > 0 -> "Рошан мб есть"
                                            else -> "Рошан точно есть"
                                        }

                                        val minutes = timerState / 60
                                        val seconds = timerState % 60

                                        if (timerState >= 0){
                                        displayedTime = String.format("%02d:%02d", minutes, seconds)}
                                        else {
                                            displayedTime = " "
                                        }
                                    }
                                    isTimerRunning = false
                                }
                            } else {
                                timerState = 0
                                timerText = "Рошан есть"
                                displayedTime = " "
                                isTimerRunning = false
                            }
                        }

                    )

                    if (isTimerRunning) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(16.dp)
                        ) {
                            LinearProgressIndicator(
                                progress = timerState / (8 * 60).toFloat(),
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.LightGray)
                            )
                            Canvas(modifier = Modifier.matchParentSize()) {
                                val progressWidth = size.width
                                val markPosition = (3 * 60 / (8 * 60).toFloat()) * progressWidth

                                drawLine(
                                    color = Color.Red,
                                    start = Offset(markPosition, 0f),
                                    end = Offset(markPosition, size.height),
                                    strokeWidth = 4f
                                )
                            }
                        }
                    }

                    Text(
                        text = displayedTime,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Text(
                        text = timerText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }




            }

            rows.forEachIndexed { index, _ ->

                var expanded by remember { mutableStateOf(false) }
                var selectedHero by remember { mutableStateOf(heroesInfo.first()) } // Выбранный герой

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(125.dp)
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Первая кнопка (выбор героя)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable { expanded = true }, // Раскрываем меню при нажатии

                    ) {
                        // Используем размер изображения для области нажатия
                        Image(
                            painter = painterResource(id = selectedHero.iconResId),
                            contentDescription = "Hero Image",
                            modifier = Modifier.fillMaxSize() // Размер картинки и нажатия
                        )


                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            heroesInfo.forEach { hero ->
                                DropdownMenuItem(
                                    text = { Text(text = hero.name) },
                                    onClick = {
                                        selectedHero = hero // Обновляем выбранного героя
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Вторая кнопка
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(2.7f)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            TimerImageButtonBay(size = 100.dp)
                            // Чекбоксы под кнопкой TimerImageButtonUlt
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                                    .alpha(0f), // Отступ между кнопкой и чекбоксами
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                var checkBoxState1 by remember { mutableStateOf(false) }
                                var checkBoxState2 by remember { mutableStateOf(false) }
                                var checkBoxState3 by remember { mutableStateOf(false) }

                                Checkbox(
                                    checked = checkBoxState1,
                                    onCheckedChange = { checkBoxState1 = it },
                                    modifier = Modifier.size(20.dp)
                                )
                                Checkbox(
                                    checked = checkBoxState2,
                                    onCheckedChange = { checkBoxState2 = it },
                                    modifier = Modifier.size(20.dp)
                                )
                                Checkbox(
                                    checked = checkBoxState3,
                                    onCheckedChange = { checkBoxState3 = it },
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    // Третья кнопка
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(2.7f)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            TimerImageButtonBKB(size = 100.dp)
                            // Чекбоксы под кнопкой TimerImageButtonUlt
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                                    .alpha(0f), // Отступ между кнопкой и чекбоксами
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                var checkBoxState1 by remember { mutableStateOf(false) }
                                var checkBoxState2 by remember { mutableStateOf(false) }
                                var checkBoxState3 by remember { mutableStateOf(false) }

                                Checkbox(
                                    checked = checkBoxState1,
                                    onCheckedChange = { checkBoxState1 = it },
                                    modifier = Modifier.size(20.dp)
                                )
                                Checkbox(
                                    checked = checkBoxState2,
                                    onCheckedChange = { checkBoxState2 = it },
                                    modifier = Modifier.size(20.dp)
                                )
                                Checkbox(
                                    checked = checkBoxState3,
                                    onCheckedChange = { checkBoxState3 = it },
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }


                    // Четвертая кнопка с чекбоксами
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(2.7f)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            // Инициализация состояния чекбоксов
                            var checkBoxState1 by remember { mutableStateOf(true) } // Первый чекбокс включен по умолчанию
                            var checkBoxState2 by remember { mutableStateOf(false) }
                            var checkBoxState3 by remember { mutableStateOf(false) }

                            // Подсчет активных чекбоксов
                            val activeCheckBoxes = listOf(checkBoxState1, checkBoxState2, checkBoxState3).count { it }

                            // Передача активных чекбоксов в TimerImageButtonUlt
                            TimerImageButtonUlt(size = 100.dp, selectedHero = selectedHero, activeCheckBoxes = activeCheckBoxes)

                            // Чекбоксы под кнопкой TimerImageButtonUlt
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp), // Отступ между кнопкой и чекбоксами
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Checkbox(
                                    checked = checkBoxState1,
                                    onCheckedChange = { checkBoxState1 = it },
                                    modifier = Modifier.size(20.dp)
                                )
                                Checkbox(
                                    checked = checkBoxState2,
                                    onCheckedChange = { checkBoxState2 = it },
                                    modifier = Modifier.size(20.dp)
                                )
                                Checkbox(
                                    checked = checkBoxState3,
                                    onCheckedChange = { checkBoxState3 = it },
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }


            Button(
                onClick = {
                    if (rows.size < 3) {
                        rows.add(Unit)
                    } else {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Нельзя добавить больше 2 героев")
                        }
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = "Добавить героя")
            }
        }

        // Snackbar host
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

    }
}










@Composable
fun ImageButton(
    imageResId: Int,
    onClick: () -> Unit,
    size: Dp = 200.dp // Размер кнопки, по умолчанию 100dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .clickable(onClick = onClick)
            .background(Color.Transparent), // Фон прозрачный
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = "Button Image",
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun TimerImageButtonUlt(size: Dp, selectedHero: HeroInfo, activeCheckBoxes: Int) {
    var time by remember { mutableStateOf(0) }
    var isRunning by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Убедитесь, что timeModifiers содержит ровно 3 элемента
    val timeModifiers = selectedHero.timeModifiers.take(3)  // Берём только 3 элемента

    // Устанавливаем время в зависимости от activeCheckBoxes
    val maxTime = when (activeCheckBoxes) {
        0, 1 -> timeModifiers[0]  // Для 0 и 1 выбираем первый элемент
        2 -> timeModifiers[1]     // Для 2 выбираем второй элемент
        3 -> timeModifiers[2]     // Для 3 выбираем третий элемент
        else -> timeModifiers[0]  // По умолчанию выбираем первый элемент
    }

    val arrowAngle = 360 * (time / maxTime.toFloat())
    val animatedAngle by animateFloatAsState(targetValue = arrowAngle)

    var isPressed by remember { mutableStateOf(false) }
    val backgroundColor by animateColorAsState(
        targetValue = if (isPressed) Color.Gray else Color(0xFF00FF00),
        animationSpec = tween(durationMillis = 500)
    )

    val borderColor by animateColorAsState(
        targetValue = if (isRunning) Color.Red else Color.Green,
        animationSpec = tween(durationMillis = 300)
    )
    val buttonImage = ImageBitmap.imageResource(id = selectedHero.ultIconResId)

    Box(
        modifier = Modifier
            .size(size)
            .graphicsLayer(scaleX = if (isPressed) 0.9f else 1f, scaleY = if (isPressed) 0.9f else 1f)
            .clipToBounds()
            .background(backgroundColor, RectangleShape)
            .border(BorderStroke(2.dp, borderColor), shape = RectangleShape)
            .clickable(
                onClick = {
                    isPressed = true
                    coroutineScope.launch {
                        delay(100)
                        isPressed = false
                    }
                    if (!isRunning) {
                        time = maxTime
                        isRunning = true
                        coroutineScope.launch {
                            while (time > 0 && isRunning) {
                                time--
                                delay(1000)
                            }
                            isRunning = false
                        }
                    } else {
                        isRunning = false
                    }
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasSize = size.toPx()

            drawImage(
                image = buttonImage,
                dstSize = IntSize(canvasSize.toInt(), canvasSize.toInt())
            )

            drawArc(
                color = Color.Gray.copy(alpha = 0.8f),
                startAngle = 270f,
                sweepAngle = -animatedAngle,
                useCenter = false,
                style = Stroke(width = canvasSize * 1f),
                size = Size(canvasSize, canvasSize),
                topLeft = Offset(0f, 0f)
            )
        }

        if (time > 0) {
            Text(
                text = "$time",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize * 2,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
fun TimerImageButtonBKB(size: Dp) {
    var time by remember { mutableStateOf(0) } // Изначальное значение таймера - 0
    var isRunning by remember { mutableStateOf(false) } // Состояние таймера
    val coroutineScope = rememberCoroutineScope()

    // Угол для дуги (максимум 360 градусов)
    val arrowAngle = 360 * (time / 95f)
    val animatedAngle by animateFloatAsState(targetValue = arrowAngle)

    // Анимация изменения фона
    var isPressed by remember { mutableStateOf(false) }
    val backgroundColor by animateColorAsState(
        targetValue = if (isPressed) Color.Gray else Color(0xFF00FF00), // Серый при нажатии, яркий зеленый в исходном состоянии
        animationSpec = tween(durationMillis = 500) // Плавный переход за 500ms
    )

    // Цвет бордюра: зеленый по умолчанию, красный если таймер запущен
    val borderColor by animateColorAsState(
        targetValue = if (isRunning) Color.Red else Color.Green,
        animationSpec = tween(durationMillis = 300) // Плавный переход
    )

    // Загружаем изображение для кнопки
    val buttonImage = ImageBitmap.imageResource(id = R.drawable.ic_bkb2) // Укажите свой ресурс

    Box(
        modifier = Modifier
            .size(size)
            .graphicsLayer(scaleX = if (isPressed) 0.9f else 1f, scaleY = if (isPressed) 0.9f else 1f) // Анимация нажатия через масштабирование
            .clipToBounds() // Ограничиваем видимость контента рамками Box
            .background(backgroundColor, RectangleShape) // Фон кнопки с анимацией
            .border(
                BorderStroke(2.dp, borderColor), // Граница с динамическим цветом
                shape = RectangleShape // Кнопка квадратной формы
            )
            .clickable(
                onClick = {
                    isPressed = true
                    coroutineScope.launch {
                        delay(100) // Удерживаем состояние нажатия
                        isPressed = false
                    }
                    if (!isRunning) {
                        isRunning = true
                        time = 95 // Устанавливаем таймер на 180 при нажатии
                        coroutineScope.launch {
                            while (time > 0 && isRunning) {
                                time--
                                delay(1000)
                            }
                            isRunning = false
                        }
                    } else {
                        isRunning = false
                    }
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasSize = size.toPx()

            // Рисуем изображение в пределах кнопки
            drawImage(
                image = buttonImage,
                dstSize = IntSize(canvasSize.toInt(), canvasSize.toInt())
            )

            // Рисуем дугу таймера
            drawArc(
                color = Color.Gray.copy(alpha = 0.8f), // Серый цвет с прозрачностью
                startAngle = 270f, // Начинаем с верхней точки
                sweepAngle = -animatedAngle, // Движение против часовой стрелки
                useCenter = false, // Оставляем пустое пространство в центре
                style = Stroke(width = canvasSize * 1f), // Толщина дуги
                size = Size(canvasSize, canvasSize), // Размер дуги совпадает с квадратом
                topLeft = Offset(0f, 0f) // Дуга начинается с верхнего левого угла квадрата
            )
        }

        // Текст таймера (скрываем, если значение равно 0)
        if (time > 0) {
            Text(
                text = "$time",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize * 2,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
fun TimerImageButtonBay(size: Dp) {
    var time by remember { mutableStateOf(0) } // Изначальное значение таймера - 0
    var isRunning by remember { mutableStateOf(false) } // Состояние таймера
    val coroutineScope = rememberCoroutineScope()

    // Угол для дуги (максимум 360 градусов)
    val arrowAngle = 360 * (time / 480f)
    val animatedAngle by animateFloatAsState(targetValue = arrowAngle)

    // Анимация изменения фона
    var isPressed by remember { mutableStateOf(false) }
    val backgroundColor by animateColorAsState(
        targetValue = if (isPressed) Color.Gray else Color(0xFF00FF00), // Серый при нажатии, яркий зеленый в исходном состоянии
        animationSpec = tween(durationMillis = 500) // Плавный переход за 500ms
    )

    // Цвет бордюра: зеленый по умолчанию, красный если таймер запущен
    val borderColor by animateColorAsState(
        targetValue = if (isRunning) Color.Red else Color.Green,
        animationSpec = tween(durationMillis = 300) // Плавный переход
    )

    // Загружаем изображение для кнопки
    val buttonImage = ImageBitmap.imageResource(id = R.drawable.ic_bay) // Укажите свой ресурс

    Box(
        modifier = Modifier
            .size(size)
            .graphicsLayer(scaleX = if (isPressed) 0.9f else 1f, scaleY = if (isPressed) 0.9f else 1f) // Анимация нажатия через масштабирование
            .clipToBounds() // Ограничиваем видимость контента рамками Box
            .background(backgroundColor, RectangleShape) // Фон кнопки с анимацией
            .border(
                BorderStroke(2.dp, borderColor), // Граница с динамическим цветом
                shape = RectangleShape // Кнопка квадратной формы
            )
            .clickable(
                onClick = {
                    isPressed = true
                    coroutineScope.launch {
                        delay(100) // Удерживаем состояние нажатия
                        isPressed = false
                    }
                    if (!isRunning) {
                        isRunning = true
                        time = 480 // Устанавливаем таймер на 180 при нажатии
                        coroutineScope.launch {
                            while (time > 0 && isRunning) {
                                time--
                                delay(1000)
                            }
                            isRunning = false
                        }
                    } else {
                        isRunning = false
                    }
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasSize = size.toPx()

            // Рисуем изображение в пределах кнопки
            drawImage(
                image = buttonImage,
                dstSize = IntSize(canvasSize.toInt(), canvasSize.toInt())
            )

            // Рисуем дугу таймера
            drawArc(
                color = Color.Gray.copy(alpha = 0.8f), // Серый цвет с прозрачностью
                startAngle = 270f, // Начинаем с верхней точки
                sweepAngle = -animatedAngle, // Движение против часовой стрелки
                useCenter = false, // Оставляем пустое пространство в центре
                style = Stroke(width = canvasSize * 1f), // Толщина дуги
                size = Size(canvasSize, canvasSize), // Размер дуги совпадает с квадратом
                topLeft = Offset(0f, 0f) // Дуга начинается с верхнего левого угла квадрата
            )
        }

        // Текст таймера (скрываем, если значение равно 0)
        if (time > 0) {
            Text(
                text = "$time",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize * 2,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}








































