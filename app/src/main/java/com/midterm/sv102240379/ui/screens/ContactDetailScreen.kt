/*
 * ĐỀ THI GIỮA KỲ: LẬP TRÌNH DI ĐỘNG
 * MSSV: 102240379
 */

package com.midterm.sv102240379.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.automirrored.outlined.Message
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.midterm.sv102240379.model.Contact
import com.midterm.sv102240379.ui.components.ContactAvatar

/**
 * MÀN HÌNH CHI TIẾT LIÊN HỆ (Contact Detail Screen)
 * Thiết kế chuẩn như Google Contacts / iOS Contacts:
 * - Avatar lớn ở trung tâm, Tên và Nhóm to rõ ràng.
 * - Cụm 4 nút tròn hành động: Gọi điện thật, Nhắn tin thật, Sao chép SĐT, Chia sẻ.
 * - Thẻ chi tiết có tính năng chạm để sao chép vào Clipboard kèm thông báo Toast.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactDetailScreen(
    contact: Contact,
    onBack: () -> Unit,
    onEditContact: () -> Unit,
    onDeleteContact: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Xử lý nút Back của Android để quay lại màn hình danh sách
    BackHandler {
        onBack()
    }

    // Hàm sao chép số điện thoại vào Clipboard và hiện Toast
    fun copyToClipboard(text: String, label: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Đã sao chép $label", Toast.LENGTH_SHORT).show()
    }

    // Hàm gọi điện thật thông qua Implicit Intent ACTION_DIAL (Không cần xin quyền nguy hiểm)
    fun makePhoneCall(phone: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phone")
        }
        context.startActivity(intent)
    }

    // Hàm nhắn tin thật thông qua ACTION_SENDTO
    fun sendSms(phone: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("smsto:$phone")
        }
        context.startActivity(intent)
    }

    // Hàm chia sẻ liên hệ thông qua ACTION_SEND
    fun shareContact() {
        val shareText = "Thông tin liên hệ:\nHọ tên: ${contact.name}\nSố điện thoại: ${contact.phone}${if (contact.email.isNotBlank()) "\nEmail: ${contact.email}" else ""}"
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Chia sẻ danh bạ")
        context.startActivity(shareIntent)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết liên hệ") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại"
                        )
                    }
                },
                actions = {
                    // Nút Yêu thích (⭐)
                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            imageVector = if (contact.isFavorite) Icons.Default.Star else Icons.Outlined.StarOutline,
                            contentDescription = "Yêu thích",
                            tint = if (contact.isFavorite) Color(0xFFFFA000) else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    // Nút Sửa (✏️)
                    IconButton(onClick = onEditContact) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Chỉnh sửa")
                    }
                    // Nút Xóa (🗑️)
                    IconButton(onClick = onDeleteContact) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Xóa",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // 1. Avatar tròn lớn với chữ cái đầu cách điệu
            ContactAvatar(
                name = contact.name,
                initialLetter = contact.initialLetter,
                size = 104.dp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Họ và tên hiển thị nổi bật
            Text(
                text = contact.name,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            // 3. Badge hiển thị Nhóm liên hệ
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = contact.group,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // 4. Cụm 4 nút tròn hành động (Gọi điện, Nhắn tin, Sao chép, Chia sẻ)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                DetailActionButton(
                    icon = Icons.Default.Call,
                    label = "Gọi điện",
                    color = Color(0xFF2E7D32),
                    onClick = { makePhoneCall(contact.phone) }
                )
                DetailActionButton(
                    icon = Icons.AutoMirrored.Outlined.Message,
                    label = "Nhắn tin",
                    color = Color(0xFF1976D2),
                    onClick = { sendSms(contact.phone) }
                )
                DetailActionButton(
                    icon = Icons.Default.ContentCopy,
                    label = "Sao chép",
                    color = Color(0xFFF57C00),
                    onClick = { copyToClipboard(contact.phone, "số điện thoại") }
                )
                DetailActionButton(
                    icon = Icons.Default.Share,
                    label = "Chia sẻ",
                    color = Color(0xFF7B1FA2),
                    onClick = { shareContact() }
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // 5. Thẻ danh thiếp thông tin liên hệ chi tiết
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    // Dòng Số điện thoại (Chạm để sao chép)
                    ContactDetailInfoRow(
                        icon = Icons.Default.Phone,
                        title = "Số điện thoại",
                        value = contact.phone,
                        subtitle = "Chạm để sao chép",
                        trailingIcon = Icons.Default.ContentCopy,
                        onClick = { copyToClipboard(contact.phone, "số điện thoại") }
                    )

                    // Dòng Email
                    ContactDetailInfoRow(
                        icon = Icons.Default.Email,
                        title = "Email",
                        value = if (contact.email.isNotBlank()) contact.email else "Chưa có email",
                        subtitle = if (contact.email.isNotBlank()) "Chạm để sao chép" else null,
                        trailingIcon = if (contact.email.isNotBlank()) Icons.Default.ContentCopy else null,
                        onClick = {
                            if (contact.email.isNotBlank()) {
                                copyToClipboard(contact.email, "email")
                            }
                        }
                    )

                    // Dòng Nhóm
                    ContactDetailInfoRow(
                        icon = Icons.Outlined.Group,
                        title = "Nhóm danh bạ",
                        value = contact.group,
                        subtitle = null,
                        trailingIcon = null,
                        onClick = null
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

/**
 * Nút hành động tròn phong cách danh bạ (Call, Message, Copy, Share)
 */
@Composable
private fun DetailActionButton(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * Hàng hiển thị một trường thông tin trong Card chi tiết
 */
@Composable
private fun ContactDetailInfoRow(
    icon: ImageVector,
    title: String,
    value: String,
    subtitle: String?,
    trailingIcon: ImageVector?,
    onClick: (() -> Unit)?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
        if (trailingIcon != null) {
            Icon(
                imageVector = trailingIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
