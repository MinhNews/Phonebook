/*
 * TRƯỜNG ĐẠI HỌC BÁCH KHOA - ĐẠI HỌC ĐÀ NẴNG
 * KHOA ĐIỆN TỬ VÀ TRÍ TUỆ NHÂN TẠO
 * ĐỀ KIỂM TRA GIỮA KỲ: LẬP TRÌNH DI ĐỘNG
 * 
 * MSSV: 102240379
 * Package: com.midterm.sv102240379
 * Đề tài: Ứng dụng "Danh bạ" điện thoại
 */

package com.midterm.sv102240379

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.midterm.sv102240379.ui.components.ContactFormDialog
import com.midterm.sv102240379.ui.components.DeleteConfirmDialog
import com.midterm.sv102240379.ui.screens.ContactDetailScreen
import com.midterm.sv102240379.ui.screens.ContactListScreen
import com.midterm.sv102240379.ui.theme.DanhBaDienThoaiTheme
import com.midterm.sv102240379.viewmodel.ContactViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DanhBaDienThoaiTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PhonebookApp()
                }
            }
        }
    }
}

/**
 * Thành phần gốc của ứng dụng (Root App Composable)
 * Điều phối điều hướng màn hình giữa Danh sách và Chi tiết, quản lý Dialogs và Snackbar Hoàn tác.
 */
@Composable
fun PhonebookApp(
    viewModel: ContactViewModel = viewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val selectedContact by viewModel.selectedContact.collectAsState()
    val showFormDialog by viewModel.showContactFormDialog.collectAsState()
    val editingContact by viewModel.editingContact.collectAsState()
    val pendingDeleteContact by viewModel.contactPendingDelete.collectAsState()

    // Chuyển đổi màn hình mượt mà với Crossfade animation
    Crossfade(
        targetState = selectedContact,
        label = "ScreenTransition"
    ) { contact ->
        if (contact != null) {
            // MÀN HÌNH CHI TIẾT LIÊN HỆ
            ContactDetailScreen(
                contact = contact,
                onBack = { viewModel.onSelectContact(null) },
                onEditContact = { viewModel.openEditContactDialog(contact) },
                onDeleteContact = { viewModel.requestDeleteContact(contact) },
                onToggleFavorite = { viewModel.toggleFavorite(contact) }
            )
        } else {
            // MÀN HÌNH DANH SÁCH CHÍNH
            ContactListScreen(
                viewModel = viewModel,
                snackbarHostState = snackbarHostState
            )
        }
    }

    // --- CÁC HỘP THOẠI TOÀN CỤC (GLOBAL DIALOGS) ---

    // 1. Dialog Thêm mới hoặc Sửa thông tin liên hệ
    if (showFormDialog) {
        ContactFormDialog(
            contactToEdit = editingContact,
            isPhoneDuplicated = { phone, excludeId ->
                viewModel.isPhoneDuplicated(phone, excludeId)
            },
            onDismiss = { viewModel.closeContactFormDialog() },
            onSave = { name, phone, email, group ->
                if (editingContact == null) {
                    viewModel.addContact(name, phone, email, group)
                } else {
                    viewModel.updateContact(editingContact!!.id, name, phone, email, group)
                }
                viewModel.closeContactFormDialog()
            }
        )
    }

    // 2. Dialog Xác nhận xóa liên hệ (Kèm tính năng Hoàn tác - Undo)
    pendingDeleteContact?.let { contactToDelete ->
        DeleteConfirmDialog(
            contact = contactToDelete,
            onDismiss = { viewModel.dismissDeleteDialog() },
            onConfirm = {
                val deletedName = contactToDelete.name
                viewModel.deleteContact(contactToDelete)
                // Kích hoạt Snackbar Hoàn tác ở góc dưới màn hình
                coroutineScope.launch {
                    val result = snackbarHostState.showSnackbar(
                        message = "Đã xóa $deletedName khỏi danh bạ",
                        actionLabel = "Hoàn tác",
                        duration = SnackbarDuration.Short
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.undoDelete()
                    }
                }
            }
        )
    }
}