package com.example.mobile_obs_asm.data;

import com.example.mobile_obs_asm.R;
import com.example.mobile_obs_asm.model.OrderPreview;
import com.example.mobile_obs_asm.model.Product;
import com.example.mobile_obs_asm.model.UserProfile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FakeMarketplaceRepository {

    private static FakeMarketplaceRepository instance;

    private final List<Product> featuredProducts;
    private final List<Product> wishlistProducts;
    private final List<OrderPreview> orderPreviews;
    private final UserProfile userProfile;

    private FakeMarketplaceRepository() {
        featuredProducts = buildFeaturedProducts();
        wishlistProducts = buildWishlistProducts();
        orderPreviews = buildOrders();
        userProfile = new UserProfile(
                "An Nguyễn",
                "Người mua",
                "buyer@oldbicycles.vn",
                "Đà Nẵng",
                3,
                wishlistProducts.size()
        );
    }

    public static FakeMarketplaceRepository getInstance() {
        if (instance == null) {
            instance = new FakeMarketplaceRepository();
        }
        return instance;
    }

    public List<Product> getFeaturedProducts() {
        return Collections.unmodifiableList(featuredProducts);
    }

    public List<Product> getWishlistProducts() {
        return Collections.unmodifiableList(wishlistProducts);
    }

    public void saveWishlistProduct(Product product) {
        if (product == null || findWishlistProduct(product.getId()) != null) {
            return;
        }
        wishlistProducts.add(product);
    }

    public void removeWishlistProduct(String productId) {
        Product existingProduct = findWishlistProduct(productId);
        if (existingProduct != null) {
            wishlistProducts.remove(existingProduct);
        }
    }

    public boolean isWishlistProduct(String productId) {
        return findWishlistProduct(productId) != null;
    }

    public List<OrderPreview> getOrderPreviews() {
        return Collections.unmodifiableList(orderPreviews);
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public Product findProductById(String productId) {
        for (Product product : featuredProducts) {
            if (product.getId().equals(productId)) {
                return product;
            }
        }
        return findWishlistProduct(productId);
    }

    private Product findWishlistProduct(String productId) {
        for (Product product : wishlistProducts) {
            if (product.getId().equals(productId)) {
                return product;
            }
        }
        return null;
    }

    private List<Product> buildFeaturedProducts() {
        List<Product> products = new ArrayList<>();
        products.add(new Product(
                "road-001",
                "Giant Defy Advanced 2",
                "Khung endurance êm, phù hợp cho những buổi đạp xa cuối tuần.",
                "RD",
                "https://picsum.photos/seed/oldbicycles-road-001/900/700",
                "Thành phố Hồ Chí Minh",
                "Đã qua sử dụng nhẹ",
                "Đã kiểm định",
                "Mẫu xe đường trường này phù hợp với người muốn đạp xa nhưng vẫn giữ tư thế thoải mái. Khung xe phản hồi tốt khi lên dốc và vẫn đủ dễ chịu để dùng hằng ngày.",
                "M / 54 cm",
                "700C",
                "Shimano 105",
                23500000L,
                R.color.card_blue,
                R.color.card_ink,
                false
        ));
        products.add(new Product(
                "gravel-002",
                "Specialized Diverge Elite",
                "Cấu hình gravel linh hoạt cho đi phố lẫn đường xấu nhẹ.",
                "GV",
                "https://picsum.photos/seed/oldbicycles-gravel-002/900/700",
                "Hà Nội",
                "Rất tốt",
                "Được quan tâm",
                "Chiếc gravel này phù hợp với người cần một mẫu xe linh hoạt để đi làm, đi chơi cuối tuần và thỉnh thoảng ra khỏi mặt đường nhựa. Khoảng sáng lốp rộng giúp xe dễ thích nghi hơn.",
                "L / 56 cm",
                "700C",
                "GRX 400",
                28900000L,
                R.color.card_mint,
                R.color.banner_olive,
                false
        ));
        products.add(new Product(
                "city-003",
                "Trek FX 3 Disc",
                "Mẫu hybrid nhanh gọn, tư thế ngồi thoải mái và phanh ổn định.",
                "CT",
                "https://picsum.photos/seed/oldbicycles-city-003/900/700",
                "Cần Thơ",
                "Tốt",
                "Đi phố hằng ngày",
                "Đây là lựa chọn dễ tiếp cận cho người cần một chiếc xe đạp đi làm, tập thể dục hoặc di chuyển trong thành phố. Hình học xe thân thiện và ít gây mỏi khi đi lâu.",
                "M",
                "700C",
                "Shimano Deore",
                14500000L,
                R.color.card_sand,
                R.color.banner_warm,
                false
        ));
        products.add(new Product(
                "vintage-004",
                "Peugeot Ventoux Classic",
                "Khung thép cổ điển đã được làm mới để đi phố nhẹ nhàng.",
                "VT",
                "https://picsum.photos/seed/oldbicycles-vintage-004/900/700",
                "Huế",
                "Đã phục dựng",
                "Phong cách sưu tầm",
                "Mẫu xe này nổi bật ở chất cổ điển và cảm giác lái êm. Phù hợp với người thích đi dạo trong phố, chụp ảnh hoặc sưu tầm hơn là theo đuổi tốc độ.",
                "53 cm",
                "700C",
                "Bộ số ma sát cổ điển",
                11800000L,
                R.color.card_peach,
                R.color.accent_gold,
                false
        ));
        return products;
    }

    private List<Product> buildWishlistProducts() {
        List<Product> products = new ArrayList<>();
        products.add(new Product(
                "road-001",
                "Giant Defy Advanced 2",
                "Khung endurance êm, phù hợp cho những buổi đạp xa cuối tuần.",
                "RD",
                "Thành phố Hồ Chí Minh",
                "Đã qua sử dụng nhẹ",
                "Đang cân nhắc",
                "Mẫu xe này đang nằm trong danh sách cần so sánh thêm về giá và tình trạng thực tế trước khi gửi yêu cầu mua.",
                "M / 54 cm",
                "700C",
                "Shimano 105",
                23500000L,
                R.color.card_blue,
                R.color.card_ink,
                false
        ));
        products.add(new Product(
                "city-003",
                "Trek FX 3 Disc",
                "Mẫu hybrid nhanh gọn, tư thế ngồi thoải mái và phanh ổn định.",
                "CT",
                "Cần Thơ",
                "Tốt",
                "Phù hợp đi làm",
                "Đây là mẫu xe được lưu lại để so sánh với những lựa chọn đi phố khác về mức giá và khả năng sử dụng hằng ngày.",
                "M",
                "700C",
                "Shimano Deore",
                14500000L,
                R.color.card_sand,
                R.color.banner_warm,
                false
        ));
        return products;
    }

    private List<OrderPreview> buildOrders() {
        List<OrderPreview> orders = new ArrayList<>();
        orders.add(new OrderPreview(
                "ORD-2403",
                "Đơn mua Giant Defy Advanced 2 đã được chấp nhận",
                "Người bán đã đồng ý đơn và đang chờ bước xác nhận giao xe tiếp theo.",
                4700000L,
                "Đã đặt cọc",
                R.color.primary_soft,
                "Đang giữ tiền",
                "Chuyển khoản",
                "19/03/2026 09:10",
                "20/03/2026 09:10",
                "Người mua: An Nguyễn • Người bán: Minh Trần",
                "Khoản thanh toán trước đã được ghi nhận. Đơn đang ở giai đoạn chờ giao xe và xác nhận hoàn tất.",
                false
        ));
        orders.add(new OrderPreview(
                "ORD-2398",
                "Đơn Specialized Diverge Elite đang chờ người bán phản hồi",
                "Yêu cầu mua đã được gửi và đang chờ người bán chấp nhận bước thanh toán tiếp theo.",
                5780000L,
                "Đang chờ",
                R.color.card_sand,
                "Chờ thanh toán",
                "Tiền mặt",
                "18/03/2026 15:20",
                "19/03/2026 15:20",
                "Người mua: An Nguyễn • Người bán: Huy Phạm",
                "Đơn này vẫn cần người bán xác nhận trước khi quá trình thanh toán hoặc giao nhận có thể tiếp tục.",
                false
        ));
        orders.add(new OrderPreview(
                "ORD-2387",
                "Đơn Trek FX 3 Disc đã hoàn tất giao nhận",
                "Người mua đã xác nhận nhận xe thành công và đơn đã khép lại.",
                14500000L,
                "Hoàn tất",
                R.color.card_mint,
                "Đã giải ngân",
                "Thanh toán online",
                "16/03/2026 11:05",
                "Đã hoàn tất",
                "Người mua: An Nguyễn • Người bán: Quang Hồ",
                "Đây là một đơn đã hoàn tất, phù hợp để xem lại các mốc thanh toán và tình trạng giao nhận.",
                false
        ));
        return orders;
    }
}
