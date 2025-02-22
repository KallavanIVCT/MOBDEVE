package com.example.mobdeve

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.mobdeve.databinding.LoginMenuBinding


class LoginActivity : AppCompatActivity() {
    private lateinit var viewBinding: LoginMenuBinding
    private lateinit var dbHelper: DBHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        dbHelper = DBHelper(this,null)
        //dbHelper.createUser("james", "bond");


        viewBinding = LoginMenuBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.btnLogin.setOnClickListener(){
            val input_username : String = viewBinding.etLoginUsername.text.toString()
            val input_password : String = viewBinding.etLoginPassword.text.toString()
            loginValidation(input_username, input_password)
        }
        viewBinding.btnSignup.setOnClickListener(){
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

    }

    private fun loginValidation(input_username: String, input_password: String){
        if (input_password.isEmpty() && input_username.isEmpty()){
            Toast.makeText(this, "Please enter a username and a password", Toast.LENGTH_SHORT).show()
        }
        else if (input_username.isEmpty()){
            Toast.makeText(this, "Please enter a proper username", Toast.LENGTH_SHORT).show()
        }
        else if (input_password.isEmpty()){
            Toast.makeText(this, "Please enter a proper password", Toast.LENGTH_SHORT).show()
        }else{
            val specificUser: UserModel? = dbHelper.verifyLogin(input_username, input_password)
            if(specificUser != null){

                //
                val sharedPreferences = getSharedPreferences("mainPreference", Context.MODE_PRIVATE)
                with(sharedPreferences.edit()) {
                    putLong("USER_ID", specificUser.id)
                    apply()
                }



                val intent = Intent(this, MainTodoActivity::class.java)
                intent.putExtra("user_username", input_username)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK) // this is so that when the user goes to addtodo the previous activity are deleted
                startActivity(intent)
            }else{
                Toast.makeText(this, "Wrong username/password, Please Try Again", Toast.LENGTH_SHORT).show()
            }

        }
    }
}