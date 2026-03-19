# Order Detail Và Remove Wishlist Trong App Mobile

## 1. Bối cảnh

Sau khi app đã có:

- tạo order
- xem danh sách orders
- xem danh sách wishlist

thì vẫn còn hai khoảng trống dễ thấy:

1. người dùng chưa bấm vào một order để xem chi tiết
2. người dùng chưa xóa được sản phẩm khỏi wishlist

Nếu thiếu hai phần này, app sẽ có cảm giác mới chỉ “xem danh sách”, chưa đủ vòng thao tác thực tế.

## 2. Khái niệm chính

### `Order Detail` là gì?

`Order Detail` là màn hình cho người dùng xem sâu hơn một order:

- trạng thái
- funding status
- payment method
- thời điểm tạo
- deadline
- các bên tham gia
- ghi chú tóm tắt

Nó giúp danh sách order không còn là điểm dừng cuối cùng.

### `Remove Wishlist` là gì?

Đây là thao tác xóa một sản phẩm đã lưu khỏi wishlist.

Về mặt backend, thao tác này map sang:

- `DELETE /api/wishlist/{productId}`

Về mặt UI, nó phải có action đủ rõ để người dùng hiểu rằng item sẽ bị bỏ khỏi danh sách đã lưu.

## 3. Luồng runtime của Order Detail

1. Người dùng mở tab `Orders`.
2. `OrdersFragment` nạp danh sách order vào `OrderAdapter`.
3. Người dùng bấm vào một order card.
4. `OrderAdapter` gọi callback click.
5. App mở `OrderDetailActivity`.
6. `OrderPreview` được truyền qua `Parcelable`.
7. `OrderDetailActivity` bind dữ liệu lên các card chi tiết.

Điểm đáng chú ý là:

- app chưa có endpoint order detail riêng
- vì vậy detail hiện được dựng từ dữ liệu đã có sẵn trong list response

Đây là cách làm rất hợp lý cho MVP vì giảm số request nhưng vẫn cho người dùng xem sâu hơn.

## 4. Luồng runtime của Remove Wishlist

## Trường hợp 1: Demo mode

1. Người dùng mở tab `Wishlist` bằng demo mode.
2. Danh sách hiện từ `FakeMarketplaceRepository`.
3. Người dùng bấm `Remove`.
4. Adapter xóa item ngay trong danh sách local.
5. App hiện toast báo đây chỉ là xóa cục bộ ở demo mode.

Trường hợp này không gọi backend.

## Trường hợp 2: Backend session thật

1. Người dùng mở tab `Wishlist` với backend session đang hoạt động.
2. `WishlistFragment` đang hiển thị dữ liệu thật.
3. Người dùng bấm `Remove` trên một card.
4. `WishlistFragment` gọi `WishlistRemoteRepository.removeProduct(...)`.
5. Repository dùng `WishlistApiService` gọi `DELETE /api/wishlist/{productId}`.
6. Nếu backend thành công, adapter xóa item khỏi danh sách.
7. Nếu xóa xong mà danh sách rỗng, UI chuyển sang `empty state`.
8. Nếu backend lỗi, `SectionStateController` hiện `error state` với nút `Retry`.

## 5. Vì sao ProductAdapter được mở rộng thay vì tạo adapter mới?

Trong project này, `ProductAdapter` vốn đã được dùng cho:

- `Home`
- `Wishlist`

Để tránh tạo thêm một adapter gần như giống hệt chỉ khác mỗi nút phụ, project mở rộng `ProductAdapter` theo hướng:

- vẫn có click mở detail như cũ
- có thể truyền thêm `actionLabel` và `actionListener` khi cần

Kết quả:

- `Home` không bị ảnh hưởng
- `Wishlist` có thêm nút `Remove`
- code gọn hơn

## 6. Những file chính nên đọc

- `app/src/main/java/com/example/mobile_obs_asm/OrderDetailActivity.java`
- `app/src/main/java/com/example/mobile_obs_asm/model/OrderPreview.java`
- `app/src/main/java/com/example/mobile_obs_asm/ui/orders/OrderAdapter.java`
- `app/src/main/java/com/example/mobile_obs_asm/ui/orders/OrdersFragment.java`
- `app/src/main/java/com/example/mobile_obs_asm/ui/home/ProductAdapter.java`
- `app/src/main/java/com/example/mobile_obs_asm/ui/wishlist/WishlistFragment.java`
- `app/src/main/java/com/example/mobile_obs_asm/data/WishlistRemoteRepository.java`
- `app/src/main/res/layout/activity_order_detail.xml`
- `app/src/main/res/layout/item_product_card.xml`

## 7. Sơ đồ luồng đơn giản

```mermaid
flowchart TD
    A[OrdersFragment] --> B[OrderAdapter]
    B --> C[OrderDetailActivity]
    D[WishlistFragment] --> E[ProductAdapter]
    E --> F{Demo hay backend session?}
    F -- Demo --> G[Xóa local item]
    F -- Backend --> H[WishlistRemoteRepository]
    H --> I[DELETE /api/wishlist/{productId}]
    I --> J[Adapter cập nhật lại list]
    J --> K[Empty state nếu danh sách rỗng]
```

## 8. Sai lầm thường gặp

### Chỉ cho xem list mà không có detail

Điều này làm người dùng không biết tiếp theo nên làm gì với một order cụ thể.

### Xóa wishlist nhưng không cập nhật UI ngay

Nếu backend xóa xong mà adapter không xóa item khỏi danh sách, người dùng sẽ tưởng app lỗi.

### Không phân biệt demo mode và backend mode

Nếu demo mode mà vẫn giả vờ gọi backend, bạn sẽ gặp:

- id giả
- request sai
- trải nghiệm khó hiểu

Project này xử lý rõ:

- demo mode thì xóa local
- backend mode thì gọi API thật

## 9. Điều quan trọng cần nhớ

Một list screen tốt thường cần hai hướng thao tác:

- đi sâu vào detail
- chỉnh lại chính item trong list

Lần cập nhật này làm đúng hai điều đó:

- `Orders` giờ đi sâu được vào `Order Detail`
- `Wishlist` giờ bỏ item ra được

Đó là bước nhỏ nhưng rất quan trọng để app bớt cảm giác “chỉ là demo UI”.
