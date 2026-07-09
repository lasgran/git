package com.restaurante.controleproducao.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import com.restaurante.controleproducao.data.local.entities.FechamentoEntity
import com.restaurante.controleproducao.data.local.entities.ProducaoEntity
import com.restaurante.controleproducao.domain.model.ItemProducao
import com.restaurante.controleproducao.domain.model.Turno
import java.io.File
import java.io.FileOutputStream

/**
 * Gera um relatório em PDF com android.graphics.pdf.PdfDocument (nativo do Android,
 * sem dependências externas) contendo, por data: produção da manhã, da noite e o
 * fechamento do dia (lombo/barriga restantes, usuários e horários).
 */
object PdfExporter {

    private const val LARGURA_PAGINA = 595 // A4 em pontos, 72dpi
    private const val ALTURA_PAGINA = 842
    private const val MARGEM = 40f

    fun gerarRelatorio(
        context: Context,
        producoes: List<ProducaoEntity>,
        fechamentos: List<FechamentoEntity>
    ): Uri {
        val documento = PdfDocument()
        val datas = (producoes.map { it.data } + fechamentos.map { it.data }).toSortedSet()

        val paintTitulo = Paint().apply { textSize = 18f; typeface = Typeface.DEFAULT_BOLD }
        val paintSubtitulo = Paint().apply { textSize = 13f; typeface = Typeface.DEFAULT_BOLD }
        val paintTexto = Paint().apply { textSize = 11f }
        val paintRodape = Paint().apply { textSize = 9f; color = android.graphics.Color.GRAY }

        var pagina: PdfDocument.Page? = null
        var canvas: Canvas? = null
        var y = 0f
        var numeroPagina = 0

        fun novaPagina() {
            pagina?.let { documento.finishPage(it) }
            numeroPagina++
            val info = PdfDocument.PageInfo.Builder(LARGURA_PAGINA, ALTURA_PAGINA, numeroPagina).create()
            pagina = documento.startPage(info)
            canvas = pagina!!.canvas
            y = MARGEM
            canvas!!.drawText("Relatório de Produção — Controle de Produção", MARGEM, y, paintTitulo)
            y += 28f
        }

        fun garantirEspaco(altura: Float) {
            if (y + altura > ALTURA_PAGINA - MARGEM) novaPagina()
        }

        novaPagina()

        if (datas.isEmpty()) {
            canvas!!.drawText("Nenhum registro encontrado.", MARGEM, y, paintTexto)
        }

        for (data in datas) {
            garantirEspaco(90f)
            canvas!!.drawText(DateUtils.isoParaDiaSemanaEData(data), MARGEM, y, paintSubtitulo)
            y += 18f

            val manha = producoes.find { it.data == data && it.turno == Turno.MANHA }
            val noite = producoes.find { it.data == data && it.turno == Turno.NOITE }
            val fechamento = fechamentos.find { it.data == data }

            manha?.let { p ->
                garantirEspaco(84f)
                canvas!!.drawText("Manhã — usuário: ${p.nomeUsuario}  às ${p.horario}", MARGEM, y, paintTexto)
                y += 14f
                val (linha1, linha2) = resumoItens(p)
                canvas!!.drawText(linha1, MARGEM + 8f, y, paintTexto)
                y += 14f
                canvas!!.drawText(linha2, MARGEM + 8f, y, paintTexto)
                y += 14f
                canvas!!.drawText("Total: ${p.total}", MARGEM + 8f, y, paintTexto)
                y += 16f
            }

            noite?.let { p ->
                garantirEspaco(84f)
                canvas!!.drawText("Noite — usuário: ${p.nomeUsuario}  às ${p.horario}", MARGEM, y, paintTexto)
                y += 14f
                val (linha1, linha2) = resumoItens(p)
                canvas!!.drawText(linha1, MARGEM + 8f, y, paintTexto)
                y += 14f
                canvas!!.drawText(linha2, MARGEM + 8f, y, paintTexto)
                y += 14f
                canvas!!.drawText("Total: ${p.total}", MARGEM + 8f, y, paintTexto)
                y += 16f
            }

            fechamento?.let { f ->
                garantirEspaco(50f)
                canvas!!.drawText(
                    "Fechamento — lombo: ${f.lomboRestanteKg}kg, barriga: ${f.barrigaRestanteKg}kg " +
                        "(${f.nomeUsuario} às ${f.horario})",
                    MARGEM, y, paintTexto
                )
                y += 16f
            }

            y += 8f
            canvas!!.drawLine(MARGEM, y, LARGURA_PAGINA - MARGEM, y, paintRodape)
            y += 14f
        }

        pagina?.let { documento.finishPage(it) }

        val pastaExports = File(context.getExternalFilesDir(null), "exports").apply { mkdirs() }
        val arquivo = File(pastaExports, "relatorio_producao_${DateUtils.hojeIso()}.pdf")
        FileOutputStream(arquivo).use { documento.writeTo(it) }
        documento.close()

        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", arquivo)
    }

    private fun resumoItens(p: ProducaoEntity): Pair<String, String> {
        val linha1 = "${ItemProducao.URAMAKI_SALMAO.label}: ${p.uramakiSalmao}  " +
            "${ItemProducao.URAMAKI_SHIMEJI.label}: ${p.uramakiShimeji}  " +
            "${ItemProducao.URAMAKI_SKIN.label}: ${p.uramakiSkin}  " +
            "${ItemProducao.URAMAKI_GRELHADO.label}: ${p.uramakiGrelhado}"
        val linha2 = "${ItemProducao.NIGIRI_SALMAO.label}: ${p.nigiriSalmao}  " +
            "${ItemProducao.NIGIRI_SKIN.label}: ${p.nigiriSkin}  " +
            "${ItemProducao.JOW.label}: ${p.jow}  " +
            "${ItemProducao.BATERA.label}: ${p.batera}"
        return linha1 to linha2
    }

    fun compartilhar(context: Context, uri: Uri) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Compartilhar relatório PDF"))
    }
}
