package com.example.depgen.view.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp

@Composable
fun BoldTextParser(text: String) {
    val annotatedText = buildAnnotatedString {
        val regex = Regex("\\*\\*(.*?)\\*\\*")
        var currentIndex = 0

        regex.findAll(text).forEach { match ->
            val start = match.range.first
            val end = match.range.last + 1

            if (currentIndex < start) {
                append(text.substring(currentIndex, start))
            }

            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append(match.groupValues[1])
            }

            currentIndex = end
        }

        if (currentIndex < text.length) {
            append(text.substring(currentIndex, text.length))
        }
    }

    Text(
        text = annotatedText,
        style = MaterialTheme.typography.bodyMedium,
        fontSize = 16.sp
    )
}
