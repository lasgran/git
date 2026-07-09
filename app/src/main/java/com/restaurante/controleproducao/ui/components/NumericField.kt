package com.restaurante.controleproducao.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

/**
 * Campo numérico grande com botões +/- e teclado numérico — pensado para ser
 * preenchido rapidamente por funcionários em pé, no balcão.
 */
@Composable
fun NumericField(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                label,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = { if (value > 0) onValueChange(value - 1) },
                colors = IconButtonDefaults.filledTonalIconButtonColors()
            ) {
                Icon(Icons.Filled.Remove, contentDescription = "Diminuir")
            }

            OutlinedTextField(
                value = if (value == 0) "" else value.toString(),
                onValueChange = { texto ->
                    val filtrado = texto.filter { it.isDigit() }
                    onValueChange(filtrado.toIntOrNull() ?: 0)
                },
                placeholder = { Text("0") },
                singleLine = true,
                textStyle = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .width(84.dp)
                    .padding(horizontal = 8.dp)
            )

            IconButton(
                onClick = { onValueChange(value + 1) },
                colors = IconButtonDefaults.filledIconButtonColors()
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Aumentar")
            }
        }
    }
}
