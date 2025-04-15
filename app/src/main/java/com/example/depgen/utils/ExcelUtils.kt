package com.example.depgen.utils

import android.content.Context
import android.net.Uri
import com.example.depgen.ctxt
import com.example.depgen.model.EventComponent
import com.example.depgen.model.EventRole
import com.example.depgen.model.Profile
import com.example.depgen.toast
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFColor
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.time.LocalDate


const val COLUMN_WIDTH = 2048


fun generateDeploymentExcel(
    components: List<EventComponent>,
    rolesNeeded: Map<EventRole, Int>,
    cornerTitle: String = ""
): XSSFWorkbook {

    val deployments = HashMap<Profile, ArrayList<Pair<LocalDate, EventRole>>>()
    val colors = HashMap<EventRole, XSSFCellStyle>()
    val dates = HashSet<LocalDate>()
    var maxNameLength = 0

    components.forEach { component ->
        component.deployment.forEach {
            if (!deployments.containsKey(it.key)) deployments[it.key] = ArrayList()
            dates.add(component.getStart().toLocalDate())
            maxNameLength = maxOf(maxNameLength, it.key.username.length)
            deployments[it.key]!!.add(Pair(
                component.getStart().toLocalDate(),
                it.value.maxBy { role -> role.priority }
            ))
        }
    }

    val membersInvolved = deployments.keys.toList()
    val uniqueDates = (dates.toList() + NO_DATE_OBJ.toLocalDate()).sorted()

    val workbook = XSSFWorkbook()
    val sheet = workbook.createSheet("Deployment Timetable")
    val header = sheet.createRow(0)
    header.createCell(0).setCellValue(cornerTitle)

    for (role in rolesNeeded.keys) {
        colors[role] = workbook.createCellStyle().apply {
            fillPattern = FillPatternType.SOLID_FOREGROUND
            (this as XSSFCellStyle).setFillForegroundColor(XSSFColor(ByteArray(3) { role.color[it].toByte() }, null))
        }
    }

    components.forEachIndexed { ind, component ->
        header.createCell(ind + 1)
            .setCellValue(component.getStart().format(DATE_MONTH_FORMATTER))
    }

    membersInvolved.forEachIndexed { ind, member ->
        val row = sheet.createRow(ind + 1)
        row.createCell(0).setCellValue(member.username)
        deployments[member]!!.forEach {
            row.createCell(uniqueDates.indexOf(it.first)).apply {
                setCellValue(it.second.eventRole.substring(0, 1).uppercase())
                cellStyle = colors[it.second]!!
            }
        }
    }

    sheet.setColumnWidth(0, (maxNameLength + 2) * 256)
    for (i in 1..uniqueDates.size) sheet.setColumnWidth(i, COLUMN_WIDTH)

    return workbook
}

fun saveExcelFile(uri: Uri, workbook: XSSFWorkbook, context: Context = ctxt) {
    try {
        context.contentResolver.openOutputStream(uri)?.use {
            workbook.write(it)
            workbook.close()
            toast("Excel File Saved!")
        }

    } catch (e: Exception) {
        e.printStackTrace()
        toast("An Unexpected Error Occurred!")
    }
}
