package android.fuckin.rash.jp.calc

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import java.util.*

class CalcActivity : AppCompatActivity() {

    lateinit var textViewLcd: TextView
    lateinit var textViewStack: TextView
    var valueInput = 0L
    var stackedInputValue = false
    var valueResult = 0L
    val stack = mutableListOf<Long>()
    val isRPN = true

    val buttonLetters = listOf<Int>(
            R.id.button_0,
            R.id.button_1,
            R.id.button_2,
            R.id.button_3,
            R.id.button_4,
            R.id.button_5,
            R.id.button_6,
            R.id.button_7,
            R.id.button_8,
            R.id.button_9
    )
    var buttons = emptyList<Button>()

    enum class Operation {
        None,
        Plus,
        Minus,
    }
    var operation = Operation.None

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calc)

        //println("textViewLcd=" + textViewLcd)


        println(rpn_pop())
        rpn_push(99)
        rpn_push(1)
        println(rpn_pop())
        rpn_push(100)
        println(rpn_pop())
        println(rpn_pop())


        textViewLcd = findViewById(R.id.text_view_lcd) as TextView
        textViewStack = findViewById(R.id.text_view_stack) as TextView
        setLcdValue(0)


        buttons = buttonLetters.map { findViewById(it) as Button }

        /*
        buttons.map {
            it.setOnClickListener(pushedNumber)
        }
        */
        for ((index, button) in buttons.withIndex()) {
            button.setOnClickListener(pushedNumber)
        }

        findViewById(R.id.button_plus).setOnClickListener(pushedPlus)
        findViewById(R.id.button_minus).setOnClickListener(pushedMinus)
        findViewById(R.id.button_equal).setOnClickListener(pushedEqual)
        findViewById(R.id.button_clear).setOnClickListener(pushedClear)

        if (isRPN) {
            (findViewById(R.id.button_equal) as Button).text = getString(R.string.mark_enter)
        }

        println(buttons)

    }

    fun setLcdValue(value: Long) {
        textViewLcd.text = value.toString()
        textViewLcd.setTextColor(if (stackedInputValue) Color.GRAY else Color.WHITE)

        textViewStack.text = stack.toString()
    }

    val pushedNumber: (View) -> Unit = {
        val button = it as Button
        val value = button.text.toString().toInt()

        if (stackedInputValue) {
            valueInput = 0
        }
        stackedInputValue = false

        if (valueInput * 10 + value <= 9999999999) {
            valueInput = valueInput * 10 + value
            setLcdValue(valueInput)
        } else {
            println("OVERFLOW")
        }
    }

    val pushedClear: (View) -> Unit = {
        valueInput = 0
        valueResult = 0
        stack.clear()
        stackedInputValue = false
        setLcdValue(0)
    }

    val pushedPlus: (View) -> Unit = {
        if (isRPN) {
            val acc = if (stackedInputValue) rpn_pop() else valueInput
            stackedInputValue = true
            setLcdValue(rpn_push(rpn_pop() + acc))
        } else {
            operate()
            operation = Operation.Plus
        }
    }

    val pushedMinus: (View) -> Unit = {
        if (isRPN) {
            val acc = if (stackedInputValue) rpn_pop() else valueInput
            stackedInputValue = true
            setLcdValue(rpn_push(rpn_pop() - acc))
        } else {
            operate()
            operation = Operation.Minus
        }
    }

    val pushedEqual: (View) -> Unit = {
        if (isRPN) {
            if (!stackedInputValue) {
                stackedInputValue = true
                setLcdValue(rpn_push(valueInput))
            }
        } else {
            operate()
            operation = Operation.None
        }
    }

    fun operate() {
        when (operation) {
            Operation.None -> {
                valueResult = valueInput
                //valueInput = 0
                //setLcdValue(valueResult)
            }

            Operation.Plus -> {
                valueResult += valueInput
                valueInput = 0
                setLcdValue(valueResult)
            }

            Operation.Minus -> {
                valueResult -= valueInput
                valueInput = 0
                setLcdValue(valueResult)
            }
        }
    }

    fun rpn_push(value: Long): Long {
        stack += value
        return value
    }

    fun rpn_pop(): Long {
        if (stack.count() > 0) {
            return stack.removeAt(stack.count() - 1)
        } else {
            println("empty")
            return 0
        }
    }

}
