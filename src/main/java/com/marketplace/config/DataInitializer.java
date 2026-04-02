package com.marketplace.config;

import com.marketplace.entity.Product;
import com.marketplace.entity.Role;
import com.marketplace.entity.RoleType;
import com.marketplace.entity.User;

import com.marketplace.repository.ProductRepository;
import com.marketplace.repository.RoleRepository;
import com.marketplace.repository.UserRepository;

import lombok.RequiredArgsConstructor;  // For constructor injection of dependencies
import lombok.extern.slf4j.Slf4j;// For logging
import org.springframework.boot.CommandLineRunner;// To run code at application startup
import org.springframework.security.crypto.password.PasswordEncoder; // For encoding passwords before saving to the database
import org.springframework.stereotype.Component; // Marks this class as a Spring component, allowing it to be detected and registered as a bean during component scanning
import org.springframework.transaction.annotation.Transactional; // To ensure that the database operations are executed within a transaction, providing atomicity and rollback capabilities in case of errors

import java.math.BigDecimal; // For representing product prices with high precision
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays; // For creating lists of roles and products to save to the database
import java.util.HashSet; // For initializing sets of roles and products for users and sellers, ensuring no duplicates and efficient lookups
import java.util.List;

/**
 * Initialize database with default roles and admin user
 */
@Component // Marks this class as a Spring component, allowing it to be detected and registered as a bean during component scanning
@RequiredArgsConstructor // Lombok annotation to generate a constructor with required arguments (final fields), enabling constructor injection of dependencies
@Slf4j // Lombok annotation to generate a logger instance for this class, allowing for logging messages at various levels (info, debug, error, etc.)
public class DataInitializer implements CommandLineRunner { // Implements CommandLineRunner to run code at application startup

    private final RoleRepository roleRepository; //
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;// For encoding passwords before saving to the database

    @Override
    @Transactional // To ensure that the database operations are executed within a transaction, providing atomicity and rollback capabilities in case of errors
    public void run(String... args) {
        log.info("Starting database initialization...");
        
        // Initialize roles
        initializeRoles();
        
        // Create default admin user
        createDefaultAdmin();
        
        // Create sample users and products
        createSampleData();
        
        log.info("Database initialization completed successfully");
    }

    private void initializeRoles() {
        log.info("Initializing roles...");
        
        // Check if roles already exist
        if (roleRepository.count() > 0) {
            log.info("Roles already initialized, skipping...");
            return;
        }

        // Create all roles
        Role buyerRole = Role.builder()
                .name(RoleType.BUYER)
                .description("Regular customer who can purchase products")
                .users(new HashSet<>())
                .build();

        Role sellerRole = Role.builder()
                .name(RoleType.SELLER)
                .description("Seller who can list and manage products")
                .users(new HashSet<>())
                .build();

        Role adminRole = Role.builder()
                .name(RoleType.ADMIN)
                .description("Administrator with full system access")
                .users(new HashSet<>())
                .build();

        roleRepository.saveAll(Arrays.asList(buyerRole, sellerRole, adminRole));
        
        log.info("Roles initialized: BUYER, SELLER, ADMIN");
    }

    private void createDefaultAdmin() {
        log.info("Creating default admin user...");
        
        // Check if admin already exists
        if (userRepository.findByUsername("admin").isPresent()) {
            log.info("Admin user already exists, skipping...");
            return;
        }

        // Get admin role 
        Role adminRole = roleRepository.findByName(RoleType.ADMIN)
                .orElseThrow(() -> new RuntimeException("Admin role not found"));

        // Create admin user 
        User admin = User.builder()
                .username("admin")
                .email("admin@marketplace.com")
                .password(passwordEncoder.encode("admin123"))
                .fullName("System Administrator")
                .phoneNumber("0123456789")
                .enabled(true)
                .roles(new HashSet<>())
                .products(new HashSet<>())
                .orders(new HashSet<>())
                .build();

        // Add role directly to the set (avoid helper method in initialization)
        admin.getRoles().add(adminRole);
        userRepository.save(admin);
        
        log.info("Default admin user created - Username: admin, Password: admin123");
        log.info("🔐 IMPORTANT: Please change the admin password after first login!");
    }

    private void createSampleData() {
        log.info("Creating sample sellers and products...");
        
        // Check if products already exist
        if (productRepository.count() > 0) {
                        log.info("Products already exist, updating product image URLs...");
                        updateMissingProductImageUrls();
            return;
        }

        // Create sample sellers
        Role sellerRole = roleRepository.findByName(RoleType.SELLER)
                .orElseThrow(() -> new RuntimeException("Seller role not found"));

        User seller1 = User.builder()
                .username("techseller")
                .email("tech@marketplace.com")
                .password(passwordEncoder.encode("seller123"))
                .fullName("Tech Electronics Store")
                .phoneNumber("01711111111")
                .enabled(true)
                .roles(new HashSet<>())
                .products(new HashSet<>())
                .orders(new HashSet<>())
                .build();
        seller1.getRoles().add(sellerRole);
        seller1 = userRepository.save(seller1);

        User seller2 = User.builder()
                .username("fashionseller")
                .email("fashion@marketplace.com")
                .password(passwordEncoder.encode("seller123"))
                .fullName("Fashion Hub")
                .phoneNumber("01722222222")
                .enabled(true)
                .roles(new HashSet<>())
                .products(new HashSet<>())
                .orders(new HashSet<>())
                .build();
        seller2.getRoles().add(sellerRole);
        seller2 = userRepository.save(seller2);

        // Create sample products for tech seller
        Product laptop = Product.builder()
                .name("Dell Inspiron 15 Laptop")
                .description("15.6 inch FHD display, Intel Core i5, 8GB RAM, 512GB SSD")
                .price(new BigDecimal("55000.00"))
                .category("Electronics")
                .imageUrl(null)
                .stockQuantity(25)
                .available(true)
                .seller(seller1)
                .orders(new HashSet<>())
                .build();

        Product smartphone = Product.builder()
                .name("Samsung Galaxy A54")
                .description("6.4 inch AMOLED, 128GB storage, 50MP camera, 5000mAh battery")
                .price(new BigDecimal("38000.00"))
                .category("Electronics")
                .imageUrl(null)
                .stockQuantity(50)
                .available(true)
                .seller(seller1)
                .orders(new HashSet<>())
                .build();

        Product headphones = Product.builder()
                .name("Sony WH-1000XM5 Headphones")
                .description("Wireless noise cancelling headphones, 30hr battery life")
                .price(new BigDecimal("28000.00"))
                .category("Electronics")
                .imageUrl(null)
                .stockQuantity(15)
                .available(true)
                .seller(seller1)
                .orders(new HashSet<>())
                .build();

        Product smartwatch = Product.builder()
                .name("Apple Watch Series 9")
                .description("GPS, fitness tracking, heart rate monitor, water resistant")
                .price(new BigDecimal("42000.00"))
                .category("Electronics")
                .imageUrl(null)
                .stockQuantity(20)
                .available(true)
                .seller(seller1)
                .orders(new HashSet<>())
                .build();

        // Create sample products for fashion seller
        Product tshirt = Product.builder()
                .name("Premium Cotton T-Shirt")
                .description("100% cotton, comfortable fit, available in multiple colors")
                .price(new BigDecimal("1200.00"))
                .category("Fashion")
                .imageUrl(null)
                .stockQuantity(100)
                .available(true)
                .seller(seller2)
                .orders(new HashSet<>())
                .build();

        Product jeans = Product.builder()
                .name("Denim Slim Fit Jeans")
                .description("Stylish slim fit denim jeans, premium quality")
                .price(new BigDecimal("2500.00"))
                .category("Fashion")
                .imageUrl(null)
                .stockQuantity(75)
                .available(true)
                .seller(seller2)
                .orders(new HashSet<>())
                .build();

        Product sneakers = Product.builder()
                .name("Nike Air Max Sneakers")
                .description("Comfortable running shoes with air cushioning")
                .price(new BigDecimal("8500.00"))
                .category("Fashion")
                .imageUrl(null)
                .stockQuantity(40)
                .available(true)
                .seller(seller2)
                .orders(new HashSet<>())
                .build();

        Product jacket = Product.builder()
                .name("Leather Jacket")
                .description("Genuine leather jacket, water resistant, warm")
                .price(new BigDecimal("12000.00"))
                .category("Fashion")
                .imageUrl(null)
                .stockQuantity(30)
                .available(true)
                .seller(seller2)
                .orders(new HashSet<>())
                .build();

        // Create more diverse products - Electronics
        Product tablet = Product.builder()
                .name("iPad Pro 11-inch")
                .description("11-inch Liquid Retina display, M2 chip, 256GB storage")
                .price(new BigDecimal("82000.00"))
                .category("Electronics")
                .imageUrl(null)
                .stockQuantity(12)
                .available(true)
                .seller(seller1)
                .orders(new HashSet<>())
                .build();

        Product camera = Product.builder()
                .name("Canon EOS R6 Camera")
                .description("Full-frame mirrorless camera, 20MP, 4K video, body only")
                .price(new BigDecimal("220000.00"))
                .category("Electronics")
                .imageUrl(null)
                .stockQuantity(5)
                .available(true)
                .seller(seller1)
                .orders(new HashSet<>())
                .build();

        Product keyboard = Product.builder()
                .name("Mechanical Gaming Keyboard")
                .description("RGB backlit, Cherry MX switches, programmable keys")
                .price(new BigDecimal("7500.00"))
                .category("Electronics")
                .imageUrl(null)
                .stockQuantity(35)
                .available(true)
                .seller(seller1)
                .orders(new HashSet<>())
                .build();

        Product mouse = Product.builder()
                .name("Logitech MX Master 3S")
                .description("Wireless mouse, ergonomic design, 8K DPI sensor")
                .price(new BigDecimal("9500.00"))
                .category("Electronics")
                .imageUrl(null)
                .stockQuantity(28)
                .available(true)
                .seller(seller1)
                .orders(new HashSet<>())
                .build();

        Product monitor = Product.builder()
                .name("27-inch 4K Monitor")
                .description("IPS display, 4K UHD resolution, HDR10, USB-C")
                .price(new BigDecimal("32000.00"))
                .category("Electronics")
                .imageUrl(null)
                .stockQuantity(18)
                .available(true)
                .seller(seller1)
                .orders(new HashSet<>())
                .build();

        // Fashion & Clothing
        Product dress = Product.builder()
                .name("Summer Floral Dress")
                .description("Light cotton fabric, beautiful floral pattern, casual wear")
                .price(new BigDecimal("2800.00"))
                .category("Fashion")
                .imageUrl(null)
                .stockQuantity(55)
                .available(true)
                .seller(seller2)
                .orders(new HashSet<>())
                .build();

        Product hoodie = Product.builder()
                .name("Premium Cotton Hoodie")
                .description("Soft fleece interior, kangaroo pocket, pullover style")
                .price(new BigDecimal("3200.00"))
                .category("Fashion")
                .imageUrl(null)
                .stockQuantity(65)
                .available(true)
                .seller(seller2)
                .orders(new HashSet<>())
                .build();

        Product backpack = Product.builder()
                .name("Travel Backpack 40L")
                .description("Water-resistant, laptop compartment, ergonomic straps")
                .price(new BigDecimal("4500.00"))
                .category("Fashion")
                .imageUrl(null)
                .stockQuantity(42)
                .available(true)
                .seller(seller2)
                .orders(new HashSet<>())
                .build();

        Product sunglasses = Product.builder()
                .name("Polarized Sunglasses")
                .description("UV400 protection, metal frame, classic aviator style")
                .price(new BigDecimal("1800.00"))
                .category("Fashion")
                .imageUrl(null)
                .stockQuantity(88)
                .available(true)
                .seller(seller2)
                .orders(new HashSet<>())
                .build();

        Product watch = Product.builder()
                .name("Analog Wrist Watch")
                .description("Stainless steel, water resistant, leather strap")
                .price(new BigDecimal("6500.00"))
                .category("Fashion")
                .imageUrl(null)
                .stockQuantity(25)
                .available(true)
                .seller(seller2)
                .orders(new HashSet<>())
                .build();

        // Home & Garden
        Product coffeeMaker = Product.builder()
                .name("Espresso Coffee Maker")
                .description("15-bar pump, milk frother, programmable settings")
                .price(new BigDecimal("18000.00"))
                .category("Home & Garden")
                .imageUrl(null)
                .stockQuantity(15)
                .available(true)
                .seller(seller1)
                .orders(new HashSet<>())
                .build();

        Product blender = Product.builder()
                .name("High-Speed Blender")
                .description("1000W motor, 1.5L capacity, stainless steel blades")
                .price(new BigDecimal("5500.00"))
                .category("Home & Garden")
                .imageUrl(null)
                .stockQuantity(32)
                .available(true)
                .seller(seller1)
                .orders(new HashSet<>())
                .build();

        Product vacuumCleaner = Product.builder()
                .name("Cordless Vacuum Cleaner")
                .description("Bagless, HEPA filter, 40min runtime, LED display")
                .price(new BigDecimal("22000.00"))
                .category("Home & Garden")
                .imageUrl(null)
                .stockQuantity(11)
                .available(true)
                .seller(seller1)
                .orders(new HashSet<>())
                .build();

        Product bedSheet = Product.builder()
                .name("Egyptian Cotton Bed Sheet Set")
                .description("King size, 400 thread count, 4-piece set, wrinkle-free")
                .price(new BigDecimal("4800.00"))
                .category("Home & Garden")
                .imageUrl(null)
                .stockQuantity(45)
                .available(true)
                .seller(seller2)
                .orders(new HashSet<>())
                .build();

        // Books
        Product book1 = Product.builder()
                .name("The Psychology of Money")
                .description("Personal finance book by Morgan Housel, bestseller")
                .price(new BigDecimal("550.00"))
                .category("Books")
                .imageUrl(null)
                .stockQuantity(120)
                .available(true)
                .seller(seller2)
                .orders(new HashSet<>())
                .build();

        Product book2 = Product.builder()
                .name("Atomic Habits")
                .description("Self-improvement book by James Clear, proven strategies")
                .price(new BigDecimal("600.00"))
                .category("Books")
                .imageUrl(null)
                .stockQuantity(95)
                .available(true)
                .seller(seller2)
                .orders(new HashSet<>())
                .build();

        Product book3 = Product.builder()
                .name("Think Like a Monk")
                .description("Wisdom book by Jay Shetty, life lessons and mindfulness")
                .price(new BigDecimal("480.00"))
                .category("Books")
                .imageUrl(null)
                .stockQuantity(78)
                .available(true)
                .seller(seller2)
                .orders(new HashSet<>())
                .build();

        // Sports & Fitness
        Product yogaMat = Product.builder()
                .name("Premium Yoga Mat")
                .description("Non-slip, eco-friendly TPE material, 6mm thick, carry strap")
                .price(new BigDecimal("2200.00"))
                .category("Sports")
                .imageUrl(null)
                .stockQuantity(60)
                .available(true)
                .seller(seller2)
                .orders(new HashSet<>())
                .build();

        Product dumbbell = Product.builder()
                .name("Adjustable Dumbbell Set")
                .description("20kg pair, quick-lock design, compact storage")
                .price(new BigDecimal("8500.00"))
                .category("Sports")
                .imageUrl(null)
                .stockQuantity(22)
                .available(true)
                .seller(seller1)
                .orders(new HashSet<>())
                .build();

        Product bicycle = Product.builder()
                .name("Mountain Bike 26-inch")
                .description("21-speed, disc brakes, alloy frame, suspension fork")
                .price(new BigDecimal("25000.00"))
                .category("Sports")
                .imageUrl(null)
                .stockQuantity(8)
                .available(true)
                .seller(seller1)
                .orders(new HashSet<>())
                .build();

        // Beauty & Personal Care
        Product perfume = Product.builder()
                .name("Luxury Eau de Parfum")
                .description("100ml, long-lasting fragrance, elegant bottle")
                .price(new BigDecimal("3500.00"))
                .category("Beauty")
                .imageUrl(null)
                .stockQuantity(48)
                .available(true)
                .seller(seller2)
                .orders(new HashSet<>())
                .build();

        Product skincare = Product.builder()
                .name("Vitamin C Serum")
                .description("Anti-aging, brightening, hyaluronic acid, 30ml")
                .price(new BigDecimal("2100.00"))
                .category("Beauty")
                .imageUrl(null)
                .stockQuantity(72)
                .available(true)
                .seller(seller2)
                .orders(new HashSet<>())
                .build();

        Product hairDryer = Product.builder()
                .name("Ionic Hair Dryer")
                .description("2000W, 3 heat settings, cool shot button, concentrator nozzle")
                .price(new BigDecimal("4200.00"))
                .category("Beauty")
                .imageUrl(null)
                .stockQuantity(28)
                .available(true)
                .seller(seller1)
                .orders(new HashSet<>())
                .build();

        // Save all products
        productRepository.saveAll(Arrays.asList(
            laptop, smartphone, headphones, smartwatch,
            tshirt, jeans, sneakers, jacket,
            tablet, camera, keyboard, mouse, monitor,
            dress, hoodie, backpack, sunglasses, watch,
            coffeeMaker, blender, vacuumCleaner, bedSheet,
            book1, book2, book3,
            yogaMat, dumbbell, bicycle,
            perfume, skincare, hairDryer
        ));

        // Replace temporary seed images with product-relevant URLs.
        updateMissingProductImageUrls();

        log.info("Sample data created: 2 sellers and 31 products across multiple categories");
        log.info("Seller accounts: techseller / fashionseller (password: seller123)");
    }

    private void updateMissingProductImageUrls() {
        List<Product> products = productRepository.findAll();
        int updatedCount = 0;

        for (Product product : products) {
            String currentImageUrl = product.getImageUrl();
            boolean shouldUpdate = currentImageUrl == null
                    || currentImageUrl.isBlank()
                    || currentImageUrl.contains("picsum.photos");

            if (shouldUpdate) {
                product.setImageUrl(buildRelevantImageUrl(product.getName(), product.getCategory()));
                updatedCount++;
            }
        }

        if (updatedCount > 0) {
            productRepository.saveAll(products);
            log.info("Updated relevant image URLs for {} products", updatedCount);
        } else {
            log.info("All products already have non-placeholder image URLs");
        }
    }

    private String buildRelevantImageUrl(String productName, String category) {
        String safeName = (productName == null || productName.isBlank()) ? "product" : productName;
        String safeCategory = (category == null || category.isBlank()) ? "shopping" : category;
        String query = safeName + " " + safeCategory + " product";
        return "https://source.unsplash.com/1200x900/?"
                + URLEncoder.encode(query, StandardCharsets.UTF_8);
    }
}
