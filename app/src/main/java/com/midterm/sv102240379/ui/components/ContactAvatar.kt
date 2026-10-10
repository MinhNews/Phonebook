/*
 * ĐỀ THI GIỮA KỲ: LẬP TRÌNH DI ĐỘNG
 * MSSV: 102240379
 */

package com.midterm.sv102240379.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs

/**
 * Danh sách các cặp màu gradient hiện đại dùng cho Avatar
 */
private val avatarGradientPalettes = listOf(
    listOf(Color(0xFF4FACFE), Color(0xFF00F2FE)), // Blue - Cyan
    listOf(Color(0xFF43E97B), Color(0xFF38F9D7)), // Mint - Green
    listOf(Color(0xFFFA709A), Color(0xFFFEE140)), // Pink - Orange
    listOf(Color(0xFF667EEA), Color(0xFF764BA2)), // Indigo - Purple
    listOf(Color(0xFFFF0844), Color(0xFFFFB199)), // Rose - Peach
    listOf(Color(0xFFF857A6), Color(0xFFFF5858)), // Magenta - Coral
    listOf(Color(0xFF0BA360), Color(0xFF3CBA92))  // Deep Teal
)

/**
 * Avatar tròn tùy biến (Custom Layout):
 * Tự động tạo màu nền Gradient dựa trên tên và hiển thị ký tự viết hoa ở trung tâm.
 */
@Composable
fun ContactAvatar(
    name: String,
    initialLetter: String,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp
) {
    // Chọn dải màu gradient dựa trên mã băm của tên liên hệ
    val paletteIndex = abs(name.hashCode()) % avatarGradientPalettes.size
    val gradientColors = avatarGradientPalettes[paletteIndex]

    val fontSize = (size.value * 0.42f).sp

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(Brush.linearGradient(gradientColors)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initialLetter,
            color = Color.White,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold
        )
    }
}
