package com.example.tasky.onboarding.onboarding.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tasky.ui.theme.AppTheme
import com.example.tasky.ui.theme.AppTheme.colors
import com.example.tasky.ui.theme.AppTheme.dimensions
import com.example.tasky.ui.theme.AppTheme.typography
import com.example.tasky.Constants.EMAIL_ADDRESS
import com.example.tasky.Constants.GET_STARTED
import com.example.tasky.Constants.NAME
import com.example.tasky.Constants.PASSWORD
import com.example.tasky.Constants.WELCOME_BACK
import com.example.tasky.components.BaseButton
import com.example.tasky.components.BaseTextField

@Composable
internal fun RegisterScreen() {
    RegisterContent()
}

@Composable
fun RegisterContent() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(colors.black),
            contentAlignment = Alignment.Center
        )
        {
            Text(
                text = WELCOME_BACK,
                style = typography.title,
                textAlign = TextAlign.Center,
                color = colors.white,
                modifier = Modifier.padding(bottom = dimensions.large32dp)
            )
        }

        Surface(
            shape = RoundedCornerShape(
                topStart = dimensions.large24dp,
                topEnd = dimensions.large24dp
            ),
            color = colors.white,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(top = 150.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(top = dimensions.large32dp)
                    .padding(horizontal = dimensions.default16dp)
            ) {
                BaseTextField(
                    modifier = Modifier.padding(dimensions.small8dp),
                    state = name,
                    onValueChange = { name = it },
                    singleLine = true,
                    placeholder = { Text(NAME) },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "",
                            tint = colors.green
                        )
                    },
                )

                Spacer(modifier = Modifier.height(dimensions.default16dp))

                BaseTextField(
                    modifier = Modifier.padding(dimensions.small8dp),
                    state = email,
                    onValueChange = { email = it },
                    singleLine = true,
                    placeholder = { Text(EMAIL_ADDRESS) },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "",
                            tint = colors.green
                        )
                    },
                )

                Spacer(modifier = Modifier.height(dimensions.default16dp))

                BaseTextField(
                    modifier = Modifier.padding(dimensions.small8dp),
                    state = password,
                    onValueChange = { password = it },
                    singleLine = true,
                    placeholder = { Text(PASSWORD) },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.VisibilityOff,
                            contentDescription = "",
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                )

                Spacer(modifier = Modifier.height(dimensions.large32dp))

                BaseButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {},
                    btnString = GET_STARTED.uppercase(),
                    textStyle = AppTheme.typography.buttonText
                )
            }
        }
    }
}

@Preview(name = "Pixel 3", device = Devices.PIXEL_3)
@Preview(name = "Pixel 6", device = Devices.PIXEL_6)
@Preview(name = "Pixel 7 PRO", device = Devices.PIXEL_7_PRO)
@Composable
fun RegisterScreenPreview() {
    AppTheme {
        RegisterScreen()
    }
}