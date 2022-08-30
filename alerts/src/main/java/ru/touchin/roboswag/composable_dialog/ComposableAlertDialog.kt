package ru.touchin.roboswag.composable_dialog

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState

object ComposableAlertDialog {
    var customTitle: @Composable (() -> Unit)? = null
    var customMessage: @Composable (() -> Unit)? = null
    var customConfirmBtn: @Composable (() -> Unit)? = null
    var customNegativeBtn: @Composable (() -> Unit)? = null

    @Composable
    fun ShowAlertDialog(
            isDialogOpen: MutableState<Boolean>,
            title: String? = null,
            message: String? = null,
            positiveButtonText: String? = null,
            onPositiveAction: (() -> Unit)? = null,
            negativeBtnTitle: String? = null,
            onNegativeAction: (() -> Unit)? = null
    ) {
        if (isDialogOpen.value) {
            AlertDialog(
                    onDismissRequest = { isDialogOpen.value = false },

                    title = customTitle ?: { Text(title.orEmpty()) },
                    text = customMessage ?: { Text(message.orEmpty()) },

                    confirmButton = customConfirmBtn ?: {
                        TextButton(
                                onClick = {
                                    onPositiveAction?.invoke()
                                    isDialogOpen.value = false
                                }
                        ) {
                            Text(positiveButtonText.orEmpty())
                        }
                    },

                    dismissButton = when {
                        customNegativeBtn != null -> customNegativeBtn
                        else -> {
                            negativeBtnTitle?.let { positiveText ->
                                {
                                    TextButton(
                                            onClick = {
                                                onNegativeAction?.invoke()
                                                isDialogOpen.value = false
                                            }
                                    ) {
                                        Text(positiveText)
                                    }
                                }
                            }
                        }
                    }
            )
        }
    }
}
