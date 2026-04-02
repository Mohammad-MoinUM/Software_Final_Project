package com.marketplace.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for serving frontend HTML pages
 */
@Controller
public class ViewController {

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @GetMapping("/products")
    public String productsPage() {
        return "products";
    }

    @GetMapping("/product-details")
    public String productDetailsPage() {
        return "product-details";
    }

    @GetMapping("/cart")
    public String cartPage() {
        return "cart";
    }

    @GetMapping("/checkout")
    public String checkoutPage() {
        return "checkout";
    }

    @GetMapping("/dashboard")
    public String dashboardPage() {
        return "dashboard";
    }

    @GetMapping("/wishlist")
    public String wishlistPage() {
        return "wishlist";
    }

    @GetMapping("/order-history")
    public String orderHistoryPage() {
        return "order-history";
    }

    @GetMapping("/seller-dashboard")
    public String sellerDashboardPage() {
        return "seller-dashboard";
    }

    @GetMapping("/product-management")
    public String productManagementPage() {
        return "product-management";
    }

    @GetMapping("/admin-dashboard")
    public String adminDashboardPage() {
        return "admin-dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        // Invalidate session
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/login?logout";
    }
}
