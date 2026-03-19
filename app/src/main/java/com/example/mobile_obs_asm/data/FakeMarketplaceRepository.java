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
                "An Nguyen",
                "Buyer",
                "buyer@oldbicycles.vn",
                "Da Nang",
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
                "Balanced endurance frame with a calm road feel.",
                "RD",
                "Ho Chi Minh City",
                "Lightly used",
                "Verified inspection",
                "A smooth endurance road bike aimed at long weekend rides. The frame keeps the position comfortable while still feeling responsive on climbs and city exits.",
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
                "Quick gravel setup for mixed pavement and broken roads.",
                "GV",
                "Ha Noi",
                "Very good",
                "Fresh pick",
                "A practical gravel bike for riders who need one machine for commute, light touring, and rougher urban roads. The wider tire clearance makes this a flexible daily option.",
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
                "Fast city hybrid with upright comfort and reliable braking.",
                "CT",
                "Can Tho",
                "Good",
                "Daily rider",
                "An easy hybrid bike for city movement, bike paths, and fitness rides. The geometry is forgiving and suitable for a rider who wants comfort more than aggressive speed.",
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
                "Vintage steel character with restored commuting charm.",
                "VT",
                "Hue",
                "Restored",
                "Collector mood",
                "A classic steel bicycle refreshed for urban cruising and display value. It is less about racing and more about personality, smooth cruising, and nostalgic presence.",
                "53 cm",
                "700C",
                "Retro friction mix",
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
                "Balanced endurance frame with a calm road feel.",
                "RD",
                "Ho Chi Minh City",
                "Lightly used",
                "Saved comparison",
                "A smooth endurance road bike aimed at long weekend rides. The frame keeps the position comfortable while still feeling responsive on climbs and city exits.",
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
                "Fast city hybrid with upright comfort and reliable braking.",
                "CT",
                "Can Tho",
                "Good",
                "Saved commute option",
                "An easy hybrid bike for city movement, bike paths, and fitness rides. The geometry is forgiving and suitable for a rider who wants comfort more than aggressive speed.",
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
                "Deposit confirmed for Giant Defy Advanced 2",
                "Seller accepted the order and the deposit is waiting for delivery confirmation.",
                4700000L,
                "Deposit confirmed",
                R.color.primary_soft
        ));
        orders.add(new OrderPreview(
                "ORD-2398",
                "Specialized Diverge Elite inspection review",
                "Inspection request is still being reviewed before the next payment step.",
                5780000L,
                "Inspection pending",
                R.color.card_sand
        ));
        orders.add(new OrderPreview(
                "ORD-2387",
                "Trek FX 3 Disc delivery completed",
                "The bike was marked as received and this order is ready for post-purchase review flow.",
                14500000L,
                "Completed",
                R.color.card_mint
        ));
        return orders;
    }
}
