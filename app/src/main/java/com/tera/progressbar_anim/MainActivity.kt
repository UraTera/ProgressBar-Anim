package com.tera.progressbar_anim

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.tera.progressbar_anim.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object{
        const val KEY_ANIM = "key_anim"
    }

    private lateinit var binding: ActivityMainBinding
    private var keyAnim = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setState()
        initButtons()
    }

    private fun initButtons() = with(binding) {
        bnStart.setOnClickListener {
            keyAnim = !keyAnim
            setState()
        }
    }

    private fun setState() = with(binding) {
        pb2.animation = keyAnim
        pb3.animation = keyAnim
        pb6.animation = keyAnim
        pb7.animation = keyAnim

        if (keyAnim) showPb()
        else hidePb()
    }

    private fun hidePb() = with(binding) {
        pb1.visibility = View.INVISIBLE
        pb4.visibility = View.INVISIBLE
        pb5.visibility = View.INVISIBLE
        pb8.visibility = View.INVISIBLE
    }

    private fun showPb() = with(binding) {
        pb1.visibility = View.VISIBLE
        pb4.visibility = View.VISIBLE
        pb5.visibility = View.VISIBLE
        pb8.visibility = View.VISIBLE
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        keyAnim = savedInstanceState.getBoolean(KEY_ANIM)
        setState()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_ANIM, keyAnim)
    }

}