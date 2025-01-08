package com.example.test_app

import com.alibaba.excel.EasyExcel
import com.alibaba.excel.ExcelWriter
import com.alibaba.excel.write.metadata.WriteSheet
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@RestController
class CustomFieldsController {

    private val logger: Logger = LoggerFactory.getLogger(CustomFieldsController::class.java)

    @GetMapping("/write-to-excel")
    fun writeToExcel(): String {
        logger.info("writeToExcel endpoint called")

        val excelFilePath = "/Users/mercymanyange/Downloads/mickeillah.xlsx"
        val customFields = readCustomFields()

        if (customFields.isEmpty()) {
            logger.warn("No custom fields found to write to Excel.")
            return "No data to write to Excel."
        }

        return try {
            // Write to Excel file
            FileOutputStream(File(excelFilePath)).use { fileOutputStream ->
                val excelWriter: ExcelWriter = EasyExcel.write(fileOutputStream).build()
                val writeSheet: WriteSheet = EasyExcel.writerSheet("mickeillah").build()
                logger.info("Writing custom fields to Excel...")

                // Write the data to the specified sheet
                excelWriter.write(customFields, writeSheet)
                excelWriter.finish()
            }
            logger.info("Data written to Excel file successfully!")

            // Returning success message
            "Data written to Excel file successfully!"

        } catch (e: IOException) {
            logger.error("Error writing to Excel file: {}", e.message)
            "Error writing to Excel file: ${e.message}"
        }
    }

    fun readCustomFields(): List<CustomField> {
        val filePath = "/Users/mercymanyange/Downloads/test-app/src/main/resources/custom-fields.txt"
        return try {
            val lines = Files.readAllLines(Paths.get(filePath))
            lines.mapNotNull { line ->
                val parts = line.split(",")
                if (parts.size < 8) {
                    logger.warn("Invalid line in custom-fields.txt: {}", line)
                    null // Skip invalid lines
                } else {
                    CustomField(
                        name = parts[0].trim(),
                        description = parts[1].trim(),
                        title = parts[2].trim(),
                        sender = parts[3].trim(),
                        url = parts[4].trim(),
                        home = parts[5].trim(),
                        supportEmail = parts[6].trim(),
                        supportNumber = parts[7].trim()
                    )
                }
            }
        } catch (e: IOException) {
            logger.error("Error reading custom fields file: {}", e.message)
            throw RuntimeException("Error reading custom fields file", e)
        }
    }

    @GetMapping("/download-excel")
    fun downloadExcel(): ResponseEntity<ByteArray> {
        val filePath = "/Users/mercymanyange/Downloads/mickeillah.xlsx"
        val file = Path.of(filePath)

        return if (Files.exists(file)) {
            val data = Files.readAllBytes(file)
            ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=mickeillah.xlsx")
                .body(data)
        } else {
            ResponseEntity.notFound().build()
        }
    }
}

data class CustomField(
    val name: String,
    val description: String,
    val title: String,
    val sender: String,
    val url: String,
    val home: String,
    val supportEmail: String,
    val supportNumber: String
)
