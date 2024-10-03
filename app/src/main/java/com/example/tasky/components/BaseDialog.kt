package com.example.tasky.components

import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.HorizontalAlignmentLine
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.tasky.ui.theme.AppTheme
import java.util.Locale

@ExperimentalComposeUiApi
@Composable
fun BaseDialog(
    title: String? = null,
    label: String,
    clickableLabel: AnnotatedString = AnnotatedString(""),
    displayCloseIcon: Boolean = false,
    positiveButtonText: String,
    positiveOnClick: () -> Unit,
    onCancelClicked: (() -> Unit)? = null,
    negativeButtonText: String? = null,
    negativeOnClick: (() -> Unit)? = null,
    clickableLabelOnClick: ((String) -> Unit)? = null,
) {
    val context = LocalContext.current
    AppTheme {
        Dialog(
            onDismissRequest = { onCancelClicked?.invoke() },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                color = AppTheme.colors.white
            )
            {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        title?.uppercase(Locale.getDefault())?.let {
                            Text(
                                modifier = Modifier.padding(bottom = 18.dp),
                                text = it,
                                style = AppTheme.typography.bodyMedium
                            )
                        }

                        if (displayCloseIcon) {
                            IconButton(
                                modifier = Modifier.size(24.dp),
                                onClick = { onCancelClicked?.invoke() }) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = null,
                                    tint = AppTheme.colors.black
                                )
                            }
                        }
                    }
                    if (clickableLabel.isNotEmpty()) {
                        ClickableText(
                            text = clickableLabel,
                            onClick = { offset ->
                                clickableLabel.getStringAnnotations(
                                    tag = "URL",
                                    start = offset,
                                    end = offset
                                )
                                    .firstOrNull()?.let { annotation ->
                                        if (clickableLabelOnClick != null) {
                                            clickableLabelOnClick(annotation.item)
                                        }
                                    }
                            },
                            style = AppTheme.typography.bodyMedium,
                        )
                    } else {
                        Text(
                            text = label,
                            style = AppTheme.typography.bodyMedium,
                            lineHeight = 20.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(AppTheme.dimensions.default16dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        negativeButtonText?.let {
                            BaseButton(
                                modifier = Modifier
                                    .fillMaxWidth(0.5f),
                                btnString = negativeButtonText,
                                onClick = {
                                    if (negativeOnClick != null) {
                                        negativeOnClick()
                                    }
                                },
                                textStyle = AppTheme.typography.buttonText
                            )

                        }
                        BaseButton(
                            modifier = Modifier
                                .fillMaxWidth(0.3f),
                            btnString = positiveButtonText,
                            onClick = { positiveOnClick() },
                            textStyle = AppTheme.typography.buttonText
                        )
                    }
                }
            }
        }
    }
}