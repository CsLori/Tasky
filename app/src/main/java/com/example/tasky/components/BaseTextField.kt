package com.example.tasky.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.tasky.ui.theme.AppTheme
import com.example.tasky.util.ErrorStatus
import com.example.tasky.util.FieldInput
import com.example.tasky.util.IconResource

@Composable
fun BaseTextField(
    modifier: Modifier,
    fieldInput: FieldInput,
    errorStatus: ErrorStatus,
    placeholderValue: String? = null,
    textStyle: TextStyle = AppTheme.typography.textFieldString.copy(color = AppTheme.colors.darkGray),
    keyboardOptions: KeyboardOptions,
    isPasswordField: Boolean = false,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    placeholder: @Composable (() -> Unit)? = if (placeholderValue != null) {
        { BasePlaceholder(text = placeholderValue) }
    } else null,
    onValueChange: (String) -> Unit,
    shape: Shape = RoundedCornerShape(10.dp),
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = AppTheme.colors.darkGray,
        unfocusedTextColor = AppTheme.colors.darkGray,
        errorTextColor = AppTheme.colors.darkGray,
        focusedBorderColor = AppTheme.colors.lightBlue,
        unfocusedBorderColor = AppTheme.colors.transparent,
        disabledBorderColor = AppTheme.colors.transparent,
        errorTrailingIconColor = AppTheme.colors.black,
        focusedContainerColor = AppTheme.colors.light2,
        unfocusedContainerColor = AppTheme.colors.light2,
        errorContainerColor = AppTheme.colors.light2,
        disabledContainerColor = AppTheme.colors.light2,
    ),
) {

    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        modifier = modifier,
        value = fieldInput.value,
        onValueChange = {
            onValueChange(it)
        },
        singleLine = true,
        textStyle = textStyle,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        placeholder = placeholder,
        isError = fieldInput.hasInteracted && errorStatus.isError,
        supportingText = {
            if (fieldInput.hasInteracted && errorStatus.isError) {
                errorStatus.errorMsg?.let {
                    Text(
                        text = it.asString(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = AppTheme.dimensions.small8dp),
                        style = AppTheme.typography.labelSmall,
                    )
                }
            }
        },
        trailingIcon = {
            if (isPasswordField) {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                        tint = AppTheme.colors.gray
                    )
                }
            } else if (fieldInput.hasInteracted && !errorStatus.isError) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = AppTheme.colors.green
                )
            }
        },
        visualTransformation = if (isPasswordField && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        shape = shape,
        colors = colors
    )
}

@Composable
fun BasePlaceholder(text: String) {
    Text(
        text = text,
        style = AppTheme.typography.textFieldString,
        color = AppTheme.colors.gray
    )
}