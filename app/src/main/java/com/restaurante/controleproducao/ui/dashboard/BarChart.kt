package com.restaurante.controleproducao.ui.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Gráfico de barras simples desenhado com Canvas — evita depender de uma
 * biblioteca externa de gráficos apenas para totais semanais/mensais.
 */
@Composable
fun BarChart(
    dados: List<Pair<String, Int>>,
    modifier: Modifier = Modifier,
    corBarra: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary
) {
    val maximo = (dados.maxOfOrNull { it.second } ?: 0).coerceAtLeast(1)
    val corTexto = MaterialTheme.colorScheme.onSurface

    Column(modifier = modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(horizontal = 4.dp)
        ) {
            if (dados.isEmpty()) return@Canvas
            val larguraBarra = size.width / (dados.size * 2f)
            val alturaMaxima = size.height - 24.dp.toPx()

            dados.forEachIndexed { index, (label, valor) ->
                val alturaBarra = (valor.toFloat() / maximo) * alturaMaxima
                val x = (index * 2 + 0.5f) * larguraBarra
                drawRect(
                    color = corBarra,
                    topLeft = androidx.compose.ui.geometry.Offset(x, alturaMaxima - alturaBarra),
                    size = androidx.compose.ui.geometry.Size(larguraBarra, alturaBarra)
                )
                drawIntoCanvas { canvas ->
                    val paint = android.graphics.Paint().apply {
                        color = corTexto.toArgb()
                        textSize = 11.sp.toPx()
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                    canvas.nativeCanvas.drawText(
                        label,
                        x + larguraBarra / 2,
                        size.height - 4.dp.toPx(),
                        paint
                    )
                    canvas.nativeCanvas.drawText(
                        valor.toString(),
                        x + larguraBarra / 2,
                        alturaMaxima - alturaBarra - 4.dp.toPx(),
                        paint
                    )
                }
            }
        }
    }
}
