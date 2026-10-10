/*
 * ĐỀ THI GIỮA KỲ: LẬP TRÌNH DI ĐỘNG
 * MSSV: 102240379
 */

package com.midterm.sv102240379.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.midterm.sv102240379.model.Contact
import com.midterm.sv102240379.model.ContactGroup

/**
 * Lớp quản lý lưu trữ danh bạ cục bộ (Local Storage) KHÔNG SỬ DỤNG SQL
 * Sử dụng SharedPreferences kết hợp định dạng JSON (Gson) để lưu trữ vĩnh viễn trên máy.
 */
class ContactStorage(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val PREF_NAME = "contacts_local_pref"
        private const val KEY_CONTACTS = "key_contact_list"
    }

    /**
     * Đọc danh sách liên hệ từ bộ nhớ máy.
     * Nếu là lần đầu mở ứng dụng (chưa có dữ liệu), nạp sẵn danh sách mẫu.
     */
    fun loadContacts(): List<Contact> {
        val json = prefs.getString(KEY_CONTACTS, null)
        if (json.isNullOrEmpty()) {
            val initialList = getSampleContacts()
            saveContacts(initialList)
            return initialList
        }

        return try {
            val type = object : TypeToken<List<Contact>>() {}.type
            gson.fromJson<List<Contact>>(json, type) ?: getSampleContacts()
        } catch (e: Exception) {
            getSampleContacts()
        }
    }

    /**
     * Lưu danh sách liên hệ vào bộ nhớ máy dưới dạng chuỗi JSON
     */
    fun saveContacts(contacts: List<Contact>) {
        val json = gson.toJson(contacts)
        prefs.edit().putString(KEY_CONTACTS, json).apply()
    }

    /**
     * Danh bạ mẫu phong phú để ứng dụng có sẵn dữ liệu trực quan khi khởi chạy và quay demo
     */
    private fun getSampleContacts(): List<Contact> {
        return listOf(
            Contact(
                name = "Nguyễn Văn An",
                phone = "0905123456",
                email = "an.nguyen@gmail.com",
                group = ContactGroup.FAMILY.displayName,
                isFavorite = true
            ),
            Contact(
                name = "Trần Thị Bình",
                phone = "0912345678",
                email = "binh.tran@outlook.com",
                group = ContactGroup.WORK.displayName,
                isFavorite = false
            ),
            Contact(
                name = "Lê Hoàng Cường",
                phone = "0987654321",
                email = "cuong.le@company.com",
                group = ContactGroup.WORK.displayName,
                isFavorite = true
            ),
            Contact(
                name = "Phạm Minh Đức",
                phone = "0934567890",
                email = "duc.pham@yahoo.com",
                group = ContactGroup.FRIENDS.displayName,
                isFavorite = false
            ),
            Contact(
                name = "Đặng Thùy Trang",
                phone = "0978901234",
                email = "trang.dang@gmail.com",
                group = ContactGroup.FRIENDS.displayName,
                isFavorite = true
            ),
            Contact(
                name = "Vũ Quang Huy",
                phone = "0945678901",
                email = "huy.vu@gmail.com",
                group = ContactGroup.OTHER.displayName,
                isFavorite = false
            )
        )
    }
}
