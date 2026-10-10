/*
 * ĐỀ THI GIỮA KỲ: LẬP TRÌNH DI ĐỘNG
 * MSSV: 102240379
 */

package com.midterm.sv102240379.ui.components

import android.util.Patterns
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.midterm.sv102240379.model.Contact
import com.midterm.sv102240379.model.ContactGroup

/**
 * Hộp thoại Form Thêm mới hoặc Chỉnh sửa liên hệ (Kiểm tra dữ liệu đầu vào chặt chẽ)
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ContactFormDialog(
    contactToEdit: Contact?,
    isPhoneDuplicated: (String, String?) -> Boolean,
    onDismiss: () -> Unit,
    onSave: (name: String, phone: String, email: String, group: String) -> Unit
) {
    var name by remember { mutableStateOf(contactToEdit?.name ?: "") }
    var phone by remember { mutableStateOf(contactToEdit?.phone ?: "") }
    var email by remember { mutableStateOf(contactToEdit?.email ?: "") }
    var selectedGroup by remember {
        mutableStateOf(contactToEdit?.group ?: ContactGroup.FRIENDS.displayName)
    }

    var nameError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }

    val isEditing = contactToEdit != null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isEditing) "Chỉnh sửa liên hệ" else "Thêm danh bạ mới",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // 1. Ô nhập Họ và Tên (Bắt buộc)
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        if (nameError != null) nameError = null
                    },
                    label = { Text("Họ và tên *") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Outlined.Person, contentDescription = null)
                    },
                    isError = nameError != null,
                    supportingText = {
                        if (nameError != null) {
                            Text(text = nameError!!, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                // 2. Ô nhập Số điện thoại (Bắt buộc, kiểm tra định dạng và trùng lặp)
                OutlinedTextField(
                    value = phone,
                    onValueChange = {
                        phone = it
                        if (phoneError != null) phoneError = null
                    },
                    label = { Text("Số điện thoại *") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Outlined.Phone, contentDescription = null)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    isError = phoneError != null,
                    supportingText = {
                        if (phoneError != null) {
                            Text(text = phoneError!!, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                // 3. Ô nhập Email (Tùy chọn, kiểm tra hợp lệ nếu có nhập)
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        if (emailError != null) emailError = null
                    },
                    label = { Text("Email (Tùy chọn)") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Outlined.Email, contentDescription = null)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    isError = emailError != null,
                    supportingText = {
                        if (emailError != null) {
                            Text(text = emailError!!, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 4. Chọn nhóm liên hệ
                Text(
                    text = "Nhóm liên hệ",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ContactGroup.selectableGroups.forEach { group ->
                        FilterChip(
                            selected = selectedGroup == group.displayName,
                            onClick = { selectedGroup = group.displayName },
                            label = { Text(group.displayName) },
                            leadingIcon = if (selectedGroup == group.displayName) {
                                { Icon(Icons.Outlined.Group, contentDescription = null) }
                            } else null
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val trimmedName = name.trim()
                    val trimmedPhone = phone.trim()
                    val trimmedEmail = email.trim()

                    var isValid = true

                    // Kiểm tra Tên
                    if (trimmedName.isEmpty()) {
                        nameError = "Vui lòng nhập họ và tên"
                        isValid = false
                    }

                    // Kiểm tra Số điện thoại
                    if (trimmedPhone.isEmpty()) {
                        phoneError = "Vui lòng nhập số điện thoại"
                        isValid = false
                    } else if (!trimmedPhone.matches(Regex("^0[0-9]{9}$"))) {
                        phoneError = "Số điện thoại phải gồm đúng 10 chữ số (bắt đầu bằng số 0)"
                        isValid = false
                    } else if (isPhoneDuplicated(trimmedPhone, contactToEdit?.id)) {
                        phoneError = "Số điện thoại này đã tồn tại trong danh bạ!"
                        isValid = false
                    }

                    // Kiểm tra Email
                    if (trimmedEmail.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
                        emailError = "Địa chỉ email không đúng định dạng"
                        isValid = false
                    }

                    // Nếu dữ liệu hoàn toàn hợp lệ -> tiến hành lưu
                    if (isValid) {
                        onSave(trimmedName, trimmedPhone, trimmedEmail, selectedGroup)
                    }
                }
            ) {
                Text(if (isEditing) "Cập nhật" else "Lưu liên hệ")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}
