package com.stockflow.controller;

import com.stockflow.model.Product;
import com.stockflow.model.ProductForm;
import com.stockflow.model.Notification;
import com.stockflow.model.Order;
import com.stockflow.repository.NotificationRepository;
import com.stockflow.service.CartService;
import com.stockflow.service.OrderService;
import com.stockflow.service.ProductService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class AdminController {

    private final ProductService productService;
    private final OrderService orderService;
    private final NotificationRepository notificationRepository;
    private final CartService cartService;

    public AdminController(ProductService productService,
                           OrderService orderService,
                           NotificationRepository notificationRepository,
                           CartService cartService) {
        this.productService = productService;
        this.orderService = orderService;
        this.notificationRepository = notificationRepository;
        this.cartService = cartService;
    }

    @GetMapping("/admin")
    public String dashboard(HttpSession session, Model model) {
        model.addAttribute("totalOrders", orderService.countOrders());
        model.addAttribute("totalProducts", productService.countProducts());
        model.addAttribute("lowStock", productService.countLowStock());
        model.addAttribute("revenue", productService.calculateRevenue());
        model.addAttribute("unreadNotifications", notificationRepository.countByReadFalse());
        model.addAttribute("cartSize", cartService.getCartSize(session));
        return "admin/dashboard";
    }

    @GetMapping("/admin/products")
    public String productsPage(HttpSession session, Model model) {
        model.addAttribute("products", productService.getAllProducts());
        if (!model.containsAttribute("productForm")) {
            model.addAttribute("productForm", new ProductForm());
        }
        model.addAttribute("editingProduct", null);
        model.addAttribute("showModal", false);
        model.addAttribute("cartSize", cartService.getCartSize(session));
        return "admin/product";
    }

    @PostMapping("/admin/products/add")
    public String addProduct(@Valid @ModelAttribute("productForm") ProductForm productForm,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.productForm", bindingResult);
            redirectAttributes.addFlashAttribute("productForm", productForm);
            return "redirect:/admin/products";
        }
        productService.addProduct(productForm.getName(), productForm.getPrice(), productForm.getStock(), productForm.getIcon());
        redirectAttributes.addFlashAttribute("message", "Product added successfully.");
        return "redirect:/admin/products";
    }

    @GetMapping("/admin/products/edit/{id}")
    public String editProductPage(@PathVariable("id") String id,
                                  HttpSession session,
                                  Model model) {
        Product product = productService.getById(id).orElse(null);
        if (product == null) {
            model.addAttribute("error", "Product not found.");
            return productsPage(session, model);
        }
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("editingProduct", product);
        model.addAttribute("productForm", new ProductForm(product.getName(), product.getIcon(), product.getPrice(), product.getStock()));
        model.addAttribute("showModal", true);
        model.addAttribute("cartSize", cartService.getCartSize(session));
        return "admin/product";
    }

    @PostMapping("/admin/products/edit/{id}")
    public String updateProduct(@PathVariable("id") String id,
                                @Valid @ModelAttribute("productForm") ProductForm productForm,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.productForm", bindingResult);
            redirectAttributes.addFlashAttribute("productForm", productForm);
            return "redirect:/admin/products/edit/" + id;
        }
        productService.updateProduct(id, productForm.getName(), productForm.getPrice(), productForm.getStock());
        redirectAttributes.addFlashAttribute("message", "Product updated successfully.");
        return "redirect:/admin/products";
    }

    @PostMapping("/admin/products/delete/{id}")
    public String deleteProduct(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
        if (productService.deleteProduct(id)) {
            redirectAttributes.addFlashAttribute("message", "Product deleted successfully.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Product not found.");
        }
        return "redirect:/admin/products";
    }

    @GetMapping("/admin/orders")
    public String adminOrders(HttpSession session, Model model) {
        List<Order> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);
        model.addAttribute("statuses", Order.STATUSES);
        model.addAttribute("cartSize", cartService.getCartSize(session));
        return "admin/orders";
    }

    @PostMapping("/admin/orders/{id}/status")
    public String updateOrderStatus(@PathVariable("id") String id,
                                    @ModelAttribute("status") String status,
                                    RedirectAttributes redirectAttributes) {
        orderService.updateStatus(id, status)
            .ifPresentOrElse(o -> redirectAttributes.addFlashAttribute("message", "Order status updated."),
                () -> redirectAttributes.addFlashAttribute("error", "Order not found."));
        return "redirect:/admin/orders";
    }

    @GetMapping("/admin/notifications")
    public String notificationsPage(HttpSession session, Model model) {
        List<Notification> notifications = notificationRepository.findAllByOrderByDateDesc();
        model.addAttribute("notifications", notifications);
        model.addAttribute("cartSize", cartService.getCartSize(session));
        return "admin/notifications";
    }

    @PostMapping("/admin/notifications/{id}/read")
    public String markNotificationRead(@PathVariable("id") String id,  
                                       RedirectAttributes redirectAttributes) {
        notificationRepository.findById(id).ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
        redirectAttributes.addFlashAttribute("message", "Notification marked as read.");
        return "redirect:/admin/notifications";
    }
}
