# Debug Login Và System Bar Insets Trong App Android Java

## 1. Bối cảnh

Trong lúc chạy app mobile, có hai lỗi rất dễ gặp:

1. đăng nhập không vào được dù form nhìn có vẻ đúng
2. UI bị chui lên vùng status bar, tức là đè vào khu vực Wi‑Fi, pin, sóng

Hai lỗi này thường không nằm ở cùng một chỗ, nhưng lại hay xuất hiện cùng lúc khi app mới được dựng.

## 2. Vì sao login có thể fail?

Ở project này, nguyên nhân phổ biến nhất là:

- app đang dùng `http://10.0.2.2:8080/`
- địa chỉ này chỉ đúng cho **Android Emulator**

Nếu bạn chạy app trên **máy thật**, `10.0.2.2` sẽ không trỏ tới máy tính đang chạy backend của bạn.

Khi đó, app không gọi được backend nên login sẽ fail kiểu network.

## 3. Vì sao trước đây nhìn khó debug?

Trước khi sửa, mobile app có 2 điểm gây hiểu nhầm:

1. màn login có copy giống như có sẵn demo credentials dùng cho backend
2. `LoginActivity` chỉ hiện toast chung chung, không hiện message thật từ backend

Kết quả là:

- sai password
- tài khoản chưa verify email
- backend không chạy
- sai base URL

đều dễ bị nhìn giống nhau.

## 4. Điều đã được sửa trong code

### Phần login

- `AuthRepository` giờ parse message lỗi từ backend thay vì nuốt mất
- nếu request không tới được backend, app hiện message có kèm `API_BASE_URL`
- `LoginActivity` không còn tự prefill một account/password dễ gây hiểu nhầm
- text helper ở màn login đã đổi sang hướng:
  - dùng tài khoản backend thật nếu muốn sign in
  - dùng `Open demo directly` nếu chỉ muốn xem UI

### Phần cấu hình base URL

Trong `gradle.properties` đã thêm note:

- emulator dùng `10.0.2.2`
- máy thật nên đổi sang IP LAN của máy tính

Ví dụ:

`apiBaseUrl=http://192.168.1.10:8080/`

## 5. System bar inset là gì?

`System bar inset` là khoảng không gian dành cho:

- status bar ở trên
- navigation bar hoặc gesture area ở dưới

Trên Android mới, app thường chạy theo hướng `edge-to-edge`, nghĩa là nội dung có thể kéo sát mép màn hình.

Nếu bạn không cộng thêm inset đúng cách, UI sẽ bị:

- dính vào status bar
- bị nav bar che mất ở dưới

## 6. Điều đã được sửa cho UI

Project đã thêm:

- `app/src/main/java/com/example/mobile_obs_asm/util/SystemBarInsetsHelper.java`

Lớp này lấy `WindowInsets` và cộng thêm padding tương ứng vào root view của activity.

Nó đã được áp dụng cho:

- `LoginActivity`
- `MainActivity`
- `ProductDetailActivity`
- `CreateOrderActivity`
- `OrderDetailActivity`

Nhờ vậy các màn chính không còn chui lên vùng pin/sóng như trước.

## 7. Những file chính nên đọc

- `app/src/main/java/com/example/mobile_obs_asm/LoginActivity.java`
- `app/src/main/java/com/example/mobile_obs_asm/data/AuthRepository.java`
- `app/src/main/java/com/example/mobile_obs_asm/util/ApiErrorMessageExtractor.java`
- `app/src/main/java/com/example/mobile_obs_asm/util/SystemBarInsetsHelper.java`
- `app/src/main/res/layout/activity_login.xml`
- `gradle.properties`
- `app/build.gradle.kts`

## 8. Điều cần nhớ khi test login

### Nếu đang dùng Android Emulator

Giữ:

`http://10.0.2.2:8080/`

### Nếu đang dùng máy thật

Phải đổi sang IP LAN của máy tính chạy backend.

Ví dụ:

`http://192.168.1.10:8080/`

và điện thoại phải cùng mạng Wi‑Fi với máy tính.

### Nếu backend báo lỗi tài khoản

Bây giờ app sẽ hiện message thật rõ hơn, ví dụ kiểu:

- email chưa verify
- credentials sai
- request bị backend từ chối

## 9. Kết luận ngắn

Lỗi login không phải lúc nào cũng là sai password.

Trong app Android gọi backend local, nguyên nhân rất hay gặp là:

- sai địa chỉ backend giữa emulator và máy thật

Còn lỗi UI đè lên status bar là do:

- thiếu xử lý `WindowInsets`

Hai phần này tuy nhỏ nhưng nếu không sửa sớm, việc test toàn bộ mobile flow sẽ rất khó chịu.
