/*
 * ĐỀ THI GIỮA KỲ: LẬP TRÌNH DI ĐỘNG
 * MSSV: 102240379
 */

package com.midterm.sv102240379.model

/**
 * Định nghĩa danh sách các nhóm liên hệ trong danh bạ
 */
enum class ContactGroup(val displayName: String) {
    ALL("Tất cả"),
    FAVORITES("Yêu thích ⭐"),
    FAMILY("Gia đình"),
    FRIENDS("Bạn bè"),
    WORK("Công việc"),
    OTHER("Khác");

    companion object {
        // Danh sách nhóm để người dùng gán khi tạo/sửa liên hệ (loại trừ ALL và FAVORITES)
        val selectableGroups = listOf(FAMILY, FRIENDS, WORK, OTHER)
    }
}
