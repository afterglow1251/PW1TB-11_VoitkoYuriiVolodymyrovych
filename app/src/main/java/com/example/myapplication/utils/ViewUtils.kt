package com.example.myapplication.utils

import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

// Глобальна змінна для відстеження натисків кнопки обчислення
// Щоб помилки з'являлись лише тоді, коли користувач хоча б один раз натиснув кнопку "Обчислити" і поля пусті
// Тобто щоб помилки не з'являлись, коли користувач тільки відкрив сторінку (бо це нелогічно і незручно з точки зору UX)
private var hasClickedCalculate = false

// Функція для застосування віконних вставок до View
fun applyWindowInsets(view: View) {
    ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
        insets
    }
}

// Функція для налаштування слухача дотиків на View
fun setupTouchListener(view: View) {
    view.setOnTouchListener { v, event ->
        if (event.action == MotionEvent.ACTION_DOWN) {
            val currentFocus = v.rootView.findFocus()
            if (currentFocus is TextInputEditText) {
                currentFocus.clearFocus()
                val imm = v.context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(currentFocus.windowToken, 0)
            }
            v.performClick()
        }
        true
    }
}

// Функція для приховування клавіатури
fun hideKeyboard(view: View) {
    val imm = view.context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

// Функція для парсингу значення з TextInputEditText за допомогою заданого парсера
fun <T> parseValueFromEditText(editText: TextInputEditText, parser: (String) -> T?): T {
    return parser(editText.text.toString()) ?: throw IllegalArgumentException("Невалідні дані")
}

// Перевіряє, чи всі поля заповнені і встановлює помилку для порожніх
fun validateInputs(inputs: List<TextInputEditText>): Boolean {
    var isValid = true

    for (input in inputs) {
        if (hasClickedCalculate && input.text.isNullOrEmpty()) {
            input.error = "Поле не може бути порожнім"
            isValid = false
        } else {
            input.error = null
        }
    }

    return isValid
}

// Очищає текст помилки, якщо всі поля вводу валідні
fun setupInputValidation(inputs: List<TextInputEditText>, errorText: TextView) {
    inputs.forEach { input ->
        input.setOnKeyListener { _, _, _ ->
            if (validateInputs(inputs)) {
                errorText.text = "" // Занулити текст помилки, якщо всі поля валідні
            }
            false
        }
    }
}

// Налаштування обробника натискань для кнопки обчислення
fun setupCalculateButton(
    button: Button,
    inputs: List<TextInputEditText>,
    errorText: TextView,
    onCalculate: () -> Unit
) {
    button.setOnClickListener {
        hideKeyboard(button)

        hasClickedCalculate = true

        // Перевірка валідації
        if (validateInputs(inputs)) {
            errorText.text = ""
            onCalculate() // Викликаємо функцію обчислення
        } else {
            errorText.text = "Будь ласка, заповніть всі поля"
        }
    }
}

// Налаштування обробника переходу на іншу Activity
fun navigateToActivity(currentActivity: AppCompatActivity, targetActivity: Class<*>) {
    val intent = Intent(currentActivity, targetActivity)
    currentActivity.startActivity(intent)
    hasClickedCalculate = false // Скидаємо статус при переході
}

