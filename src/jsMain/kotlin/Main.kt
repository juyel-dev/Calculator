import androidx.compose.runtime.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.attributes.*
import org.jetbrains.compose.web.css.*

@Composable
fun Calculator() {
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
            display = if (result == result.toInt().toDouble()) result.toInt().toString() else result.toString()
        }

        previousValue = display
        operation = nextOperation
        waitingForNewValue = true
    }

    fun calculate() {
        performOperation(operation)
        operation = ""
    }

    Div({
        style {
            width(320.px)
            margin(50.px)
            fontFamily("Arial, sans-serif")
            backgroundColor(Color("#222"))
            borderRadius(16.px)
            padding(20.px)
            boxShadow("0 10px 30px rgba(0,0,0,0.5)")
            userSelect("none")
        }
    }) {
        // Display
        Div({
            style {
                backgroundColor(Color("#000"))
                color(Color.white)
                fontSize(48.px)
                textAlign("right")
                padding(20.px)
                borderRadius(8.px)
                marginBottom(20.px)
                minHeight(60.px)
                display("flex")
                alignItems("center")
                justifyContent("flex-end")
            }
        }) {
            Text(display)
        }

        // Buttons
        val buttons = listOf(
            listOf("C", "±", "%", "÷"),
            listOf("7", "8", "9", "×"),
            listOf("4", "5", "6", "-"),
            listOf("1", "2", "3", "+"),
            listOf("0", ".", "=")
        )

        buttons.forEach { row ->
            Div({ style { display("grid"); gridTemplateColumns("repeat(4, 1fr)"); gap(12.px) } }) {
                row.forEach { label ->
                    if (label == "0") {
                        Button({
                            attrs {
                                style {
                                    gridColumn("1 / span 2")
                                    backgroundColor(if (label == "=") Color("#ff9500") else Color("#333"))
                                    color(Color.white)
                                    fontSize(32.px)
                                    borderRadius(50.px)
                                    border("none")
                                    padding(20.px)
                                    cursor("pointer")
                                    transitions("all 0.2s")
                                }
                                onClick {
                                    when (label) {
                                        "C" -> clear()
                                        "=" -> calculate()
                                        "÷", "×", "-", "+" -> performOperation(label)
                                        else -> inputNumber(label)
                                    }
                                }
                            }
                        }) { Text(label) }
                    } else {
                        Button({
                            attrs {
                                style {
                                    backgroundColor(
                                        when (label) {
                                            in listOf("÷", "×", "-", "+", "=") -> Color("#ff9500")
                                            "C" -> Color("#a6a6a6")
                                            else -> Color("#333")
                                        }
                                    )
                                    color(Color.white)
                                    fontSize(32.px)
                                    borderRadius(50.px)
                                    border("none")
                                    padding(20.px)
                                    cursor("pointer")
                                    property("transition", "all 0.2s")
                                }
                                onClick {
                                    when (label) {
                                        "C" -> clear()
                                        "." -> inputDecimal()
                                        "=" -> calculate()
                                        "÷", "×", "-", "+" -> performOperation(label)
                                        else -> inputNumber(label)
                                    }
                                }
                            }
                        }) { Text(label) }
                    }
                }
            }
            Br()
        }
    }
}
