package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldBorderDark
import com.example.ui.theme.EmeraldCardDark
import com.example.ui.theme.EmeraldDeepDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldSurfaceDark
import com.example.ui.theme.GoldAccent

@Composable
fun AccountGateScreen(
    onSignIn: (email: String, password: String) -> Pair<Boolean, String?>,
    onSignUp: (name: String, email: String, password: String) -> Pair<Boolean, String?>,
    modifier: Modifier = Modifier
) {
    var isSignUpMode by remember { mutableStateOf(false) }
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(EmeraldDeepDark, EmeraldSurfaceDark, EmeraldDeepDark)
                    )
                )
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .testTag("account_gate_screen"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 520.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 22.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Brand Crest
                Surface(
                    shape = CircleShape,
                    color = GoldAccent.copy(alpha = 0.16f),
                    border = BorderStroke(2.dp, GoldAccent),
                    modifier = Modifier.size(84.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.Mosque,
                            contentDescription = "صلاتي",
                            tint = GoldAccent,
                            modifier = Modifier.size(44.dp)
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "صلاتي",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GoldAccent
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "مرحباً بك، يرجى تسجيل الدخول أو إنشاء حساب للمتابعة",
                        fontSize = 13.sp,
                        color = Color(0xFFB7D1C6),
                        textAlign = TextAlign.Center
                    )
                }

                // Auth Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = EmeraldCardDark),
                    border = BorderStroke(1.dp, EmeraldBorderDark)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Mode Switcher: Sign In vs Sign Up
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(EmeraldDeepDark)
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            val tabSignInSelected = !isSignUpMode
                            Surface(
                                onClick = {
                                    isSignUpMode = false
                                    errorMessage = null
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = if (tabSignInSelected) GoldAccent else Color.Transparent,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("auth_tab_signin")
                            ) {
                                Text(
                                    text = "تسجيل الدخول",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (tabSignInSelected) Color.Black else Color(0xFFB7D1C6),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 10.dp)
                                )
                            }

                            Surface(
                                onClick = {
                                    isSignUpMode = true
                                    errorMessage = null
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = if (!tabSignInSelected) GoldAccent else Color.Transparent,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("auth_tab_signup")
                            ) {
                                Text(
                                    text = "إنشاء حساب",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (!tabSignInSelected) Color.Black else Color(0xFFB7D1C6),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 10.dp)
                                )
                            }
                        }

                        if (isSignUpMode) {
                            OutlinedTextField(
                                value = fullName,
                                onValueChange = {
                                    fullName = it
                                    errorMessage = null
                                },
                                label = { Text("الاسم الكامل") },
                                placeholder = { Text("أدخل اسمك") },
                                singleLine = true,
                                leadingIcon = {
                                    Icon(Icons.Filled.Person, contentDescription = null, tint = GoldAccent)
                                },
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("auth_name_field")
                            )
                        }

                        OutlinedTextField(
                            value = email,
                            onValueChange = {
                                email = it
                                errorMessage = null
                            },
                            label = { Text("البريد الإلكتروني") },
                            placeholder = { Text("example@email.com") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            leadingIcon = {
                                Icon(Icons.Filled.Email, contentDescription = null, tint = GoldAccent)
                            },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_email_field")
                        )

                        OutlinedTextField(
                            value = password,
                            onValueChange = {
                                password = it
                                errorMessage = null
                            },
                            label = { Text("كلمة المرور") },
                            placeholder = { Text("أدخل كلمة المرور") },
                            singleLine = true,
                            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            leadingIcon = {
                                Icon(Icons.Filled.Lock, contentDescription = null, tint = GoldAccent)
                            },
                            trailingIcon = {
                                IconButton(onClick = { showPassword = !showPassword }) {
                                    Icon(
                                        imageVector = if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                        contentDescription = "إظهار أو إخفاء كلمة المرور",
                                        tint = Color(0xFFB7D1C6)
                                    )
                                }
                            },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_password_field")
                        )

                        if (errorMessage != null) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.errorContainer,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.ErrorOutline,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onErrorContainer,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = errorMessage!!,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = {
                                val (ok, err) = if (isSignUpMode) {
                                    onSignUp(fullName, email, password)
                                } else {
                                    onSignIn(email, password)
                                }
                                if (!ok) {
                                    errorMessage = err ?: "يرجى التحقق من البيانات المدخلة"
                                }
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EmeraldLight,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("auth_submit_button")
                        ) {
                            Icon(
                                imageVector = if (isSignUpMode) Icons.Filled.PersonAdd else Icons.Filled.Login,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isSignUpMode) "إنشاء الحساب والدخول" else "تسجيل الدخول",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
