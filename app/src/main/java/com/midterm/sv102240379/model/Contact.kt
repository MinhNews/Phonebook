/*
 * ĐỀ THI GIỮA KỲ: LẬP TRÌNH DI ĐỘNG
 * MSSV: 102240379
 */

package com.midterm.sv102240379.model

import java.util.UUID

/**
 * Data class biểu diễn một liên hệ trong danh bạ
 */
data class Contact(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val phone: String,
    val email: String = "",
    val group: String = ContactGroup.FRIENDS.displayName,
    val isFavorite: Boolean = false
) {
    /**
     * Lấy ký tự đầu tiên để hiển thị trên Avatar và nhóm theo vần A-Z
     */
    val initialLetter: String
        get() {
            val trimmed = name.trim()
            if (trimmed.isEmpty()) return "#"
            val firstChar = trimmed.first().uppercaseChar()
            return if (firstChar in 'A'..'Z' || firstChar in 'À'..'ỹ' || firstChar == 'Đ') {
                firstChar.toString()
            } else {
                "#"
            }
        }
}
