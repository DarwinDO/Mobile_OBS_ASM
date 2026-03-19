# Mobile Register, Auth và Usability Polish

## Bối cảnh

Trước đợt sửa này, app mobile có 3 vấn đề lớn:

1. Màn hình đăng nhập chưa có đăng ký tài khoản.
2. Nhiều câu chữ nghe giống ghi chú cho lập trình viên hơn là nội dung cho người dùng cuối.
3. Khi backend rỗng hoặc lỗi, màn hình `Home` dễ rơi vào cảm giác "không có gì để bấm".

Với một app mobile MVP, đây là vấn đề quan trọng vì người dùng thường đánh giá app qua ấn tượng đầu tiên:

- có vào được không
- có biết phải bấm gì không
- có hiểu màn hình đang làm gì không

## Khái niệm quan trọng

### 1. Auth entry flow là gì?

Đây là luồng đi vào ứng dụng bằng các màn như:

- đăng nhập
- đăng ký
- vào nhanh ở chế độ tham quan

Nếu luồng này thiếu một mắt xích, người dùng sẽ bị chặn ngay từ đầu.

### 2. Usability là gì?

`Usability` nghĩa là mức độ dễ dùng.

Một màn hình "đúng code" chưa chắc đã "dễ dùng". Ví dụ:

- card có click được nhưng không có nút rõ ràng
- dữ liệu rỗng nhưng không giải thích
- câu chữ nói về API/backend thay vì nói người dùng nên làm gì

### 3. Fallback UI là gì?

`Fallback UI` là giao diện dự phòng khi dữ liệu thật chưa có hoặc chưa tải được.

Ví dụ:

- backend chưa trả về sản phẩm
- người dùng chưa đăng nhập
- API lỗi mạng

Lúc đó app vẫn nên giữ cho người dùng thấy được một giao diện có thể tiếp tục khám phá, thay vì trống trơn.

## Vì sao thay đổi này quan trọng?

- Người dùng có thể tự tạo tài khoản ngay trên app.
- Copy trong app chuyển sang giọng điệu cho người dùng cuối.
- Các màn chính có nút và hướng đi rõ hơn.
- `Home` không còn bị "chết" khi backend rỗng.
- `Profile` không còn các dòng placeholder kiểu `later` hay `after integration`.

## Luồng đăng ký và đăng nhập trong project này

### Luồng đăng ký

1. Người dùng mở màn `Register`.
2. `RegisterActivity` đọc dữ liệu form.
3. Activity kiểm tra:
   - họ tên
   - email
   - mật khẩu
   - nhập lại mật khẩu
4. Activity gọi `AuthRepository.register(...)`.
5. `AuthRepository` gọi `AuthApiService.register(...)`.
6. API mobile gửi request đến backend `POST /api/auth/register`.
7. Backend trả về thông báo đăng ký thành công.
8. App hiện thông báo xác thực email và điều hướng về màn đăng nhập.

Các file chính:

- `app/src/main/java/com/example/mobile_obs_asm/RegisterActivity.java`
- `app/src/main/java/com/example/mobile_obs_asm/data/AuthRepository.java`
- `app/src/main/java/com/example/mobile_obs_asm/network/auth/AuthApiService.java`
- `app/src/main/java/com/example/mobile_obs_asm/network/auth/RegisterRequestBody.java`

### Luồng đăng nhập

1. Người dùng nhập email và mật khẩu ở `LoginActivity`.
2. `LoginActivity` gọi `AuthRepository.login(...)`.
3. Repository gọi `POST /api/auth/login`.
4. Nếu thành công, `SessionManager` lưu token và thông tin người dùng.
5. App chuyển sang `MainActivity`.
6. Nếu lỗi, app hiển thị thông báo ngắn gọn cho người dùng.

Các file chính:

- `app/src/main/java/com/example/mobile_obs_asm/LoginActivity.java`
- `app/src/main/java/com/example/mobile_obs_asm/data/AuthRepository.java`
- `app/src/main/java/com/example/mobile_obs_asm/data/SessionManager.java`

## Luồng fallback ở màn Home

### Vấn đề cũ

`HomeFragment` trước đây nạp fake data trước, nhưng nếu backend trả về danh sách rỗng thì adapter lại bị thay bằng danh sách rỗng. Kết quả là:

- người dùng nhìn thấy card trạng thái
- danh sách sản phẩm biến mất
- app có cảm giác như "không bấm được gì"

### Cách sửa

Hiện tại `HomeFragment` giữ một danh sách gợi ý dự phòng từ `FakeMarketplaceRepository`.

Luồng mới:

1. Màn `Home` mở ra.
2. App hiển thị sẵn danh sách gợi ý.
3. Đồng thời app gọi `ProductRemoteRepository.fetchProducts(...)`.
4. Nếu backend có dữ liệu thật:
   - thay bằng dữ liệu thật
   - ẩn state card
5. Nếu backend rỗng hoặc lỗi:
   - giữ lại danh sách gợi ý
   - hiện state card giải thích ngắn gọn
   - vẫn cho người dùng tiếp tục xem sản phẩm

Các file chính:

- `app/src/main/java/com/example/mobile_obs_asm/ui/home/HomeFragment.java`
- `app/src/main/java/com/example/mobile_obs_asm/data/FakeMarketplaceRepository.java`
- `app/src/main/java/com/example/mobile_obs_asm/data/ProductRemoteRepository.java`

## Vì sao đã login nhưng tab protected vẫn có thể báo chưa đăng nhập?

Đây là một lỗi rất hay gặp khi làm mobile app có token.

### Nguyên nhân

Trong Android, `SharedPreferences.apply()` là lưu bất đồng bộ.

Điều đó có nghĩa là:

1. App gọi lưu token.
2. Hệ thống nhận yêu cầu lưu.
3. Nhưng dữ liệu chưa chắc đã được ghi xong ngay tại thời điểm dòng code tiếp theo chạy.

Nếu app vừa lưu token xong đã chuyển màn ngay, các request kế tiếp có thể chạy khi token vẫn chưa sẵn sàng trong storage.

### Cách sửa trong project này

`SessionManager.saveAuthSession(...)` đã được đổi từ:

- `apply()`

sang:

- `commit()`

`commit()` là lưu đồng bộ, nghĩa là app chỉ đi tiếp sau khi token đã được ghi xong.

Điều này đặc biệt quan trọng với luồng:

1. login thành công
2. mở `MainActivity`
3. người dùng bấm ngay sang `Wishlist` hoặc `Orders`

Các file chính:

- `app/src/main/java/com/example/mobile_obs_asm/data/SessionManager.java`
- `app/src/main/java/com/example/mobile_obs_asm/LoginActivity.java`
- `app/src/main/java/com/example/mobile_obs_asm/network/AuthHeaderInterceptor.java`

## Biến chip thành filter thật trong Home

### Vấn đề cũ

4 nhóm:

- Đường trường
- Địa hình nhẹ
- Đi phố
- Cổ điển

trước đây chỉ là `TextView`.

Nghĩa là chúng chỉ hiển thị chứ không có hành vi.

### Cách sửa

`Home` bây giờ dùng `ChipGroup` và `Chip` checkable.

Luồng chạy:

1. Người dùng chạm vào một chip.
2. `HomeFragment` nhận biết chip nào đang được chọn.
3. App lọc lại `sourceProducts`.
4. Adapter nhận danh sách mới.
5. RecyclerView hiển thị đúng nhóm xe tương ứng.

Nếu bộ lọc không có kết quả:

- app hiện state card báo chưa có xe trong nhóm đó
- kèm nút `Bỏ lọc`

Các file chính:

- `app/src/main/res/layout/fragment_home.xml`
- `app/src/main/java/com/example/mobile_obs_asm/ui/home/HomeFragment.java`

## Ghi chú về mock data

Ở thời điểm hiện tại, `Home` vẫn có thể hiển thị mock data trong 2 trường hợp:

1. backend chưa trả về sản phẩm
2. app không kết nối được backend sản phẩm

Mục tiêu của mock data trong nhánh này là:

- giữ cho UI luôn có thứ để xem và để bấm
- tránh cảm giác app bị "chết"

Nhưng với `Wishlist` và `Orders`, khi người dùng đã đăng nhập và backend trả dữ liệu thật thì app sẽ ưu tiên dữ liệu thật trước.

## Vì sao Profile phải bỏ placeholder?

Những dòng như:

- edit profile later
- review history after order completion
- security setup after auth integration

không giúp người dùng làm gì cả. Chúng chỉ nói rằng app "chưa xong".

Thay vào đó, `ProfileFragment` bây giờ tập trung vào:

- thông tin tài khoản
- khu vực
- vai trò
- số đơn và số xe đã lưu
- nút thật để đi tới:
  - đơn mua
  - yêu thích
  - đăng nhập hoặc đăng ký
  - đăng xuất

Các file chính:

- `app/src/main/java/com/example/mobile_obs_asm/ui/profile/ProfileFragment.java`
- `app/src/main/res/layout/fragment_profile.xml`

## Một bài học thực tế cho người mới học Android

Khi làm mobile app có API, đừng chỉ hỏi:

- "API đã gọi chưa?"

Hãy hỏi thêm:

- "Nếu API chưa có dữ liệu thì người dùng còn làm được gì?"
- "Người dùng có nhìn ra đâu là nút cần bấm không?"
- "Thông báo đang nói về hệ thống hay đang hướng dẫn người dùng?"

Đó là khác biệt giữa:

- app chỉ "chạy được"
- và app "dùng được"

## Sai lầm thường gặp

### 1. Dùng copy kỹ thuật cho người dùng cuối

Ví dụ xấu:

- sync backend
- fallback preview
- API products

Người dùng bình thường không cần biết những chi tiết đó.

### 2. Xem card click như một nút đủ rõ ràng

Nhiều khi card có `setOnClickListener`, nhưng người dùng không nhận ra.

Vì vậy trong đợt này, danh sách sản phẩm và đơn mua được bổ sung nút nhìn thấy rõ như:

- `Xem chi tiết`

### 3. Để màn hình rỗng hoàn toàn khi dữ liệu chưa sẵn sàng

Khi dữ liệu thật chưa có, nên có:

- dữ liệu gợi ý
- CTA quay về trang chủ
- CTA đăng nhập
- CTA thử lại

## Tóm tắt áp dụng trong project

- Thêm `RegisterActivity` và request body cho API đăng ký.
- Cập nhật `LoginActivity` để có 3 hướng rõ ràng: đăng nhập, tạo tài khoản, xem thử.
- Viết lại toàn bộ user-facing copy trong `strings.xml` theo tiếng Việt cho người dùng cuối.
- Sửa `HomeFragment` để luôn giữ được danh sách gợi ý khi backend rỗng hoặc lỗi.
- Viết lại `ProfileFragment` thành màn thao tác thật, không còn placeholder nội bộ.
- Bổ sung CTA rõ ràng hơn cho product card và order card.

Nếu đọc code theo luồng, nên đi theo thứ tự này:

1. `LoginActivity`
2. `RegisterActivity`
3. `AuthRepository`
4. `SessionManager`
5. `HomeFragment`
6. `ProfileFragment`
7. `FakeMarketplaceRepository`
