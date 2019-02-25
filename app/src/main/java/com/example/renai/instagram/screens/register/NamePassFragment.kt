package com.example.renai.instagram.screens.register

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.renai.instagram.R
import com.example.renai.instagram.screens.common.coordinateBtnAndInputs
import kotlinx.android.synthetic.main.fragment_register_namepass.*

// 2 - Full name, password, register button
class NamePassFragment : Fragment() {
    private lateinit var mListener: Listener

    interface Listener {
        fun onRegister(fullName: String, password: String)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register_namepass, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        coordinateBtnAndInputs(
            register_btn,
            register_name_input,
            register_password_input
        )
        register_btn.setOnClickListener {
            val fullName = register_name_input.text.toString()
            val password = register_password_input.text.toString()
            mListener.onRegister(fullName, password)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = context as Listener
    }
}