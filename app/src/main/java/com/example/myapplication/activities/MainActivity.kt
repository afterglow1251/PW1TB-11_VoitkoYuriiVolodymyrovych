package com.example.myapplication.activities

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.content.Intent
import com.example.myapplication.R
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.utils.applyWindowInsets

class MainActivity : AppCompatActivity() {

    // Оголошення кнопок для переходу на інші активності
    private lateinit var firstActivityButton: Button
    private lateinit var secondActivityButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Дозвіл відображення контенту до країв екрану
        setContentView(R.layout.activity_main)

        // Отримуємо View основного макету по id
        val mainView = findViewById<View>(R.id.main)
        // Застосовуємо віконні вставки для адаптації макету до країв екрану
        applyWindowInsets(mainView)

        // Прив'язка кнопок до відповідних елементів у макеті
        firstActivityButton = findViewById(R.id.go_to_first_activity_button)
        secondActivityButton = findViewById(R.id.go_to_second_activity_button)

        // Обробник натискання для першої кнопки
        firstActivityButton.setOnClickListener {
            val intent = Intent(this, FirstActivity::class.java)
            startActivity(intent)
        }

        // Обробник натискання для другої кнопки
        secondActivityButton.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }
    }
}
