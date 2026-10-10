# Ứng Dụng "Danh Bạ Điện Thoại" (Phonebook App)

Đề thi giữa kỳ môn **Lập trình di động**  
**Trường:** Đại học Bách Khoa - Đại học Đà Nẵng  
**Khoa:** Công nghệ thông tin
**MSSV:** 102240379
**Tên:** Nguyễn Đức Minh
**Lớp:** 24T_Nhat2
---

## 📱 Giới thiệu dự án
Ứng dụng danh bạ điện thoại hiện đại được xây dựng hoàn toàn bằng **Kotlin** và **Jetpack Compose (Material 3)**. Dữ liệu được lưu trữ cục bộ (**Local Storage**) vĩnh viễn trên thiết bị **không sử dụng SQL**, đảm bảo không mất dữ liệu khi tắt mở ứng dụng.

---

## ✨ Tính năng nổi bật

### 1. Giao diện người dùng (UI/UX)
- **Màn hình danh sách (`ContactListScreen`):** Thiết kế chuẩn Material 3, thanh tìm kiếm thời gian thực, bộ lọc nhóm, danh sách cuộn mượt mà và nút FAB (+) thêm mới.
- **Màn hình chi tiết (`ContactDetailScreen`):** Hồ sơ cá nhân với Avatar lớn, cụm nút tròn hành động (Gọi điện, Nhắn tin, Sao chép, Chia sẻ) và thông tin chi tiết.
- **Custom Layout:** 
  - `ContactItemCard` bo góc 14dp viền mỏng tinh tế.
  - `ContactAvatar` tự sinh dải màu Gradient Pastel theo tên và chữ cái đầu in hoa.
  - **Alphabetical Headers:** Phân nhóm danh bạ theo vần chữ cái (A, B, C...).
- **Chế độ Sáng / Tối (Dark & Light Theme):** Tự động thích ứng theo cài đặt hệ điều hành.
- **Đếm số lượng liên hệ:** Dòng footer hiển thị tổng số liên hệ ở đáy danh sách.
- **Empty State:** Hình minh họa và thông báo khi danh bạ rỗng hoặc không tìm thấy kết quả.

### 2. Nghiệp vụ & Chức năng (Business Logic)
- **Thêm & Chỉnh sửa liên hệ:** Form nhập liệu với kiểm tra hợp lệ chặt chẽ:
  - Tên không được để trống.
  - Số điện thoại chuẩn **10 chữ số** (bắt đầu bằng số 0).
  - Cảnh báo trùng lặp số điện thoại nếu số đã tồn tại trong danh bạ.
  - Kiểm tra định dạng Email hợp lệ.
- **Xóa & Hoàn tác (Undo Delete):** 
  - Hộp thoại cảnh báo xác nhận trước khi xóa.
  - **Thanh Snackbar Hoàn tác:** Bấm nút *"Hoàn tác"* ở đáy màn hình để khôi phục ngay liên hệ vừa xóa.
- **Tìm kiếm:** Lọc tức thì realtime theo cả Tên và Số điện thoại.
- **Bộ lọc & Sắp xếp:**
  - Lọc theo nhóm: *Tất cả*, *Yêu thích ⭐*, *Gia đình*, *Bạn bè*, *Công việc*, *Khác*.
  - Sắp xếp linh hoạt: **A → Z** hoặc **Z → A**.
- **Tính năng mở rộng:**
  - **Gọi điện thoại thật:** Tích hợp Android Intent `ACTION_DIAL`.
  - **Nhắn tin SMS thật:** Tích hợp Android Intent `ACTION_SENDTO`.
  - **Sao chép số điện thoại:** Chạm vào SĐT để copy vào Clipboard kèm thông báo Toast.
  - **Chia sẻ liên hệ:** Gửi thông tin liên hệ qua Intent `ACTION_SEND`.
  - **Đánh dấu Yêu thích ⭐:** Bật/tắt trạng thái yêu thích một chạm.

---

## 🛠 Công nghệ sử dụng
- **Ngôn ngữ:** Kotlin
- **Giao diện:** Jetpack Compose, Material 3
- **Kiến trúc:** MVVM (Model - View - ViewModel)
- **Quản lý trạng thái:** StateFlow, Coroutines
- **Lưu trữ Local:** `SharedPreferences` + `Gson` (Không dùng SQL)
- **Target SDK:** Android 15 (API 37) | **Min SDK:** Android 7.0 (API 24)
