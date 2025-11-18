import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.web.renderComposable

@Composable
fun CalculatorApp() {
    var display by remember { mutableStateOf("0") }
    var previousValue by remember { mutableStateOf<Double?>(null) }
    var operation by remember { mutableStateOf<String?>(null) }
    var waitingForNewValue by remember { mutableStateOf(false) }

    fun inputNumber(num: String) {
        if (waitingForNewValue) {
            display = num
            waitingForNewValue = false
        } else {
            display = if (display == "0") num else display + num
        }
    }

    fun inputDecimal() {
        if (waitingForNewValue) {
            display = "0."
            waitingForNewValue = false
        } else if (!display.contains(".")) {
            display += "."
        }
    }

    fun clear() {
        display = "0"
        previousValue = null
        operation = null
        waitingForNewValue = false
    }

    fun performOperation(nextOperation: String) {
        val currentValue = display.toDoubleOrNull() ?: 0.0

        if (operation != null && previousValue != null && !waitingForNewValue) {
            val prev = previousValue!!
            val result = when (operation) {
                "+" -> prev + currentValue
                "-" -> prev - currentValue
                "×" -> prev * currentValue
                "÷" -> if (currentValue != 0.0) prev / currentValue else { display = "Error"; return }
                else -> currentValue
            }
            display = if (result % 1 == 0.0) result.toInt().toString() else result.toString()
        }

        previousValue = currentValue
        operation = nextOperation
        waitingForNewValue = true
    }

    fun calculate() {
        if (operation != null && previousValue != null) {
            performOperation(operation!!)
            operation = null
            previousValue = null
            waitingForNewValue = false
        }
    }

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF111111)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Display
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(
                            text = display,
                            fontSize = 48.sp,
                            color = Color.White,
                            textAlign = TextAlign.End,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Buttons Grid
                val buttonGrid = listOf(
                    listOf("C", "±", "%", "÷"),
                    listOf("7", "8", "9", "×"),
                    listOf("4", "5", "6", "-"),
                    listOf("1", "2", "3", "+"),
                    listOf("0", ".", "=")
                )

                buttonGrid.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        row.forEach { label ->
                            val isZero = label == "0"
                            val width = if (isZero) 2f else 1f
                            val isOperator = label in listOf("÷", "×", "-", "+", "=")
                            val isClear = label == "C"

                            Box(
                                modifier = Modifier
                                    .weight(width)
                                    .aspectRatio(1f)
                                    .clip(CircleShape)
                                    .background(
                                        if (isOperator) Color(0xFFFF9500)
                                        else if (isClear) Color(0xFFA6A6A6)
                                        else Color(0xFF333333)
                                    )
                                    .clickable {
                                        when (label) {
                                            "C" -> clear()
                                            "±" -> {} // Implement if needed
                                            "%" -> {} // Implement if needed
                                            "." -> inputDecimal()
                                            "=", "+", "-", "×", "÷" -> if (label == "=") calculate() else performOperation(label)
                                            else -> inputNumber(label)
                                        }
                                    }
                            ) {
                                Text(
                                    text = label,
                                    color = Color.White,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                    }
                    if (row != buttonGrid.last()) {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

fun main() {
    renderComposable("root") {
        CalculatorApp()
    }
}
