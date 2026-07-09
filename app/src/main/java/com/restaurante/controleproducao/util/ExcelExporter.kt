package com.restaurante.controleproducao.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.restaurante.controleproducao.data.local.entities.FechamentoEntity
import com.restaurante.controleproducao.data.local.entities.ProducaoEntity
import com.restaurante.controleproducao.domain.model.Turno
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * Exportador de Excel (.xlsx) escrito manualmente, gerando o pacote OOXML mínimo
 * (um zip com [Content_Types].xml, _rels, workbook e uma planilha em XML).
 *
 * Por quê não usar Apache POI: a biblioteca depende de classes de java.awt que não
 * existem no runtime do Android (ART), o que causa NoClassDefFoundError em tempo de
 * execução mesmo compilando sem erros. Este writer manual evita esse problema e
 * produz um .xlsx válido que abre normalmente no Excel, Google Sheets e LibreOffice.
 */
object ExcelExporter {

    fun gerarPlanilha(
        context: Context,
        producoes: List<ProducaoEntity>,
        fechamentos: List<FechamentoEntity>
    ): Uri {
        val pastaExports = File(context.getExternalFilesDir(null), "exports").apply { mkdirs() }
        val arquivo = File(pastaExports, "registros_producao_${DateUtils.hojeIso()}.xlsx")

        val cabecalho = listOf(
            "Data", "Turno", "Usuário", "Horário",
            "Uramaki Salmão", "Uramaki Shimeji", "Uramaki Skin", "Uramaki Grelhado",
            "Nigiri Salmão", "Nigiri Skin", "Jow", "Batera", "Total",
            "Lombo Restante (kg)", "Barriga Restante (kg)", "Observações Fechamento"
        )

        val linhas = mutableListOf<List<String>>()
        val datas = (producoes.map { it.data } + fechamentos.map { it.data }).toSortedSet()

        for (data in datas) {
            val fechamento = fechamentos.find { it.data == data }
            val doDia = producoes.filter { it.data == data }
            if (doDia.isEmpty()) {
                linhas.add(
                    listOf(
                        data, "", "", "", "", "", "", "", "", "", "", "", "",
                        fechamento?.lomboRestanteKg?.toString().orEmpty(),
                        fechamento?.barrigaRestanteKg?.toString().orEmpty(),
                        fechamento?.observacoes.orEmpty()
                    )
                )
            } else {
                doDia.forEachIndexed { index, p ->
                    linhas.add(
                        listOf(
                            data,
                            if (p.turno == Turno.MANHA) "Manhã" else "Noite",
                            p.nomeUsuario,
                            p.horario,
                            p.uramakiSalmao.toString(),
                            p.uramakiShimeji.toString(),
                            p.uramakiSkin.toString(),
                            p.uramakiGrelhado.toString(),
                            p.nigiriSalmao.toString(),
                            p.nigiriSkin.toString(),
                            p.jow.toString(),
                            p.batera.toString(),
                            p.total.toString(),
                            if (index == 0) fechamento?.lomboRestanteKg?.toString().orEmpty() else "",
                            if (index == 0) fechamento?.barrigaRestanteKg?.toString().orEmpty() else "",
                            if (index == 0) fechamento?.observacoes.orEmpty() else ""
                        )
                    )
                }
            }
        }

        escreverXlsx(arquivo, cabecalho, linhas)

        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", arquivo)
    }

    private fun escreverXlsx(arquivo: File, cabecalho: List<String>, linhas: List<List<String>>) {
        FileOutputStream(arquivo).use { fos ->
            ZipOutputStream(fos).use { zip ->
                zip.putNextEntry(ZipEntry("[Content_Types].xml"))
                zip.write(CONTENT_TYPES.toByteArray())
                zip.closeEntry()

                zip.putNextEntry(ZipEntry("_rels/.rels"))
                zip.write(RELS.toByteArray())
                zip.closeEntry()

                zip.putNextEntry(ZipEntry("xl/_rels/workbook.xml.rels"))
                zip.write(WORKBOOK_RELS.toByteArray())
                zip.closeEntry()

                zip.putNextEntry(ZipEntry("xl/workbook.xml"))
                zip.write(WORKBOOK.toByteArray())
                zip.closeEntry()

                zip.putNextEntry(ZipEntry("xl/worksheets/sheet1.xml"))
                zip.write(gerarSheetXml(cabecalho, linhas).toByteArray())
                zip.closeEntry()
            }
        }
    }

    private fun gerarSheetXml(cabecalho: List<String>, linhas: List<List<String>>): String {
        val sb = StringBuilder()
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>")
        sb.append("<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\"><sheetData>")

        fun escreverLinha(numero: Int, valores: List<String>) {
            sb.append("<row r=\"$numero\">")
            valores.forEachIndexed { colIndex, valor ->
                val ref = "${colLetra(colIndex)}$numero"
                val texto = escaparXml(valor)
                sb.append("<c r=\"$ref\" t=\"inlineStr\"><is><t xml:space=\"preserve\">$texto</t></is></c>")
            }
            sb.append("</row>")
        }

        escreverLinha(1, cabecalho)
        linhas.forEachIndexed { index, linha -> escreverLinha(index + 2, linha) }

        sb.append("</sheetData></worksheet>")
        return sb.toString()
    }

    private fun colLetra(indiceZeroBased: Int): String {
        var n = indiceZeroBased
        val sb = StringBuilder()
        do {
            sb.insert(0, ('A' + (n % 26)))
            n = n / 26 - 1
        } while (n >= 0)
        return sb.toString()
    }

    private fun escaparXml(texto: String): String = texto
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&apos;")

    fun compartilhar(context: Context, uri: Uri) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Compartilhar planilha Excel"))
    }

    private const val CONTENT_TYPES = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
<Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
<Default Extension="xml" ContentType="application/xml"/>
<Override PartName="/xl/workbook.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml"/>
<Override PartName="/xl/worksheets/sheet1.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml"/>
</Types>"""

    private const val RELS = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
<Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="xl/workbook.xml"/>
</Relationships>"""

    private const val WORKBOOK_RELS = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
<Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet" Target="worksheets/sheet1.xml"/>
</Relationships>"""

    private const val WORKBOOK = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<workbook xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships">
<sheets><sheet name="Produção" sheetId="1" r:id="rId1"/></sheets>
</workbook>"""
}
