package ch.hslu.mobpro.timeismoney

import android.app.Activity
import android.app.Application
import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ch.hslu.mobpro.timeismoney.ui.theme.TimeIsMoneyTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    companion object {
        const val CHANNEL_ID = "ch.hslu.mobpro.timeismoney.channel"
        const val EXTRA_TASK = "TASK_NAME"
        const val EXTRA_TASK_ID = "TASK_ID"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        val currentUser = auth.currentUser
        var startDestination = "loginScreen"
        if (currentUser != null) {
            startDestination = "homeScreen"
        }
        setContent {
            TimeIsMoneyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val owner = LocalViewModelStoreOwner.current

                    owner?.let {
                        val viewModel: MainViewModel = viewModel(
                            it,
                            "MainViewModel",
                            MainViewModelFactory(
                                LocalContext.current.applicationContext as Application
                            , "")
                        )
                        TimeIsMoney(viewModel, startDestination, auth)
                    }

                }
            }
        }
    }
}

@Composable
fun LoginScreen(navController: NavController, viewModel: MainViewModel, auth: FirebaseAuth) {
    var isLogin by remember { mutableStateOf(true) }
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Hallo!", fontSize = 48.sp, fontWeight = FontWeight.Bold)
        Text(text = "Wir brauchen ein paar Angaben von dir", fontSize = 28.sp, textAlign = TextAlign.Center, lineHeight = 32.sp)
        Spacer(modifier = Modifier.height(16.dp))

        var email by remember { mutableStateOf("") }
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Password input field
        var password by remember { mutableStateOf("") }
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text = "Passwort") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Login button
        Button(
            onClick = {
                if (isLogin) {
                    if (email != "" && password != "") {
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(context as Activity) { task ->
                                if (task.isSuccessful) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success")
                                    val user = auth.currentUser
                                    if (user != null) {
                                        val pref: SharedPreferences =
                                            context.getApplicationContext().getSharedPreferences("USER_PREFERENCES", 0)
                                        var editor = pref.edit()
                                        editor.putString("user",user.uid)
                                        editor.commit()
                                        navController.navigate("homeScreen")
                                    }
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                                    Toast.makeText(
                                        context,
                                        "Authentifizierung fehlgeschlagen.",
                                        Toast.LENGTH_SHORT,
                                    ).show()
                                }
                            }
                    } else {
                        Toast.makeText(
                            context,
                            "Bitte fülle alle Felder aus.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                } else {
                    if (email != "" && password != "") {
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(context as Activity) { task ->
                                if (task.isSuccessful) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success")
                                    val user = auth.currentUser
                                    if (user != null) {
                                        val pref: SharedPreferences =
                                            context.getApplicationContext().getSharedPreferences("USER_PREFERENCES", 0)
                                        var editor = pref.edit()
                                        editor.putString("user",user.uid)
                                        editor.commit()
                                        navController.navigate("homeScreen")
                                    }
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                                    Toast.makeText(
                                        context,
                                        "Authentifizierung fehlgeschlagen.",
                                        Toast.LENGTH_SHORT,
                                    ).show()
                                }
                            }
                        } else {
                        Toast.makeText(
                            context,
                            "Bitte fülle alle Felder aus.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
            },
        ) {
            Text(text = if (isLogin) "ICH BIN ZURÜCK" else "LOS GEHTS")
        }
        ClickableText(onClick = {
                       isLogin = !isLogin
        }, text = AnnotatedString(if (isLogin) "Warte, ich habe noch keinen Account" else "Ich habe bereits einen Account" ))
    }
}


@Composable
fun TimeIsMoney(viewModel: MainViewModel, startDestination: String, auth: FirebaseAuth) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = startDestination) {
        composable("loginScreen") { LoginScreen(navController, viewModel, auth) }
        composable("homeScreen") { HomeScreen(navController, viewModel) }
        composable("overviewScreen") { OverviewScreen(navController, viewModel) }
        composable("settingScreen") { SettingScreen(navController, viewModel) }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TimeIsMoneyTheme {
        //TimeIsMoney(MainViewModel(Application()), "loginScreen")
    }
}