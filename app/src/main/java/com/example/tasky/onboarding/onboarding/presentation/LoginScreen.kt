package com.example.tasky.onboarding.onboarding.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tasky.R
import com.example.tasky.Screen
import com.example.tasky.components.BaseButton
import com.example.tasky.components.BaseTextField
import com.example.tasky.ui.theme.AppTheme
import com.example.tasky.ui.theme.AppTheme.colors
import com.example.tasky.ui.theme.AppTheme.dimensions
import com.example.tasky.ui.theme.AppTheme.typography
import com.example.tasky.util.ErrorStatus
import com.example.tasky.util.FieldInput

@Composable
internal fun LoginScreen(navController: NavController) {
    LoginContent(onSignUpClick = { navController.navigate(Screen.Register) })
}

@Composable
fun LoginContent(
    onSignUpClick: () -> Unit
) {

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
                text = stringResource(R.string.Welcome_Back),
                style = typography.title,
                textAlign = TextAlign.Center,
                color = colors.white,
                modifier = Modifier.padding(bottom = dimensions.large32dp)
            )
        }

        Surface(
            shape = RoundedCornerShape(
                topStart = 30.dp,
                topEnd = 30.dp
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
                    .padding(top = 40.dp)
                    .padding(horizontal = dimensions.default16dp)
            ) {
                BaseTextField(
                    modifier = Modifier.fillMaxWidth(),
                    fieldInput = FieldInput("Lori"),
                    errorStatus = ErrorStatus(false),
                    placeholderValue = stringResource(R.string.Email_address),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    onValueChange = {}
                )

                Spacer(modifier = Modifier.height(dimensions.extraSmall4dp))

                BaseTextField(
                    modifier = Modifier.fillMaxWidth(),
                    fieldInput = FieldInput("lori@boohoo.com"),
                    errorStatus = ErrorStatus(false),
                    placeholderValue = stringResource(R.string.Password),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                    onValueChange = {}
                )

                Spacer(modifier = Modifier.height(dimensions.large32dp))

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    BaseButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {},
                        btnString = stringResource(R.string.Log_in).uppercase(),
                        textStyle = typography.buttonText
                    )

                    Text(
                        text = "Don't have an account? Sign up".uppercase(),
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .clickable { onSignUpClick() },
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W500,
                            lineHeight = 30.sp
                        )
                    )
                }
            }
        }
    }
}

@Preview(name = "Pixel 3", device = Devices.PIXEL_3)
@Preview(name = "Pixel 6", device = Devices.PIXEL_6)
@Preview(name = "Pixel 7 PRO", device = Devices.PIXEL_7_PRO)
@Composable
fun LoginScreenPreview() {
    AppTheme {
        LoginContent(onSignUpClick = {})
    }
}