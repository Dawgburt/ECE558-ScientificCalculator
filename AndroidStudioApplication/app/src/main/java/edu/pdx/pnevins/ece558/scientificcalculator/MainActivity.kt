/**
 * MainActivity.kt
 *
 * ECE 558 Scientific Calculator
 *
 * This is an Android application implementing a scientific calculator
 * using Jetpack Compose. The app supports basic arithmetic operations,
 * square root, logarithm, and percentage calculations.
 *
 * Chat GPT was asked to give a template for a calculator app with buttons and a + operation
 * working that can be built on.
 * ChatGPT was consulted for debugging and error handling.
 *
 * Features:
 * - Basic arithmetic operations (+, -, *, /)
 * - Square (x^2), square root (√), logarithm (Log, Ln)
 * - Sin, Cos, Tan functions
 * - Percentage calculation (%)
 * - +/- sign toggle (not complete)
 * - Clear and Clear History (C, CH) functionality
 * - Displays calculation history (last 5 operations)
 *
 * Developed for ECE 558 at Portland State University.
 *
 * Author: Phil Nevins (p.nevins971@gmail.com)
 * Date Created: 1/25/2025
 *
 * Notes:
 * 1-25-2025    PN      Project created to experiment with Jetpack Compose via templates from ChatGPT
 * 2-8-2025     PN      Added Sin, Cos, Tan, Log, Ln, and % functions
 * 2-12-2025    PN      Finished implementing all functions, debugged issues and made final APK file
 */

package edu.pdx.pnevins.ece558.scientificcalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import edu.pdx.pnevins.ece558.scientificcalculator.ui.theme.CalculatorTheme
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.sqrt
import kotlin.math.log10
import kotlin.math.sin
import kotlin.math.tan
import androidx.navigation.compose.composable


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculatorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CalculatorApp()
                }
            }
        }
    }
}


@Composable
fun CalculatorApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "calculatorScreen") {
        composable("calculatorScreen") { CalculatorScreen(navController) }
        composable("secondScreen") { SecondScreen(navController) }
    }
}


@Composable
fun CalculatorScreen(navController: NavController) {
    var input by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    var operator by remember { mutableStateOf("") }
    var firstOperand by remember { mutableStateOf("") }
    var history by remember { mutableStateOf(listOf<String>()) } // Store history
    var clearPressedOnce by remember { mutableStateOf(false) } // Track first press
    var calculationCompleted by remember { mutableStateOf(false) } // New flag to track if "=" was pressed

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = "ECE 558 Calculator",
            style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF00008B)) // Dark Blue Background
                .padding(10.dp) // Padding for spacing
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Display History Above Result Box
        HistoryDisplay(history)
        Spacer(modifier = Modifier.height(16.dp))

        // Display
        Display(input = input, result = result, operator = operator, firstOperand = firstOperand)
        Spacer(modifier = Modifier.height(16.dp))

        // Buttons
        val buttons = listOf(
            "<-", "√", "x^2", "CH", "C",
            "Sin", "Log", "Ln", "%", "*",
            "Cos", "7", "8", "9", "/",
            "Tan", "4", "5", "6", "+",
            "", "1", "2", "3", "-",
            "", "+/-", "0", ".", "=",
        ) // 2nd Removed - under construction
        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            items(buttons) { button ->
                CalculatorButton(
                    text = button,
                    onClick = {
                        when (button) {

                            // Removed for release apk version
                            "2nd" -> {
                                navController.navigate("SecondScreen")
                            }

                            "<-" -> {
                                if (result.isNotEmpty()) {
                                    result = ""
                                } else if (input.isNotEmpty()) {
                                    input = input.dropLast(1)
                                } else if (operator.isNotEmpty()) {
                                    operator = operator.dropLast(1)
                                } else if (firstOperand.isNotEmpty()) {
                                    firstOperand = firstOperand.dropLast(1)
                                } else result = "Error"
                            }


                            "C" -> {
                                input = ""
                                result = ""
                                operator = ""
                                firstOperand = ""
                                calculationCompleted = false // Reset flag
                                clearPressedOnce = true // Mark first press
                            }

                            "CH" -> {
                                if (!clearPressedOnce) {
                                    // First press: Clear input, result, operator, firstOperand
                                    input = ""
                                    result = ""
                                    operator = ""
                                    firstOperand = ""
                                    clearPressedOnce = true // Mark first press
                                } else {
                                    // Second press: If everything is already cleared, clear the history list
                                    if (input.isEmpty() && result.isEmpty()) {
                                        history = emptyList() // Clears the entire history list
                                    }
                                    clearPressedOnce = false // Reset the flag
                                }
                            }

                            "+", "-", "*", "/" -> {
                                if(calculationCompleted){
                                    firstOperand = result
                                    operator = button
                                    input = ""
                                    calculationCompleted = false
                                }
                                if (input.isNotEmpty()) {
                                    operator = button
                                    firstOperand = input
                                    input = ""
                                    calculationCompleted = false // Reset flag since we're in the middle of a calculation
                                }
                            }

                            "Sin" ->{
                                result = (input.toDoubleOrNull()?.let { sin(it) } ?: "Error").toString()
                                history = history + "Sin($input) = $result"
                                operator = button
                                calculationCompleted = true // Set flag after calculation
                            }

                            "Cos" -> {
                                result = (input.toDoubleOrNull()?.let { cos(it) } ?: "Error").toString()
                                history = history + "Cos($input) = $result"
                                operator = button
                                calculationCompleted = true // Set flag after calculation
                            }

                            "Tan" -> {
                                result = (input.toDoubleOrNull()?.let { tan(it) } ?: "Error").toString()
                                history = history + "Tan($input) = $result"
                                operator = button
                                firstOperand = input
                                calculationCompleted = true // Set flag after calculation

                            }

                            "%" -> {
                                result = (input.toDoubleOrNull()?.div(100) ?: "Error").toString()
                                history = history + "$input% = $result"
                                operator = button
                                calculationCompleted = true // Set flag after calculation
                            }

                            "Log" -> {
                                result = (input.toDoubleOrNull()?.let { log10(it) } ?: "Error").toString()
                                history = history + "log($input) = $result"
                                operator = button
                                calculationCompleted = true // Set flag after calculation
                            }

                            "Ln" -> {
                                result = (input.toDoubleOrNull()?.let { ln(it) } ?: "Error").toString()
                                history = history + "ln($input) = $result"
                                operator = button
                                calculationCompleted = true // Set flag after calculation
                            }

                            "√" -> {
                                result = input.toDoubleOrNull()?.let {
                                    if (it >= 0) sqrt(it).toString() else "Error" // Ensure non-negative values only
                                } ?: "Error" // Show "Error" if input is invalid
                                history = history + "√$input = $result"
                                operator = button
                                calculationCompleted = true // Set flag after calculation
                            }

                            "x^2" -> {
                                result = (input.toDoubleOrNull()?.let { it * it } ?: "Error").toString()
                                history = history + "$input^2 = $result"
                                operator = button
                                calculationCompleted = true // Set flag after calculation
                            }

                            "+/-" -> {
                                input = if (input.startsWith("-")) {
                                    input.substring(1)
                                } else {
                                    "-$input"
                                }
                            }

                            "=" -> {
                                if (input.isNotEmpty() && firstOperand.isNotEmpty() && operator.isNotEmpty()) {
                                    result = calculate(firstOperand, input, operator)
                                    history = history + "$firstOperand $operator $input = $result" // Add to history
                                    calculationCompleted = true // Set flag to indicate completion
                                }
                            }

                            else -> {
                                // If last action was "=", clear input for new entry
                                if (calculationCompleted) {
                                    input = ""
                                    result = ""
                                    operator = ""
                                    firstOperand = ""
                                    input = button // Start new input with this number
                                    calculationCompleted = false // Reset flag
                                } else {
                                    input += button // Append number as usual
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}

//UNDER CONSTRUCTION. CODE LEFT IN PLACE FOR REFERENCE AND FUTURE WORK
@Composable
fun SecondScreen(navController: NavController) {
    var input by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    var operator by remember { mutableStateOf("") }
    var firstOperand by remember { mutableStateOf("") }
    var history by remember { mutableStateOf(listOf<String>()) } // Store history
    var clearPressedOnce by remember { mutableStateOf(false) } // Track first press
    var calculationCompleted by remember { mutableStateOf(false) } // New flag to track if "=" was pressed

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = "ECE 558 Calculator (2nd Mode)",
            style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF00008B))
                .padding(10.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Display History Above Result Box
        HistoryDisplay(history)
        Spacer(modifier = Modifier.height(16.dp))

        // Display
        Display(input = input, result = result, operator = operator, firstOperand = firstOperand)
        Spacer(modifier = Modifier.height(16.dp))

        // Buttons (No Functionality)
        val buttons = listOf(
            "", "", "", "CH", "C",
            "", "", "", "", "",
            "", "7", "8", "9", "/",
            "", "4", "5", "6", "+",
            "", "1", "2", "3", "-",
            "Back", "+/-", "0", ".", "=",
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            items(buttons) { button ->
                CalculatorButton(
                    text = button,
                    onClick = {
                        when (button) {
                            "+", "-", "*", "/" -> {
                                if(calculationCompleted){
                                    firstOperand = result
                                    operator = button
                                    input = ""
                                    calculationCompleted = false
                                }
                                if (input.isNotEmpty()) {
                                    operator = button
                                    firstOperand = input
                                    input = ""
                                    calculationCompleted = false // Reset flag since we're in the middle of a calculation
                                }
                            }

                            "CH" -> {
                                if (!clearPressedOnce) {
                                    // First press: Clear input, result, operator, firstOperand
                                    input = ""
                                    result = ""
                                    operator = ""
                                    firstOperand = ""
                                    clearPressedOnce = true // Mark first press
                                }
                            }

                            "C" -> {
                                input = ""
                                result = ""
                                operator = ""
                                firstOperand = ""
                                calculationCompleted = false // Reset flag
                                clearPressedOnce = true // Mark first press
                            }

                            "=" -> {
                                if (input.isNotEmpty() && firstOperand.isNotEmpty() && operator.isNotEmpty()) {
                                    result = calculate(firstOperand, input, operator)
                                    history = history + "$firstOperand $operator $input = $result" // Add to history
                                    calculationCompleted = true // Set flag to indicate completion
                                }
                            }

                            "Back" -> navController.popBackStack() // Return to CalculatorScreen
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun HistoryDisplay(history: List<String>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.DarkGray, RoundedCornerShape(8.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.End
    ) {
        history.takeLast(5).forEach { entry -> // Show last 5 calculations
            Text(
                text = entry,
                style = TextStyle(fontSize = 18.sp, color = Color.LightGray),
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}


@Composable
fun Display(input: String, result: String, operator: String, firstOperand: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black, RoundedCornerShape(8.dp)) // Black background
            .padding(16.dp),
        horizontalAlignment = Alignment.End
    ) {
// Construct dynamic display text with proper formatting for trigonometric functions
        val displayText = buildString {
            if (operator in listOf("Sin", "Cos", "Tan", "Log", "Ln", "√", "%")) {
                append("$operator($input)")
            } else if (operator in listOf("x^2")) {
                append("$input^2")
            } else {
                if (firstOperand.isNotEmpty()) append(firstOperand)
                if (operator.isNotEmpty()) append(" $operator ")
                if (input.isNotEmpty()) append(input)
            }
        }


        // Show the expression (only if it's not empty)
        if (displayText.isNotEmpty()) {
            Text(
                text = displayText,
                style = TextStyle(fontSize = 30.sp, color = Color.Red),
                textAlign = TextAlign.Left,
                modifier = Modifier.fillMaxWidth()
            )
        }


        // Show result only if it's not empty
        if (result.isNotEmpty()) {
            Text(
                text = if (result == "Error") "Error" else result, // Show "Error" if needed
                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White),
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}



@Composable
fun CalculatorButton(text: String, onClick: () -> Unit) {
    val buttonColor = when (text) {
        //Was having issues declaring color codes in Color.kt, so explicitly defined here
        "+", "-", "*", "/", "=", "%", "x^2", "Log", "Sqrt", "√", "Sin", "Cos", "Tan", "Ln" -> Color(0xFF00008B) // Dark Blue
        "+/-", ".", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" -> Color(0xFF000066) // Darker Navy Blue
        "CH", "C" -> Color(0xFF8A0707) // Blood Red
        else -> Color.Black // Default color
    }


    Button(
        onClick = onClick,
        modifier = Modifier
            .width(50.dp)
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
    ) {
        Text(
            text = text,
            style = TextStyle(fontSize = 14.sp, color = Color.White) // White text for all buttons
        )
    }
}

//Helper Function
fun calculate(firstOperand: String, secondOperand: String, operator: String): String {
    val num1 = firstOperand.toDoubleOrNull() ?: return "Error"
    val num2 = secondOperand.toDoubleOrNull() ?: return "Error"

    return when (operator) {
        "+" -> (num1 + num2).toString()
        "-" -> (num1 - num2).toString()
        "*" -> (num1 * num2).toString()
        "/" -> {
            if (num2 == 0.0) "Error" else (num1 / num2).toString()
        }

        else -> "Error"
    }
}

@Preview(showBackground = true)
@Composable
fun CalculatorAppPreview() {
    CalculatorTheme {
        CalculatorApp()
    }
}