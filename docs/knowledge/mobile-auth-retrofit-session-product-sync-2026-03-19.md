# Mobile Auth + Retrofit + Session + Product Sync

## 1. Bối cảnh

Sau khi đã có UI skeleton, bước tiếp theo của app mobile là nối những phần quan trọng nhất với backend thật:

- đăng nhập
- lưu phiên đăng nhập
- lấy danh sách sản phẩm
- lấy chi tiết sản phẩm

Trong project này, phần đó đã được nối theo hướng đơn giản, dễ đọc, phù hợp với MVP Android Java.

## 2. Các khái niệm chính

### Retrofit là gì?

`Retrofit` là thư viện giúp Android gọi HTTP API dễ hơn.

Thay vì tự tạo request thủ công, ta khai báo interface như:

- `AuthApiService`
- `ProductApiService`

Sau đó Retrofit tự biến các hàm trong interface thành lệnh gọi API thật.

### Repository là gì?

`Repository` là lớp đứng giữa UI và network.

UI không nên gọi Retrofit trực tiếp, vì như vậy `Activity` hoặc `Fragment` sẽ bị ôm quá nhiều việc.

Trong project này:

- `AuthRepository` xử lý đăng nhập
- `ProductRemoteRepository` xử lý lấy danh sách và chi tiết sản phẩm

### SessionManager là gì?

`SessionManager` là nơi lưu trạng thái đăng nhập sau khi login thành công.

Ở app này, nó lưu:

- `accessToken`
- `refreshToken`
- email
- tên người dùng
- role
- địa chỉ mặc định

Phần lưu trữ ưu tiên dùng `EncryptedSharedPreferences` để token không bị để ở dạng plain text.

## 3. Vì sao cách tách này quan trọng?

Nếu `LoginActivity` tự gọi Retrofit, tự giữ token, tự đọc response và tự quyết định cache thì code sẽ nhanh rối.

Cách chia hiện tại giúp:

- UI chỉ lo hiển thị và nhận thao tác người dùng
- repository chỉ lo lấy dữ liệu
- `SessionManager` chỉ lo lưu phiên
- phần network có thể tái sử dụng cho nhiều màn hình sau này

## 4. Luồng runtime trong app

## Luồng 1: Đăng nhập thật với backend

1. Người dùng nhập email và password ở `LoginActivity`.
2. `LoginActivity` gọi `AuthRepository.login(...)`.
3. `AuthRepository` dùng `AuthApiService` để gọi `POST /api/auth/login`.
4. `RetrofitClient` tạo `OkHttpClient`, gắn `AuthHeaderInterceptor`, rồi gửi request đến `BuildConfig.API_BASE_URL`.
5. Backend trả về `ApiEnvelope<RemoteAuthResponse>`.
6. Nếu thành công, `AuthRepository` chuyển `RemoteAuthResponse` cho `SessionManager`.
7. `SessionManager` lưu token và thông tin user vào `EncryptedSharedPreferences`.
8. `LoginActivity` mở `MainActivity`.

## Luồng 2: Vào app khi đã có phiên đăng nhập

1. Người dùng mở app lại.
2. `LoginActivity` kiểm tra `SessionManager.hasActiveSession()`.
3. Nếu đã có `accessToken`, app bỏ qua màn login và đi thẳng vào `MainActivity`.

Điều này giúp trải nghiệm gần giống app thật hơn, thay vì mỗi lần mở app đều phải đăng nhập lại.

## Luồng 3: Tải danh sách sản phẩm

1. `MainActivity` mở `HomeFragment`.
2. `HomeFragment` nạp dữ liệu giả từ `FakeMarketplaceRepository` trước để màn hình có nội dung ngay.
3. Sau đó `HomeFragment` gọi `ProductRemoteRepository.fetchProducts(...)`.
4. `ProductRemoteRepository` dùng `ProductApiService` gọi `GET /api/products?page=0&size=12`.
5. Backend trả về `ApiEnvelope<SpringPageResponse<RemoteProductResponse>>`.
6. Repository map dữ liệu backend sang model `Product` của app mobile.
7. `ProductAdapter.replaceProducts(...)` cập nhật lại `RecyclerView`.

Điểm hay của cách này là:

- app không bị màn hình trắng khi backend chậm
- vẫn có dữ liệu fallback để demo
- khi backend trả về được thì UI đổi sang dữ liệu thật

## Luồng 4: Mở chi tiết sản phẩm

1. Người dùng bấm vào một card ở `HomeFragment` hoặc `WishlistFragment`.
2. App mở `ProductDetailActivity`.
3. `Product` được truyền qua `Intent` bằng `Parcelable`.
4. `ProductDetailActivity` bind dữ liệu preview trước.
5. Nếu sản phẩm có `remoteSource = true`, màn hình sẽ gọi `ProductRemoteRepository.fetchProductDetail(...)`.
6. Repository gọi `GET /api/products/{id}`.
7. Khi backend trả về chi tiết mới hơn, `ProductDetailActivity` bind lại UI.

Cách làm này giúp detail screen mở nhanh hơn vì không phải chờ request đầu tiên mới vẽ được màn hình.

## 5. Những file chính để đọc code

- `app/src/main/java/com/example/mobile_obs_asm/LoginActivity.java`
- `app/src/main/java/com/example/mobile_obs_asm/MainActivity.java`
- `app/src/main/java/com/example/mobile_obs_asm/ProductDetailActivity.java`
- `app/src/main/java/com/example/mobile_obs_asm/ui/home/HomeFragment.java`
- `app/src/main/java/com/example/mobile_obs_asm/ui/home/ProductAdapter.java`
- `app/src/main/java/com/example/mobile_obs_asm/ui/profile/ProfileFragment.java`
- `app/src/main/java/com/example/mobile_obs_asm/data/AuthRepository.java`
- `app/src/main/java/com/example/mobile_obs_asm/data/ProductRemoteRepository.java`
- `app/src/main/java/com/example/mobile_obs_asm/data/SessionManager.java`
- `app/src/main/java/com/example/mobile_obs_asm/network/RetrofitClient.java`
- `app/src/main/java/com/example/mobile_obs_asm/network/AuthHeaderInterceptor.java`
- `app/src/main/java/com/example/mobile_obs_asm/network/auth/AuthApiService.java`
- `app/src/main/java/com/example/mobile_obs_asm/network/product/ProductApiService.java`
- `app/src/main/java/com/example/mobile_obs_asm/model/Product.java`

## 6. Sơ đồ luồng đơn giản

```mermaid
flowchart TD
    A[User nhập email/password] --> B[LoginActivity]
    B --> C[AuthRepository]
    C --> D[AuthApiService]
    D --> E[Backend /api/auth/login]
    E --> F[RemoteAuthResponse]
    F --> G[SessionManager]
    G --> H[MainActivity]
    H --> I[HomeFragment]
    I --> J[ProductRemoteRepository]
    J --> K[ProductApiService]
    K --> L[Backend /api/products]
    L --> M[ProductAdapter]
    M --> N[ProductDetailActivity]
    N --> O[Backend /api/products/{id}]
```

## 7. Điều đã được áp dụng trong project này

- `BuildConfig.API_BASE_URL` được đưa vào `build.gradle.kts` để dễ đổi server.
- App đã thêm `INTERNET` permission.
- App đang bật `usesCleartextTraffic="true"` để dễ test local backend HTTP trong giai đoạn đầu.
- `SessionManager` ưu tiên `EncryptedSharedPreferences`.
- `Product` đã đổi sang `Parcelable` để truyền giữa các màn hình sạch hơn và tránh warning deprecated từ đường `Serializable`.

## 8. Lỗi thường gặp

### Lưu token trực tiếp bằng SharedPreferences thường

Điều này dễ làm lúc đầu nhưng không an toàn bằng encrypted storage.

### Để Activity gọi API trực tiếp

Nếu viết nhanh trong `Activity`, code sẽ chạy được nhưng rất khó mở rộng khi có thêm loading, retry, logout, refresh token.

### Chỉ dùng dữ liệu giả hoặc chỉ dùng dữ liệu thật

Nếu chỉ dùng dữ liệu giả, app không tiến gần backend.
Nếu chỉ dùng dữ liệu thật quá sớm, UI thường khó test khi server lỗi.

Cách kết hợp preview data + remote sync trong project này là một điểm cân bằng tốt cho giai đoạn MVP.

### Dùng Serializable cho model truyền qua Intent

`Serializable` vẫn dùng được trong vài trường hợp, nhưng với Android hiện đại thì `Parcelable` phù hợp hơn cho model UI được truyền thường xuyên giữa các màn.

## 9. Bước tiếp theo hợp lý

Sau mốc này, các bước tiếp theo nên là:

1. nối wishlist với backend thật
2. nối orders với backend thật
3. thêm trạng thái loading và empty state rõ ràng hơn cho list/detail
4. xử lý refresh token khi access token hết hạn
5. thêm test cho repository và mapping dữ liệu backend
