package com.example.ksaturno

import android.content.Intent // Added this import
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class login : AppCompatActivity() {

    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Retrofit Service
        apiService = RetrofitClient.instance

        val editTextUsername = findViewById<EditText>(R.id.email_edit_text)
        val editTextPassword = findViewById<EditText>(R.id.password_edit_text)
        val buttonLogin = findViewById<Button>(R.id.login_button)

        buttonLogin.setOnClickListener {
            val username = editTextUsername.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                loginUser(username, password)
            } else {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginUser(usernameString: String, passwordString: String) {
        lifecycleScope.launch {
            try {
                val request = LoginRequest(username = usernameString, password = passwordString)
                val response = apiService.login(request)

                if (response.isSuccessful) {
                    val loginApiResponse = response.body()
                    if (loginApiResponse != null) {
                        if (loginApiResponse.success) {
                            // Login successful
                            val userId = loginApiResponse.user?.id
                            val userName = loginApiResponse.user?.username
                            Toast.makeText(this@login, "Login Successful! User ID: $userId, Username: $userName", Toast.LENGTH_LONG).show()
                            
                            // Navigate to MainActivity
                            val intent = Intent(this@login, MainActivity::class.java)
                            startActivity(intent)
                            finish() // Finish login activity so user can't go back

                        } else {
                            // Login failed by API
                            Toast.makeText(this@login, "Login Failed: ${loginApiResponse.message}", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        // Empty response body
                        Toast.makeText(this@login, "Login error: Empty response body", Toast.LENGTH_LONG).show()
                        Log.e("LoginError", "Successful HTTP response but empty body")
                    }
                } else {
                    // HTTP call not successful
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(this@login, "Login Error: ${response.code()} ${response.message()} - $errorBody", Toast.LENGTH_LONG).show()
                    Log.e("LoginError", "HTTP Error: ${response.code()} ${response.message()} - $errorBody")
                }
            } catch (e: HttpException) {
                Toast.makeText(this@login, "Error: ${e.message()}", Toast.LENGTH_LONG).show()
                Log.e("LoginError", "HttpException: ", e)
            } catch (e: IOException) {
                Toast.makeText(this@login, "Network Error: Please check your connection", Toast.LENGTH_LONG).show()
                Log.e("LoginError", "IOException: ", e)
            } catch (e: Exception) {
                Toast.makeText(this@login, "An unexpected error occurred: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                Log.e("LoginError", "Exception: ", e)
            }
        }
    }
}