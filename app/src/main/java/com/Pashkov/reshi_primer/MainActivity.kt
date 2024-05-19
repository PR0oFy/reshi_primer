package com.Pashkov.reshi_primer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import kotlin.random.Random
import com.Pashkov.reshi_primer.databinding.ActivityMainBinding
import android.text.InputType

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding // Создание переменной для привязки к макету

    private var right = 0 // Переменная правильных ответов
    private var lose = 0 // Переменная  неправильных ответов
    private var all = 0 // Переменная общих попыток
    private var isStartButtonEnabled = true //  Доступна ли кнопка "Старт"
    private var isCheckButtonEnabled = false // Доступна ли кнопка "Проверка"
    private var isInputEnabled = false // Доступно ли поле ввода
    private var operand1: String = "" // Первый операнд для примера
    private var operand2: String = "" // Второй операнд для примера
    private var vvodColor: Int = 0 // Цвет поля ввода

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Инициализация привязки к макету
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Установка фона макета
        binding.primerlayout.setBackgroundResource(R.color.white)

        // Проверка на наличие сохраненного состояния
        if (savedInstanceState != null) {
            // Восстановление значений переменных из сохраненного состояния
            right = savedInstanceState.getInt("right", 0)
            lose = savedInstanceState.getInt("lose", 0)
            all = savedInstanceState.getInt("all", 0)
            isStartButtonEnabled = savedInstanceState.getBoolean("isStartButtonEnabled", true)
            isCheckButtonEnabled = savedInstanceState.getBoolean("isCheckButtonEnabled", false)
            isInputEnabled = savedInstanceState.getBoolean("isInputEnabled", false)
            operand1 = savedInstanceState.getString("operand1", "")
            operand2 = savedInstanceState.getString("operand2", "")
            vvodColor = savedInstanceState.getInt("vvodColor", ContextCompat.getColor(this, android.R.color.white))
        }


        // Обновление пользовательского интерфейса
        updateUI()

        // Отключение кнопки "Проверка" и поля ввода при создании активности
        binding.check.isEnabled = isCheckButtonEnabled
        binding.start.isEnabled = isStartButtonEnabled
        binding.vvod.isEnabled = isInputEnabled

        // Установка типа ввода для поля vvod, позволяющего вводить отрицательные числа
        binding.vvod.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED

        // Обработчик нажатия на кнопку "Старт"
        binding.start.setOnClickListener {
            isCheckButtonEnabled = true // Включение кнопки "Проверка"
            isStartButtonEnabled = false // Отключение кнопки "Старт"
            isInputEnabled = true // Включение поля ввода

            // Установка состояния кнопок и поля ввода
            binding.check.isEnabled = isCheckButtonEnabled
            binding.start.isEnabled = isStartButtonEnabled
            binding.vvod.isEnabled = isInputEnabled

            generatePrimer() // Генерация примера

            // Устанавливаем цвет поля ввода в белый и запоминаем его
            vvodColor = ContextCompat.getColor(this, android.R.color.white)
            binding.vvod.setBackgroundColor(vvodColor)

            binding.vvod.text = null // Очистка поля ввода
        }

        // Обработчик нажатия на кнопку "Проверка"
        binding.check.setOnClickListener {
            isCheckButtonEnabled = false // Отключение кнопки "Проверка"
            isStartButtonEnabled = true // Включение кнопки "Старт"
            isInputEnabled = false // Отключение поля ввода

            // Установка состояния кнопок и поля ввода
            binding.check.isEnabled = isCheckButtonEnabled
            binding.start.isEnabled = isStartButtonEnabled
            binding.vvod.isEnabled = isInputEnabled

            checkPrimer() // Проверка примера
        }
    }

    // Генерация случайного операнда
    private fun generateRandomOperand(): Int {
        return Random.nextInt(10, 100)
    }

    // Генерация случайного оператора
    private fun generateRandomOperator(): Char {
        val operators = listOf('*', '/', '-', '+')
        return operators.random()
    }

    // Генерация примера
    private fun generatePrimer(){
        binding.vvod.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white))

        operand1 = generateRandomOperand().toString()
        operand2 = generateRandomOperand().toString()

        binding.nullNull1.text = operand1
        binding.nullNull2.text = operand2
        binding.znak.text = generateRandomOperator().toString()
    }

    // Проверка примера
    private fun checkPrimer(){
        val numberOne = binding.nullNull1.text.toString().toDouble()
        val numberTwo = binding.nullNull2.text.toString().toDouble()
        var pollResult = binding.vvod.text.toString()

        val result = when (binding.znak.text) {
            "+" -> numberOne + numberTwo
            "-" -> numberOne - numberTwo
            "*" -> numberOne * numberTwo
            "/" -> {
                val stringResult = String.format("%.2f", numberOne / numberTwo)
                stringResult.replace(',', '.').toDouble()
            }
            else -> throw IllegalArgumentException("Unknown operator")
        }

        // Если ввод пуст, устанавливаем значение по умолчанию
        if (pollResult.isEmpty()){
            pollResult = "0"
        }

        // Проверка правильности ответа
        if (result == pollResult.toDouble()){
            right++
            vvodColor = ContextCompat.getColor(this, R.color.green)
        } else {
            lose++
            vvodColor = ContextCompat.getColor(this, R.color.red)
        }
        all++

        updateUI()
    }

    // Обновление пользовательского интерфейса
    private fun updateUI() {
        // Проверка деления на ноль для предотвращения "NaN%"
        val present = if (all > 0) {
            String.format("%.2f%%", (right.toDouble() / all.toDouble()) * 100)
        } else {
            "0,00%"
        }
        binding.null1.text = all.toString()
        binding.null2.text = right.toString()
        binding.null3.text = lose.toString()
        binding.prosenttext.text = present

        // Сохранение цвета объекта "vvod"
        binding.vvod.setBackgroundColor(vvodColor)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Сохранение состояния приложения
        outState.putInt("right", right)
        outState.putInt("lose", lose)
        outState.putInt("all", all)
        outState.putBoolean("isStartButtonEnabled", isStartButtonEnabled)
        outState.putBoolean("isCheckButtonEnabled", isCheckButtonEnabled)
        outState.putBoolean("isInputEnabled", isInputEnabled)
        outState.putString("operand1", operand1)
        outState.putString("operand2", operand2)
        outState.putInt("vvodColor", vvodColor) // Сохраняем цвет поля ввода
    }
}
