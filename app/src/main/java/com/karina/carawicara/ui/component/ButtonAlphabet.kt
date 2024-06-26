package com.karina.carawicara.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ButtonAlphabet(
    onClick: () -> Unit,
    text: String,
    enabled: Boolean = true
) {
    val borderColor = if (enabled) Color.DarkGray else Color.LightGray
    val fontColor = if (enabled) Color.Black else Color.LightGray

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clickable(onClick = onClick, enabled = enabled)
            .border(2.dp, color = borderColor, shape = RoundedCornerShape(12.dp))
            .background(color = Color.White, shape = RoundedCornerShape(12.dp)) // Use the dynamic background color here
            .size(48.dp, 52.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = text,
                color = fontColor,
                fontSize = 20.sp
            )
        }
    }
}

@Preview
@Composable
fun ButtonAlphabetPreview() {
    ButtonAlphabet(
        onClick = { /* Handle click here */ },
        text = "A",
    )
}