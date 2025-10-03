package com.example.tugaskalkulator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlin.math.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                CalculatorScreen()
            }
        }
    }
}

private fun formatResult(result: Double): String {
    if (result.isInfinite() || result.isNaN()) return "Error"

    if (result == result.roundToInt().toDouble()) {
        return result.roundToInt().toString()
    }

    var formattedString = String.format("%.8f", result)
    formattedString = formattedString.trimEnd('0').trimEnd('.')

    if (formattedString == "-0" || formattedString == "-") return "0"

    if (formattedString.length > 15) return String.format("%.5e", result)

    return formattedString
}

private fun Double.toRadians(): Double = this * PI / 180.0
private fun factorial(n: Int): Int {
    if (n > 20) throw RuntimeException("Number too large!")
    return if (n == 1 || n == 0) 1 else n * factorial(n - 1)
}
private fun Double.toDegrees(): Double = this * 180.0 / PI

@Throws(RuntimeException::class)
private fun evalKotlin(str: String): Double {
    return object : Any() {
        var pos = -1
        var ch: Int = 0

        fun nextChar() {
            ch = if (++pos < str.length) str[pos].code else -1
        }

        fun eat(charToEat: Int): Boolean {
            while (ch == ' '.code) nextChar()
            if (ch == charToEat) {
                nextChar()
                return true
            }
            return false
        }

        fun parse(): Double {
            nextChar()
            val x = parseExpression()
            if (pos < str.length) throw RuntimeException("Unexpected: " + ch.toChar())
            return x
        }

        fun parseExpression(): Double {
            var x = parseTerm()
            while (true) {
                if (eat('+'.code)) x += parseTerm()
                else if (eat('-'.code)) x -= parseTerm()
                else return x
            }
        }

        fun parseTerm(): Double {
            var x = parseFactor()
            while (true) {
                if (eat('*'.code)) x *= parseFactor()
                else if (eat('/'.code)) x /= parseFactor()
                else if (eat('%'.code)) x %= parseFactor()
                else return x
            }
        }

        fun parseFactor(): Double {
            if (eat('+'.code)) return parseFactor()
            if (eat('-'.code)) return -parseFactor()

            var x: Double
            val startPos = this.pos
            if (eat('('.code)) {
                x = parseExpression()
                eat(')'.code)
            } else if (ch >= '0'.code && ch <= '9'.code || ch == '.'.code) {
                while (ch >= '0'.code && ch <= '9'.code || ch == '.'.code) nextChar()
                x = str.substring(startPos, this.pos).toDouble()
            } else if (ch >= 'a'.code && ch <= 'z'.code) {
                while (ch >= 'a'.code && ch <= 'z'.code) nextChar()
                val func = str.substring(startPos, this.pos)

                x = when (func) {
                    else -> {
                        val arg = parseFactor()
                        when (func) {
                            "sqrt" -> if (arg < 0) throw RuntimeException("Domain Error: sqrt of negative") else sqrt(arg)
                            "log10" -> if (arg <= 0) throw RuntimeException("Domain Error: log of non-positive") else log10(arg)
                            "ln" -> if (arg <= 0) throw RuntimeException("Domain Error: log of non-positive") else ln(arg)
                            "sin" -> sin(arg.toRadians())
                            "cos" -> cos(arg.toRadians())
                            "tan" -> tan(arg.toRadians())
                            "asin" -> asin(arg).toDegrees()
                            "acos" -> acos(arg).toDegrees()
                            "atan" -> atan(arg).toDegrees()
                            else -> throw RuntimeException("Unknown function: $func")
                        }
                    }
                }
            } else {
                throw RuntimeException("Unexpected: " + ch.toChar())
            }

            if (eat('^'.code)) x = x.pow(parseFactor())
            return x
        }
    }.parse()
}

@Composable
fun CalculatorScreen() {
    var expression by remember { mutableStateOf("0") }
    var isInvertedTrig by remember { mutableStateOf(false) }

    val updateExpression: (String) -> Unit = { input ->
        if (input == "%") {
            val lastNumberRegex = Regex("([0-9.]+)([^0-9.]*)\$")
            val match = lastNumberRegex.find(expression)

            if (match != null) {
                val lastNumberString = match.groups[1]?.value ?: ""
                val prefix = expression.substring(0, expression.length - lastNumberString.length)

                val lastNumber = lastNumberString.toDoubleOrNull()
                if (lastNumber != null) {
                    val percentageValue = lastNumber / 100.0
                    expression = prefix + formatResult(percentageValue)
                } else {
                    expression += input
                }
            } else {
                expression += input
            }
        } else {
            val isOperator = !input.first().isLetterOrDigit() && input != "." && input != "(" && input != ")"
            val isDigitOrStartFunc = input.first().isDigit() || input == "("

            if (expression == "0") {
                if (isOperator && input != "(") {
                    expression = input
                } else if (isDigitOrStartFunc) {
                    expression = input
                }
            } else {
                expression += input
            }
        }
    }

    val onInvToggle: () -> Unit = {
        isInvertedTrig = !isInvertedTrig
    }

    val onScientificOpClick: (String, String) -> Unit = { op, _ ->
        val actualOp = when (op) {
            "sin" -> if (isInvertedTrig) "asin" else "sin"
            "cos" -> if (isInvertedTrig) "acos" else "cos"
            "tan" -> if (isInvertedTrig) "atan" else "tan"
            else -> op
        }

        when (actualOp) {
            "fact" -> {
                val currentValue = expression.toDoubleOrNull()?.roundToInt()
                if (currentValue != null && currentValue >= 0) {
                    try {
                        expression = formatResult(factorial(currentValue).toDouble())
                    } catch (e: Exception) {
                        expression = "Error"
                    }
                } else {
                    expression = "Error"
                }
            }
            "sqrt", "log10", "ln", "sin", "cos", "tan", "asin", "acos", "atan", "inv" -> {
                val currentValue = expression.toDoubleOrNull()

                if (currentValue != null && expression != "0" && !expression.contains("(") && !expression.contains(actualOp)) {
                    try {
                        val result = when (actualOp) {
                            "sqrt" -> if (currentValue < 0) throw RuntimeException("Domain Error: sqrt of negative") else sqrt(currentValue)
                            "log10" -> if (currentValue <= 0) throw RuntimeException("Domain Error: log of non-positive") else log10(currentValue)
                            "ln" -> if (currentValue <= 0) throw RuntimeException("Domain Error: log of non-positive") else ln(currentValue)
                            "sin" -> sin(currentValue.toRadians())
                            "cos" -> cos(currentValue.toRadians())
                            "tan" -> tan(currentValue.toRadians())
                            "asin" -> asin(currentValue).toDegrees()
                            "acos" -> acos(currentValue).toDegrees()
                            "atan" -> atan(currentValue).toDegrees()
                            "inv" -> if (currentValue == 0.0) throw RuntimeException("division by zero") else 1.0 / currentValue
                            else -> currentValue
                        }
                        expression = formatResult(result)

                    } catch (e: Exception) {
                        expression = "Error"
                    }
                } else {
                    val functionName = when (actualOp) {
                        "sqrt" -> "sqrt("
                        "inv" -> "1/("
                        "log10" -> "log10("
                        "ln" -> "ln("
                        "sin" -> "sin("
                        "cos" -> "cos("
                        "tan" -> "tan("
                        "asin" -> "asin("
                        "acos" -> "acos("
                        "atan" -> "atan("
                        else -> "$actualOp("
                    }
                    if (expression == "0") {
                        expression = functionName
                    } else {
                        expression += functionName
                    }
                }
            }
            else -> {
                val functionName = "$actualOp("
                if (expression == "0") {
                    expression = functionName
                } else {
                    expression += functionName
                }
            }
        }
    }

    val onEqualsClick: () -> Unit = {
        if (expression.isNotBlank() && expression != "Error") {
            try {
                var cleanExpression = expression
                    .replace('×', '*')
                    .replace('÷', '/')
                    .replace("√(", "sqrt(")
                    .replace("²", "^2")

                val result = evalKotlin(cleanExpression)
                expression = formatResult(result)
            } catch (e: RuntimeException) {
                expression = "Error"
            } catch (e: Exception) {
                expression = "Error"
            }
        }
    }

    val onClearClick: (String) -> Unit = { type ->
        when (type) {
            "AC" -> { expression = "0"; isInvertedTrig = false }
            "DEL" -> {
                if (expression != "Error") {
                    if (expression.length > 1) {
                        expression = expression.dropLast(1)
                    } else {
                        expression = "0"
                    }
                } else {
                    expression = "0"
                }
            }
        }
    }

    CalculatorLayout(
        expression = expression,
        isInvertedTrig = isInvertedTrig,
        onUpdateExpression = updateExpression,
        onScientificOpClick = onScientificOpClick,
        onEqualsClick = onEqualsClick,
        onClearClick = onClearClick,
        onInvToggle = onInvToggle
    )
}