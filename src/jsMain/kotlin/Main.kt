import androidx.compose.runtime.*
import org.jetbrains.compose.html.*
import org.jetbrains.compose.html.impl.ComposeHtmlInternal
import org.jetbrains.compose.html.style.*
import kotlinx.browser.document
import kotlinx.dom.clearChildren
import web.cssom.*

fun main() {
    ComposeHtmlInternal.renderComposable(rootElementId = "root") {
        CalculatorApp()
    }
}

@Composable
fun CalculatorApp() {
    var display by remember { mutableStateOf("0") }
    var previousValue by remember { mutableStateOf("") }
    var operation by remember { mutableStateOf("") }
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
        } else if ("." !in display) {
            display += "."
        }
    }

    fun clear() {
        display = "0"
        previousValue = ""
        operation = ""
        waitingForNewValue = false
    }

    fun performOperation(nextOperation: String) {
        val currentValue = display.toDoubleOrNull() ?: 0.0

        if (operation.isNotEmpty() && !waitingForNewValue) {
            val prev = previousValue.toDoubleOrNull() ?: 0.0
            val result = when (operation) {
                "+" -> prev + currentValue
                "-" -> prev - currentValue
                "×" -> prev * currentValue
                "÷" -> if (currentValue != 0.0) prev / currentValue else { display = "Error"; return }
                else -> currentValue
            }
            display = if (result == result.toInt().toDouble()) result.toInt().toString() else "%.2f".format(result)
        }

        previousValue = display
        operation = nextOperation
        waitingForNewValue = true
    }

    fun calculate() {
        performOperation(operation)
        operation = ""
    }

    style {
        css {
            "body" {
                margin = "0"
                padding = "0"
                backgroundColor = Color("#111")
                display = Display.flex
                justifyContent = JustifyContent.center
                alignItems = AlignItems.center
                height = 100.vh
                fontFamily = "Arial, sans-serif"
            }
            "#root" {
                width = 320.px
                backgroundColor = Color("#222")
                borderRadius = 16.px
                padding = 20.px
                boxShadow = "0 10px 30px rgba(0,0,0,0.5)"
                userSelect = "none"
            }
            ".display" {
                backgroundColor = Color("#000")
                color = Color.white
                fontSize = 48.px
                textAlign = TextAlign.right
                padding = 20.px
                borderRadius = 8.px
                marginBottom = 20.px
                minHeight = 60.px
                display = Display.flex
                alignItems = AlignItems.center
                justifyContent = JustifyContent.flexEnd
            }
            ".button-row" {
                display = Display.grid
                gridTemplateColumns = "repeat(4, 1fr)"
                gap = 12.px
                marginBottom = 12.px
            }
            ".button" {
                backgroundColor = Color("#333")
                color = Color.white
                fontSize = 32.px
                borderRadius = 50.px
                border = Border.none
                padding = 20.px
                cursor = Cursor.pointer
                transition = "all 0.2s"
                onPointerOver { style { backgroundColor = Color("#555") } }
                onPointerOut { style { backgroundColor = Color("#333") } }
            }
            ".button:hover" {
                backgroundColor = Color("#555")
            }
            ".operator" {
                backgroundColor = Color("#ff9500")
            }
            ".operator:hover" {
                backgroundColor = Color("#e68900")
            }
            ".clear" {
                backgroundColor = Color("#a6a6a6")
            }
            ".clear:hover" {
                backgroundColor = Color("#8e8e8e")
            }
            ".zero" {
                gridColumn = "1 / span 2"
            }
            ".equals" {
                backgroundColor = Color("#ff9500")
            }
        }
    }

    div(id = "root") {
        div {
            attrs {
                classes += "display"
            }
            +display
        }

        val buttons = listOf(
            listOf("C", "±", "%", "÷"),
            listOf("7", "8", "9", "×"),
            listOf("4", "5", "6", "-"),
            listOf("1", "2", "3", "+"),
            listOf("0", ".", "=")
        )

        buttons.forEach { row ->
            div {
                attrs {
                    classes += "button-row"
                }
                row.forEach { label ->
                    val isZero = label == "0"
                    val isOperator = label in listOf("÷", "×", "-", "+", "=")
                    val isClear = label == "C"
                    val isEquals = label == "="

                    button(label) {
                        attrs {
                            classes += listOfNotNull(
                                "button",
                                if (isZero) "zero" else null,
                                if (isOperator) "operator" else null,
                                if (isClear) "clear" else null,
                                if (isEquals) "equals" else null
                            )
                            if (isZero) style { gridColumn = "1 / span 2" }
                            onClick {
                                when (label) {
                                    "C" -> clear()
                                    "=" -> calculate()
                                    "÷", "×", "-", "+" -> performOperation(label)
                                    "." -> inputDecimal()
                                    else -> inputNumber(label)
                                }
                            }
                        }
                        +label
                    }
                }
            }
        }
    }
}
