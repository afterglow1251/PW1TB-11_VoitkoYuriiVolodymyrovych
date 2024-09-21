package com.example.myapplication.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.myapplication.R
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.utils.applyWindowInsets
import com.example.myapplication.utils.navigateToActivity
import com.example.myapplication.utils.setupTouchListener
import com.example.myapplication.utils.setupCalculateButton
import com.example.myapplication.utils.setupInputValidation
import com.example.myapplication.utils.parseValueFromEditText
import com.google.android.material.textfield.TextInputEditText

class SecondActivity : AppCompatActivity() {

    // Оголошення змінних для полів вводу та кнопок
    private lateinit var inputHp: TextInputEditText
    private lateinit var inputCp: TextInputEditText
    private lateinit var inputSp: TextInputEditText
    private lateinit var inputOp: TextInputEditText
    private lateinit var inputWp: TextInputEditText
    private lateinit var inputAp: TextInputEditText
    private lateinit var inputVp: TextInputEditText
    private lateinit var inputQFuelOil: TextInputEditText
    private lateinit var resultText: TextView
    private lateinit var errorText: TextView
    private lateinit var calculateButton: Button
    private lateinit var goToMainActivityButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_second)

        val secondView = findViewById<View>(R.id.second)

        // Застосування відступів вікна
        applyWindowInsets(secondView)

        // Налаштування прослуховувача дотиків
        setupTouchListener(secondView)

        // Ініціалізація полів вводу та кнопок
        inputHp = findViewById(R.id.input_edit_hp) // HP
        inputCp = findViewById(R.id.input_edit_cp) // CP
        inputSp = findViewById(R.id.input_edit_sp) // SP
        inputOp = findViewById(R.id.input_edit_op) // OP
        inputWp = findViewById(R.id.input_edit_wp) // WP
        inputAp = findViewById(R.id.input_edit_ap) // AP
        inputVp = findViewById(R.id.input_edit_v) // VP
        inputQFuelOil = findViewById(R.id.input_edit_q_fuel_oil) // Q мазуту
        resultText = findViewById(R.id.result_text)
        errorText = findViewById(R.id.error_text)
        calculateButton = findViewById(R.id.calculate_button)
        goToMainActivityButton = findViewById(R.id.go_to_main_activity_button)

        val inputs = listOf(inputHp, inputCp, inputSp, inputOp, inputWp, inputAp, inputVp, inputQFuelOil)

        // Налаштування обробника натискань для кнопки обчислення
        setupCalculateButton(calculateButton, inputs, errorText) {
            calculateValues()
        }

        // Налаштування валідації для полів вводу
        setupInputValidation(inputs, errorText)

        // Обробник натискань для кнопки переходу
        goToMainActivityButton.setOnClickListener {
            navigateToActivity(this, MainActivity::class.java)
        }
    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun calculateValues() {
        // Отримуємо значення з полів вводу
        val inputValues = mapOf(
            "H" to parseValueFromEditText(inputHp) { it.toDoubleOrNull() },
            "C" to parseValueFromEditText(inputCp) { it.toDoubleOrNull() },
            "S" to parseValueFromEditText(inputSp) { it.toDoubleOrNull() },
            "O" to parseValueFromEditText(inputOp) { it.toDoubleOrNull() },
            "W" to parseValueFromEditText(inputWp) { it.toDoubleOrNull() },
            "A" to parseValueFromEditText(inputAp) { it.toDoubleOrNull() },
            "V" to parseValueFromEditText(inputVp) { it.toDoubleOrNull() },
            "qFO" to parseValueFromEditText(inputQFuelOil) { it.toDoubleOrNull() }
        )

        val w = inputValues["W"]!!
        val a = inputValues["A"]!!
        val qFO = inputValues["qFO"]!!
        val krs = (100 - w - a) / 100

        // Обчислення складу робочої маси
        val hWork = inputValues["H"]!! * krs
        val cWork = inputValues["C"]!! * krs
        val sWork = inputValues["S"]!! * krs
        val oWork = inputValues["O"]!! * krs
        val vWork = inputValues["V"]!! * (100 - w) / 100

        // Нижча теплота згоряння для робочої маси
        val qR = qFO * krs - 0.025 * w

        // Виведення результатів
        resultText.text = """
        Склад робочої маси мазуту:
        CP: ${"%.2f".format(cWork)}%
        HP: ${"%.2f".format(hWork)}%
        SP: ${"%.2f".format(sWork)}%
        OP: ${"%.2f".format(oWork)}%
        AP: ${"%.2f".format(a)}%
        VP: ${"%.2f".format(vWork)} мг/кг
        
        Нижча теплота згоряння: ${"%.2f".format(qR)} МДж/кг
    """.trimIndent()
    }
}
