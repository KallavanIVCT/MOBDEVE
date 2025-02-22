package com.example.mobdeve

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mobdeve.databinding.SignupMenuBinding

class SignupActivity : AppCompatActivity() {
    private lateinit var dbHelper: DBHelper
    private lateinit var viewBinding: SignupMenuBinding
    override fun onCreate(savedInstanceState: Bundle?) {

        dbHelper = DBHelper(this ,null)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewBinding = SignupMenuBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.btnSignup.setOnClickListener(){
            val input_username : String = viewBinding.etSignupUsername.text.toString()
            val input_password : String = viewBinding.etSignupPassword.text.toString()
            signupValidation(input_username, input_password)
        }


    }
    private fun signupValidation(input_username: String, input_password: String){
        if (input_password.isEmpty() && input_username.isEmpty()){
            Toast.makeText(this, "Please enter a username and a password", Toast.LENGTH_SHORT).show()
        }
        else if (input_username.isEmpty()){
            Toast.makeText(this, "Please enter a proper username", Toast.LENGTH_SHORT).show()
        }
        else if (input_password.isEmpty()){
            Toast.makeText(this, "Please enter a proper password", Toast.LENGTH_SHORT).show()
        }else{
            val result = dbHelper.createUser(input_username, input_password)
            if(result){
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }else{
                Toast.makeText(this, "Invalid Username and Password, Please Try Again", Toast.LENGTH_SHORT).show()
            }

        }
    }
}