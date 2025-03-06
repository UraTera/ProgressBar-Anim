package com.tera.progressbar_anim

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.tera.progressbar_anim.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var keyAnim = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setStart()
        initButtons()
    }

    private fun setStart() = with(binding) {
        hidePb()
        pb2.animation = false
        pb3.animation = false
        pb6.animation = false
    }

    private fun initButtons() = with(binding) {
        bnStart.setOnClickListener {
            keyAnim = !keyAnim
            pb2.animation = keyAnim
            pb3.animation = keyAnim
            pb6.animation = keyAnim

            if (keyAnim) showPb()
            else hidePb()
        }
    }

    private fun hidePb() = with(binding) {
        pb1.visibility = View.INVISIBLE
        pb4.visibility = View.INVISIBLE
        pb5.visibility = View.INVISIBLE
    }

    private fun showPb() = with(binding) {
        pb1.visibility = View.VISIBLE
        pb4.visibility = View.VISIBLE
        pb5.visibility = View.VISIBLE
    }

}