package com.example.calculator

import java.util.Stack

class Calculator
{
    private val expressionBuilder = mutableListOf<String>()

    val expression: String
        get() {
            return expressionBuilder.joinToString(separator = "") {
                when (it) {
                    "*" -> "\u00D7"
                    "/" -> "\u00F7"
                    else -> it
                }
            }
        }

    fun appendToExpression(element: String) {
        if (!this.isValid(element)) {
            throw IllegalArgumentException("Illegal input: $element")
        }

        if (expressionBuilder.isEmpty()) {
            expressionBuilder.add(element)
            return
        }

        if (
            element == "." &&
            expressionBuilder.last().last().isDigit()
        ) {
            expressionBuilder[expressionBuilder.size - 1] += element
        } else if (
            element.toDoubleOrNull() != null &&
            (expressionBuilder.last().toDoubleOrNull() != null || expressionBuilder.last().endsWith("."))
        ) {  // if last element digit or digit with dot (2.)
            expressionBuilder[expressionBuilder.size - 1] += element
        } else if (
            "+-*/()".contains(element) &&
            expressionBuilder.last().endsWith(".")
        ) {  // if last element 1. and add +, -, *, /, ), (
            expressionBuilder[expressionBuilder.size - 1] += "0"
            expressionBuilder.add(element)
        } else if (
            element.toDoubleOrNull() != null &&
            expressionBuilder.last() == "-" &&
            (expressionBuilder.size == 1 || expressionBuilder[expressionBuilder.size - 2] == "(")
        ) { // if is -3+3 or (-3)+3
            expressionBuilder[expressionBuilder.size - 1] += element
        } else {
            expressionBuilder.add(element)
        }
    }

    fun clear() {
        this.expressionBuilder.clear()
    }

    fun removeLast() {
        if (expressionBuilder.isEmpty()) {
            return
        }

        val last = expressionBuilder.last()
        if (last.toDoubleOrNull() != null) {
            var newLast = last.dropLast(1)

            if (newLast.endsWith(".")) {
                newLast = newLast.dropLast(1)
            }

            if (newLast.isEmpty()) {
                expressionBuilder.removeAt(expressionBuilder.size - 1)
            } else {
                expressionBuilder[expressionBuilder.size - 1] = newLast
            }

        } else {
            expressionBuilder.removeAt(expressionBuilder.size - 1)
        }
    }

    fun calculate(): Double {
        if (this.expressionBuilder.isEmpty()) {
            return 0.0
        }

        val postfix = infixToPostfix(expressionBuilder)
        return evaluatePostfix(postfix)
    }

    private fun isValid(element: String): Boolean {
        val pattern = Regex("""^(\d+(\.\d+)?|[()+\-*/\.])$""") // match numbers and +, -, *, ), (

        return pattern.matches(element)
    }

    private fun infixToPostfix(tokens: List<String>): List<String> {
        val output = mutableListOf<String>()
        val operators = Stack<String>()

        for (token in tokens) {
            when {
                token.toDoubleOrNull() != null -> output.add(token)
                token == "(" -> operators.push(token)
                token == ")" -> {
                    while (operators.peek() != "(") {
                        output.add(operators.pop())
                    }
                    operators.pop()
                }
                "+-*/".contains(token) -> {
                    while (operators.isNotEmpty() && precedence(operators.peek()) >= precedence(token)) {
                        output.add(operators.pop())
                    }
                    operators.push(token)
                }
            }
        }

        while (operators.isNotEmpty()) {
            output.add(operators.pop())
        }

        return output
    }

    // Setting priorities for operators
    private fun precedence(operator: String): Int {
        return when (operator) {
            "+", "-" -> 1
            "*", "/" -> 2
            else -> 0
        }
    }

    private fun evaluatePostfix(postfix: List<String>): Double {
        val stack = Stack<Double>()

        for (token in postfix) {
            if (token.toDoubleOrNull() != null) {
                stack.push(token.toDouble())
            }

            if ("+-*/".contains(token)) {
                val b = stack.pop()
                val a = stack.pop()
                val result = when (token) {
                    "+" -> a + b
                    "-" -> a - b
                    "*" -> a * b
                    "/" -> a / b
                    else -> throw IllegalArgumentException("Illegal operator: $token")
                }
                stack.push(result)
            }
        }

        return stack.pop()
    }
}