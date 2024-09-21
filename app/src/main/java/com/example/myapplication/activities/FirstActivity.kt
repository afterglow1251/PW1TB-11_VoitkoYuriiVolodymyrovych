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

class FirstActivity : AppCompatActivity() {

    // Оголошення змінних для полів вводу та кнопок
    private lateinit var inputHp: TextInputEditText
    private lateinit var inputCp: TextInputEditText
    private lateinit var inputSp: TextInputEditText
    private lateinit var inputNp: TextInputEditText
    private lateinit var inputOp: TextInputEditText
    private lateinit var inputWp: TextInputEditText
    private lateinit var inputAp: TextInputEditText
    private lateinit var resultText: TextView
    private lateinit var errorText: TextView
    private lateinit var calculateButton: Button
    private lateinit var goToMainActivityButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_first)

        val firstView = findViewById<View>(R.id.first)

        // Застосування відступів вікна
        applyWindowInsets(firstView)

        // Налаштування прослуховувача дотиків
        setupTouchListener(firstView)

        // Ініціалізація полів вводу та кнопок
        inputHp = findViewById(R.id.input_edit_hp) // HP
        inputCp = findViewById(R.id.input_edit_cp) // CP
        inputSp = findViewById(R.id.input_edit_sp) // SP
        inputNp = findViewById(R.id.input_edit_np) // NP
        inputOp = findViewById(R.id.input_edit_op) // OP
        inputWp = findViewById(R.id.input_edit_wp) // WP
        inputAp = findViewById(R.id.input_edit_ap) // AP
        resultText = findViewById(R.id.result_text)
        errorText = findViewById(R.id.error_text)
        calculateButton = findViewById(R.id.calculate_button)
        goToMainActivityButton = findViewById(R.id.go_to_main_activity_button)

        val inputs = listOf(inputHp, inputCp, inputSp, inputNp, inputOp, inputWp, inputAp)

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
            "hp" to parseValueFromEditText(inputHp) { it.toDoubleOrNull() },
            "cp" to parseValueFromEditText(inputCp) { it.toDoubleOrNull() },
            "sp" to parseValueFromEditText(inputSp) { it.toDoubleOrNull() },
            "np" to parseValueFromEditText(inputNp) { it.toDoubleOrNull() },
            "op" to parseValueFromEditText(inputOp) { it.toDoubleOrNull() },
            "wp" to parseValueFromEditText(inputWp) { it.toDoubleOrNull() },
            "ap" to parseValueFromEditText(inputAp) { it.toDoubleOrNull() }
        )

        // Умова коректного введення користувача
        val total = inputValues.values.sum()
        if (total != 100.0) {
            errorText.text = "Помилка: Сума всіх компонентів повинна складати 100%"
            return
        }

        // Обчислення коефіцієнтів переходу
        val wp = inputValues["wp"]!!
        val krs = 100 / (100 - wp)
        val krg = 100 / (100 - wp - (inputValues["ap"]!!))

        // Склад сухої маси палива
        val dryMassComposition = inputValues.mapValues { it.value * krs }

        // Склад горючої маси палива
        val combustibleMassComposition = inputValues.mapValues { it.value * krg }

        // Нижча теплота згоряння для робочої маси
        val qph =
            (339 * inputValues["cp"]!! + 1030 * inputValues["hp"]!! - 108.8 * (inputValues["op"]!! - inputValues["sp"]!!) - 25 * wp) / 1000

        // Нижча теплота згоряння для сухої маси
        val qch = (qph + 0.025 * wp) * krs

        // Нижча теплота згоряння для горючої маси
        val qgh = (qph + 0.025 * wp) * krg

        // Виведення результатів
        resultText.text = """
        Коефіцієнт переходу від робочої до сухої маси: ${String.format("%.2f", krs)}
        Коефіцієнт переходу від робочої до горючої маси: ${String.format("%.2f", krg)}
        
        Склад сухої маси палива:
        HС: ${String.format("%.2f", dryMassComposition["hp"])}%
        CС: ${String.format("%.2f", dryMassComposition["cp"])}%
        SС: ${String.format("%.2f", dryMassComposition["sp"])}%
        NС: ${String.format("%.2f", dryMassComposition["np"])}%
        OС: ${String.format("%.2f", dryMassComposition["op"])}%
        AС: ${String.format("%.2f", dryMassComposition["ap"])}%
        
        Склад горючої маси палива:
        HГ: ${String.format("%.2f", combustibleMassComposition["hp"])}%
        CГ: ${String.format("%.2f", combustibleMassComposition["cp"])}%
        SГ: ${String.format("%.2f", combustibleMassComposition["sp"])}%
        NГ: ${String.format("%.2f", combustibleMassComposition["np"])}%
        OГ: ${String.format("%.2f", combustibleMassComposition["op"])}%
        
        Нижча теплота згоряння для робочої маси: ${String.format("%.4f", qph)} МДж/кг
        Нижча теплота згоряння для сухої маси: ${String.format("%.4f", qch)} МДж/кг
        Нижча теплота згоряння для горючої маси: ${String.format("%.4f", qgh)} МДж/кг
    """.trimIndent()
    }
}
