package com.restaurante.controleproducao.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun ConfirmDialog(
    titulo: String,
    mensagem: String,
    textoConfirmar: String = "Confirmar",
    textoCancelar: String = "Cancelar",
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text(titulo) },
        text = { Text(mensagem) },
        confirmButton = {
            TextButton(onClick = onConfirmar) { Text(textoConfirmar) }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text(textoCancelar) }
        }
    )
}
