# Phân tích toàn bộ dự án Mobile_OBS_ASM (Hệ thống xe đạp cũ)

Tài liệu này cung cấp cái nhìn tổng quan về kiến trúc, thành phần và luồng hoạt động của ứng dụng di động trong hệ thống Old Bicycle System (OBS).

## 1. Tổng quan dự án
- **Tên dự án:** Mobile_OBS_ASM
- **Nền tảng:** Android Native (Java)
- **Giao diện:** XML Views với Material Design
- **Mục tiêu:** Cho phép người dùng mua bán xe đạp cũ, quản lý đơn hàng và danh sách sản phẩm.

## 2. Kiến trúc ứng dụng
Dự án được tổ chức theo mô hình phân lớp đơn giản, dễ tiếp cận cho sinh viên:
- **UI Layer (`ui` & `activities`):** Các Activity và Fragment đảm nhận hiển thị và tương tác người dùng.
- **Data Layer (`data`):** Các Repository chịu trách nhiệm xử lý dữ liệu (lấy từ API hoặc bộ nhớ tạm).
- **Network Layer (`network`):** Sử dụng Retrofit để giao tiếp với backend thông qua RESTful API.
- **Model Layer (`model`):** Các POJO định nghĩa cấu trúc dữ liệu.

## 3. Các thành phần chính

### 3.1. Giao diện (Activities & Fragments)
- **MainActivity:** Chứa Bottom Navigation điều hướng giữa Trang chủ, Yêu thích/Tin đăng, Đơn hàng và Cá nhân.
- **LoginActivity / RegisterActivity:** Xử lý đăng nhập và đăng ký người dùng.
- **ProductDetailActivity:** Hiển thị chi tiết xe đạp và cho phép đặt mua.
- **CreateOrderActivity:** Quy trình tạo đơn hàng từ một sản phẩm.
- **Fragments:**
    - `HomeFragment`: Danh sách xe đạp công khai.
    - `WishlistFragment`: Xe đạp yêu thích của người mua.
    - `SellerListingsFragment`: Danh sách tin đăng của người bán.
    - `OrdersFragment`: Lịch sử đơn hàng.

### 3.2. Quản lý dữ liệu và Phiên làm việc
- **SessionManager:** Lưu trữ JWT Token và thông tin vai trò người dùng (Buyer/Seller) bằng `SharedPreferences`.
- **SessionAwareActivity:** Lớp cơ sở đảm bảo người dùng phải đăng nhập mới truy cập được các màn hình nhạy cảm.

### 3.3. Kết nối mạng (Networking)
- **RetrofitClient:** Cấu hình Retrofit với `AuthHeaderInterceptor` để tự động đính kèm Token vào Header.
- **ApiEnvelope:** Cấu trúc chuẩn để bọc các phản hồi từ server (Spring Boot).

## 4. Luồng dữ liệu chính
Quy trình hoạt động thông thường:
1. **User Action:** Người dùng nhấn vào một sản phẩm trên `HomeFragment`.
2. **Activity/Fragment:** `ProductDetailActivity` được khởi tạo.
3. **Repository:** Gọi `ProductRemoteRepository.fetchProductDetail(id)`.
4. **API Service:** `ProductApiService` thực hiện gọi API tới Backend qua Retrofit.
5. **Backend Response:** Trả về JSON, được `mapProduct` chuyển đổi sang Model `Product`.
6. **UI Update:** Dữ liệu được hiển thị lên các View (TextView, ImageView qua Glide).

## 5. Các thư viện quan trọng
- **Retrofit 2:** Xử lý HTTP requests.
- **Gson:** Chuyển đổi JSON sang Java Object.
- **Glide:** Tải và hiển thị hình ảnh từ URL.
- **OkHttp Logging Interceptor:** Hỗ trợ debug thông tin mạng.
- **Material Components:** Các thành phần giao diện hiện đại.

## 6. Ghi chú cho lập trình viên mới
- **Xử lý màu sắc:** Dự án có cơ chế tự động chọn màu nền cho thẻ sản phẩm dựa trên ID (`pickHeroColor`).
- **Phân quyền:** Ứng dụng tự động thay đổi giao diện (Labels, Fragments) dựa trên vai trò của người dùng (Người mua vs Người bán) được lưu trong `SessionManager`.
- **Dữ liệu tham chiếu:** Sử dụng `ReferenceDataRemoteRepository` để tải các danh mục như tỉnh thành, kích thước khung xe từ máy chủ.

---
*Tài liệu được cập nhật lần cuối: 2026-03-20*
