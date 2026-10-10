/*
 * ĐỀ THI GIỮA KỲ: LẬP TRÌNH DI ĐỘNG
 * MSSV: 102240379
 */

package com.midterm.sv102240379.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.midterm.sv102240379.data.ContactStorage
import com.midterm.sv102240379.model.Contact
import com.midterm.sv102240379.model.ContactGroup
import com.midterm.sv102240379.model.SortOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.text.Collator
import java.util.Locale

/**
 * ViewModel quản lý logic nghiệp vụ và trạng thái (State) của ứng dụng Danh bạ
 */
class ContactViewModel(application: Application) : AndroidViewModel(application) {

    private val storage = ContactStorage(application.applicationContext)

    // Danh sách gốc tất cả các liên hệ
    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts: StateFlow<List<Contact>> = _contacts.asStateFlow()

    // Từ khóa tìm kiếm
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Nhóm được chọn để lọc (Tất cả, Yêu thích, Gia đình, Bạn bè, Công việc, Khác)
    private val _selectedGroup = MutableStateFlow(ContactGroup.ALL)
    val selectedGroup: StateFlow<ContactGroup> = _selectedGroup.asStateFlow()

    // Tùy chọn sắp xếp (A -> Z hoặc Z -> A)
    private val _sortOption = MutableStateFlow(SortOption.NAME_ASC)
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()

    // Liên hệ đang được chọn xem ở Màn hình chi tiết (null: đang ở danh sách)
    private val _selectedContact = MutableStateFlow<Contact?>(null)
    val selectedContact: StateFlow<Contact?> = _selectedContact.asStateFlow()

    // Lưu lại liên hệ vừa xóa để phục vụ tính năng "Hoàn tác (Undo)"
    private val _lastDeletedContact = MutableStateFlow<Contact?>(null)
    val lastDeletedContact: StateFlow<Contact?> = _lastDeletedContact.asStateFlow()

    // State điều khiển hiển thị Dialog Thêm/Sửa
    private val _showContactFormDialog = MutableStateFlow(false)
    val showContactFormDialog: StateFlow<Boolean> = _showContactFormDialog.asStateFlow()

    // Liên hệ đang chỉnh sửa (null nếu là thêm mới)
    private val _editingContact = MutableStateFlow<Contact?>(null)
    val editingContact: StateFlow<Contact?> = _editingContact.asStateFlow()

    // Liên hệ đang chờ xác nhận xóa trong Dialog
    private val _contactPendingDelete = MutableStateFlow<Contact?>(null)
    val contactPendingDelete: StateFlow<Contact?> = _contactPendingDelete.asStateFlow()

    // Collator hỗ trợ so sánh sắp xếp tên tiếng Việt chính xác
    private val vietnameseCollator = Collator.getInstance(Locale.forLanguageTag("vi-VN")).apply {
        strength = Collator.SECONDARY
    }

    init {
        // Tải danh bạ từ Local Storage khi ViewModel khởi tạo
        _contacts.value = storage.loadContacts()
    }

    /**
     * Danh sách liên hệ sau khi đã Lọc theo từ khóa, Lọc theo nhóm và Sắp xếp A-Z / Z-A
     */
    val filteredContacts: StateFlow<List<Contact>> = combine(
        _contacts,
        _searchQuery,
        _selectedGroup,
        _sortOption
    ) { contactList, query, group, sort ->
        var result = contactList

        // 1. Lọc theo nhóm
        result = when (group) {
            ContactGroup.ALL -> result
            ContactGroup.FAVORITES -> result.filter { it.isFavorite }
            else -> result.filter { it.group.equals(group.displayName, ignoreCase = true) }
        }

        // 2. Lọc theo từ khóa tìm kiếm (hỗ trợ tìm cả Tên và Số điện thoại)
        if (query.isNotBlank()) {
            val cleanQuery = query.trim().lowercase()
            result = result.filter {
                it.name.lowercase().contains(cleanQuery) || it.phone.contains(cleanQuery)
            }
        }

        // 3. Sắp xếp theo A-Z hoặc Z-A
        result.sortedWith { c1, c2 ->
            val comp = vietnameseCollator.compare(c1.name.trim(), c2.name.trim())
            if (sort == SortOption.NAME_ASC) comp else -comp
        }
    }.stateIn(
        scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Default),
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // --- CÁC HÀM XỬ LÝ SỰ KIỆN TÌM KIẾM, LỌC, SẮP XẾP ---

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onGroupSelect(group: ContactGroup) {
        _selectedGroup.value = group
    }

    fun onSortOptionChange(option: SortOption) {
        _sortOption.value = option
    }

    fun onSelectContact(contact: Contact?) {
        _selectedContact.value = contact
    }

    // --- CÁC HÀM ĐIỀU KHIỂN HỘP THOẠI (DIALOGS) ---

    fun openAddContactDialog() {
        _editingContact.value = null
        _showContactFormDialog.value = true
    }

    fun openEditContactDialog(contact: Contact) {
        _editingContact.value = contact
        _showContactFormDialog.value = true
    }

    fun closeContactFormDialog() {
        _showContactFormDialog.value = false
        _editingContact.value = null
    }

    fun requestDeleteContact(contact: Contact) {
        _contactPendingDelete.value = contact
    }

    fun dismissDeleteDialog() {
        _contactPendingDelete.value = null
    }

    // --- CÁC THAO TÁC NGHIỆP VỤ (CRUD & LƯU LOCAL) ---

    /**
     * Bật/tắt trạng thái yêu thích (⭐ Favorite)
     */
    fun toggleFavorite(contact: Contact) {
        val updatedList = _contacts.value.map {
            if (it.id == contact.id) it.copy(isFavorite = !it.isFavorite) else it
        }
        updateContactsAndPersist(updatedList)

        // Nếu đang ở màn hình chi tiết của người này thì cập nhật luôn
        if (_selectedContact.value?.id == contact.id) {
            _selectedContact.value = _selectedContact.value?.copy(isFavorite = !contact.isFavorite)
        }
    }

    /**
     * Kiểm tra số điện thoại đã tồn tại ở liên hệ khác hay chưa
     */
    fun isPhoneDuplicated(phone: String, excludeContactId: String? = null): Boolean {
        val normalized = phone.trim().replace(" ", "").replace("-", "")
        return _contacts.value.any {
            val contactPhone = it.phone.trim().replace(" ", "").replace("-", "")
            contactPhone == normalized && it.id != excludeContactId
        }
    }

    /**
     * Thêm liên hệ mới và lưu vào Local Storage
     */
    fun addContact(name: String, phone: String, email: String, group: String) {
        val newContact = Contact(
            name = name.trim(),
            phone = phone.trim(),
            email = email.trim(),
            group = group,
            isFavorite = false
        )
        val updatedList = _contacts.value + newContact
        updateContactsAndPersist(updatedList)
    }

    /**
     * Cập nhật thông tin liên hệ và lưu vào Local Storage
     */
    fun updateContact(id: String, name: String, phone: String, email: String, group: String) {
        val updatedList = _contacts.value.map {
            if (it.id == id) {
                it.copy(
                    name = name.trim(),
                    phone = phone.trim(),
                    email = email.trim(),
                    group = group
                )
            } else it
        }
        updateContactsAndPersist(updatedList)

        // Cập nhật lại màn hình chi tiết nếu đang mở người này
        if (_selectedContact.value?.id == id) {
            _selectedContact.value = updatedList.find { it.id == id }
        }
    }

    /**
     * Xóa liên hệ khỏi danh bạ, lưu lại bản sao để có thể Hoàn tác (Undo)
     */
    fun deleteContact(contact: Contact) {
        _lastDeletedContact.value = contact
        val updatedList = _contacts.value.filter { it.id != contact.id }
        updateContactsAndPersist(updatedList)

        // Đóng dialog xác nhận
        _contactPendingDelete.value = null

        // Nếu đang mở màn hình chi tiết của người này thì thoát ra danh sách
        if (_selectedContact.value?.id == contact.id) {
            _selectedContact.value = null
        }
    }

    /**
     * Hoàn tác (Khôi phục liên hệ vừa bị xóa)
     */
    fun undoDelete() {
        val contactToRestore = _lastDeletedContact.value ?: return
        val updatedList = _contacts.value + contactToRestore
        updateContactsAndPersist(updatedList)
        _lastDeletedContact.value = null
    }

    /**
     * Ghi đè danh sách mới vào State và lưu cục bộ vĩnh viễn
     */
    private fun updateContactsAndPersist(newList: List<Contact>) {
        _contacts.value = newList
        storage.saveContacts(newList)
    }
}
