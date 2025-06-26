package com.example.coffeeapp.ui.screens

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.coffeeapp.R
import com.example.coffeeapp.sign_in.AuthViewModel
import kotlinx.coroutines.launch

// Professional Color Scheme
private val CoffeePrimary = Color(0xFF8B4513)
private val CoffeeSecondary = Color(0xFFD2691E)
private val CoffeeAccent = Color(0xFFDEB887)
private val BackgroundGradientStart = Color(0xFFFAF6F0)
private val BackgroundGradientEnd = Color(0xFFF5F0E8)
private val TextPrimary = Color(0xFF2C1810)
private val TextSecondary = Color(0xFF6B4226)

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = viewModel(factory = AuthViewModel.Factory),
    onSignInSuccess: () -> Unit = {}
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val user by viewModel.user.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val signInError by viewModel.signInError.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(user) {
        user?.let {
            onSignInSuccess()
            navController.navigate("Splash") {
                popUpTo("Login") { inclusive = true }
            }
        }
    }

    LaunchedEffect(signInError) {
        signInError?.let { error ->
            coroutineScope.launch {
                snackbarHostState.showSnackbar(error)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(BackgroundGradientStart, BackgroundGradientEnd)
                    )
                )
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.9f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(40.dp),
                        color = CoffeePrimary,
                        strokeWidth = 4.dp
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    SignInContent(
                        navController = navController,
                        onSignInClick = { activity?.let { viewModel.signIn(it) } },
                        onSignInSuccess = onSignInSuccess,
                        snackbarHostState = snackbarHostState,
                        isLoading = isLoading,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Composable
private fun SignInContent(
    navController: NavController,
    onSignInClick: () -> Unit,
    onSignInSuccess: () -> Unit,
    snackbarHostState: SnackbarHostState,
    isLoading: Boolean,
    viewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            // Header Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Coffee Icon or Logo placeholder
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(CoffeePrimary, CoffeeSecondary)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "☕",
                        fontSize = 40.sp,
                        color = Color.White
                    )
                }

                Text(
                    text = "Coffee House",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Premium coffee experience",
                    fontSize = 16.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }

            // Form Section
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email",
                            tint = CoffeePrimary
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CoffeePrimary,
                        focusedLabelColor = CoffeePrimary,
                        cursorColor = CoffeePrimary
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Password",
                            tint = CoffeePrimary
                        )
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CoffeePrimary,
                        focusedLabelColor = CoffeePrimary,
                        cursorColor = CoffeePrimary
                    ),
                    singleLine = true
                )
            }

            // Login Button
            Button(
                onClick = {
                    coroutineScope.launch {
                        viewModel.signInWithEmail(email, password)
                    }
                },
                enabled = !isLoading && email.isNotBlank() && password.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CoffeePrimary,
                    contentColor = Color.White,
                    disabledContainerColor = CoffeePrimary.copy(alpha = 0.6f)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = "Sign In",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Register Button
            OutlinedButton(
                onClick = { navController.navigate("Register") },
                enabled = !isLoading,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = CoffeePrimary
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = "Create New Account",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Divider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = Color.Gray.copy(alpha = 0.3f)
                )
                Text(
                    text = "OR",
                    fontSize = 14.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = Color.Gray.copy(alpha = 0.3f)
                )
            }

            // Google Sign In Button
            OutlinedButton(
                onClick = onSignInClick,
                enabled = !isLoading,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = TextPrimary,
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.icon_google),
                        contentDescription = "Google Icon",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Continue with Google",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}