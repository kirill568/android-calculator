package com.example.calculator

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.HorizontalScrollView
import com.example.calculator.databinding.ActivityMainBinding
import java.util.EmptyStackException

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val calculator: Calculator = Calculator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.expression.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                binding.expressionScroll.post{
                    binding.expressionScroll.fullScroll(HorizontalScrollView.FOCUS_RIGHT)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    fun onClearClick(view: View) {
        this.calculator.clear()
        binding.expression.text = ""
        binding.result.text = "0"
    }

    fun onOperatorClick(view: View) {
        if (
            calculator.expression.isEmpty() &&
            !(view.id == binding.minus.id || view.id == binding.leftBrace.id)
        ) {
            return
        }

        when(view.id) {
            binding.divide.id -> calculator.appendToExpression("/")
            binding.multiple.id -> calculator.appendToExpression("*")
            binding.minus.id -> calculator.appendToExpression("-")
            binding.plus.id -> calculator.appendToExpression("+")
            binding.leftBrace.id -> calculator.appendToExpression("(")
            binding.rightBrace.id -> calculator.appendToExpression(")")
            binding.dot.id -> calculator.appendToExpression(".")
        }

        binding.expression.text = calculator.expression
    }

    fun onDigitClick(view: View) {
        when(view.id) {
            binding.digit1.id -> calculator.appendToExpression("1")
            binding.digit2.id -> calculator.appendToExpression("2")
            binding.digit3.id -> calculator.appendToExpression("3")
            binding.digit4.id -> calculator.appendToExpression("4")
            binding.digit5.id -> calculator.appendToExpression("5")
            binding.digit6.id -> calculator.appendToExpression("6")
            binding.digit7.id -> calculator.appendToExpression("7")
            binding.digit8.id -> calculator.appendToExpression("8")
            binding.digit9.id -> calculator.appendToExpression("9")
            binding.digitZero.id-> calculator.appendToExpression("0")
        }

        binding.expression.text = calculator.expression
    }

    fun onDelClick(view: View) {
        calculator.removeLast()

        binding.expression.text = calculator.expression
    }

    fun calculate(view: View) {
        try {
            val result = calculator.calculate()

            if (result % 1.0 == 0.0) {
                binding.result.text = result.toInt().toString()
            } else {
                binding.result.text = String.format("%.3f", result)
            }
        } catch (ex: IllegalArgumentException) {
            binding.result.text = "Выражение не валидно"
        } catch (ex: EmptyStackException) {
            binding.result.text = "Выражение не валидно"
        } catch (ex: ArithmeticException) {
            binding.result.text = "Выражение не валидно"
        }
    }
}