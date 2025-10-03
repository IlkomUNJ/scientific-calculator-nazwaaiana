package com.example.tugaskalkulator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val DarkBackground = Color(0xFF1E1E1E)
val DarkGray = Color(0xFF333333)
val MediumGray = Color(0xFF555555)
val TealOperator = Color(0xFF009688)
val OrangeEquals = Color(0xFFFF9800)
val BlueInverseActive = Color(0xFF0288D1)

private val SciButtonHeight = 44.dp
private val BasicButtonHeight = 68.dp

@Composable
fun CalculatorLayout(
    expression: String,
    isInvertedTrig: Boolean,
    onUpdateExpression: (String) -> Unit,
    onScientificOpClick: (String, String) -> Unit,
    onEqualsClick: () -> Unit,
    onClearClick: (String) -> Unit,
    onInvToggle: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 10.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                text = expression,
                fontSize = 68.sp,
                fontWeight = FontWeight.Light,
                color = Color.White,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }
        ScientificCalculatorButtons(
            onUpdateExpression = onUpdateExpression,
            onScientificOpClick = onScientificOpClick,
            onEqualsClick = onEqualsClick,
            onClearClick = onClearClick,
            onInvToggle = onInvToggle,
            isInvertedTrig = isInvertedTrig
        )
    }
}

@Composable
fun ScientificCalculatorButtons(
    onUpdateExpression: (String) -> Unit,
    onScientificOpClick: (String, String) -> Unit,
    onEqualsClick: () -> Unit,
    onClearClick: (String) -> Unit,
    onInvToggle: () -> Unit,
    isInvertedTrig: Boolean
) {
    val sciFontSize = 16.sp
    val basicFontSize = 24.sp
    val clearColor = TealOperator
    val sinLabel = if (isInvertedTrig) "sin⁻¹" else "sin"
    val cosLabel = if (isInvertedTrig) "cos⁻¹" else "cos"
    val tanLabel = if (isInvertedTrig) "tan⁻¹" else "tan"
    val invBgColor = if (isInvertedTrig) BlueInverseActive else MediumGray
    val invTextColor = if (isInvertedTrig) Color.White else Color.White

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SciButton("log", Modifier.weight(1f), MediumGray, Color.White, fontSize = sciFontSize) { onScientificOpClick("log10", "log") }
            SciButton("ln", Modifier.weight(1f), MediumGray, Color.White, fontSize = sciFontSize) { onScientificOpClick("ln", "ln") }
            SciButton("xʸ", Modifier.weight(1f), MediumGray, Color.White, fontSize = sciFontSize) { onUpdateExpression("^") }
            SciButton("x!", Modifier.weight(1f), MediumGray, Color.White, fontSize = sciFontSize) { onScientificOpClick("fact", "fact") }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SciButton("Inv", Modifier.weight(1f), invBgColor, invTextColor, fontSize = sciFontSize) { onInvToggle() }
            SciButton(sinLabel, Modifier.weight(1f), MediumGray, Color.White, fontSize = sciFontSize) { onScientificOpClick("sin", sinLabel) }
            SciButton(cosLabel, Modifier.weight(1f), MediumGray, Color.White, fontSize = sciFontSize) { onScientificOpClick("cos", cosLabel) }
            SciButton(tanLabel, Modifier.weight(1f), MediumGray, Color.White, fontSize = sciFontSize) { onScientificOpClick("tan", tanLabel) }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SciButton("√", Modifier.weight(1f), MediumGray, Color.White, fontSize = sciFontSize) { onScientificOpClick("sqrt", "√") }
            SciButton("1/x", Modifier.weight(1f), MediumGray, Color.White, fontSize = sciFontSize) { onScientificOpClick("inv", "1/") }
            SciButton("(", Modifier.weight(1f), MediumGray, Color.White, fontSize = sciFontSize) { onUpdateExpression("(") }
            SciButton(")", Modifier.weight(1f), MediumGray, Color.White, fontSize = sciFontSize) { onUpdateExpression(")") }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BasicButton("AC", Modifier.weight(1f), clearColor, Color.White, fontSize = basicFontSize) { onClearClick("AC") }
            BasicButton("<-", Modifier.weight(1f), clearColor, Color.White, fontSize = basicFontSize) { onClearClick("DEL") }
            BasicButton("%", Modifier.weight(1f), TealOperator, Color.White, fontSize = basicFontSize) { onUpdateExpression("%") }
            BasicButton("÷", Modifier.weight(1f), TealOperator, Color.White, fontSize = basicFontSize) { onUpdateExpression("÷") }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BasicButton("7", Modifier.weight(1f), DarkGray, Color.White, fontSize = basicFontSize) { onUpdateExpression("7") }
            BasicButton("8", Modifier.weight(1f), DarkGray, Color.White, fontSize = basicFontSize) { onUpdateExpression("8") }
            BasicButton("9", Modifier.weight(1f), DarkGray, Color.White, fontSize = basicFontSize) { onUpdateExpression("9") }
            BasicButton("×", Modifier.weight(1f), TealOperator, Color.White, fontSize = basicFontSize) { onUpdateExpression("×") }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BasicButton("4", Modifier.weight(1f), DarkGray, Color.White, fontSize = basicFontSize) { onUpdateExpression("4") }
            BasicButton("5", Modifier.weight(1f), DarkGray, Color.White, fontSize = basicFontSize) { onUpdateExpression("5") }
            BasicButton("6", Modifier.weight(1f), DarkGray, Color.White, fontSize = basicFontSize) { onUpdateExpression("6") }
            BasicButton("-", Modifier.weight(1f), TealOperator, Color.White, fontSize = basicFontSize) { onUpdateExpression("-") }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BasicButton("1", Modifier.weight(1f), DarkGray, Color.White, fontSize = basicFontSize) { onUpdateExpression("1") }
            BasicButton("2", Modifier.weight(1f), DarkGray, Color.White, fontSize = basicFontSize) { onUpdateExpression("2") }
            BasicButton("3", Modifier.weight(1f), DarkGray, Color.White, fontSize = basicFontSize) { onUpdateExpression("3") }
            BasicButton("+", Modifier.weight(1f), TealOperator, Color.White, fontSize = basicFontSize) { onUpdateExpression("+") }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BasicButton("0", Modifier.weight(2f), DarkGray, Color.White, fontSize = basicFontSize) { onUpdateExpression("0") }
            BasicButton(".", Modifier.weight(1f), DarkGray, Color.White, fontSize = basicFontSize) { onUpdateExpression(".") }
            BasicButton("=", Modifier.weight(1f), OrangeEquals, Color.White, fontSize = 32.sp) { onEqualsClick() }
        }
    }
}
@Composable
fun CalcButton(
    label: String,
    modifier: Modifier = Modifier,
    bgColor: Color = DarkGray,
    textColor: Color = Color.White,
    fontSize: TextUnit = 24.sp,
    height: Dp = BasicButtonHeight,
    onClick: () -> Unit = {}
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(height),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = bgColor,
            contentColor = textColor
        ),
        contentPadding = PaddingValues(4.dp)
    ) {
        Text(
            text = label,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
fun SciButton(
    label: String,
    modifier: Modifier = Modifier,
    bgColor: Color = MediumGray,
    textColor: Color = Color.White,
    fontSize: TextUnit = 16.sp,
    onClick: () -> Unit = {}
) {
    CalcButton(label, modifier, bgColor, textColor, fontSize, SciButtonHeight, onClick)
}

@Composable
fun BasicButton(
    label: String,
    modifier: Modifier = Modifier,
    bgColor: Color = DarkGray,
    textColor: Color = Color.White,
    fontSize: TextUnit = 28.sp,
    onClick: () -> Unit = {}
) {
    CalcButton(label, modifier, bgColor, textColor, fontSize, BasicButtonHeight, onClick)
}

@Preview(showBackground = true)
@Composable
fun CalculatorLayoutPreview() {
    MaterialTheme {
        CalculatorScreen()
    }

}