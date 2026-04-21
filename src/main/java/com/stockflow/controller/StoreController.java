package com.stockflow.controller;

import com.stockflow.model.CartItem;
import com.stockflow.model.SignupForm;
import com.stockflow.service.CartService;
import com.stockflow.service.OrderService;
import com.stockflow.service.ProductService;
import com.stockflow.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;
import java.util.List;

@Controller
public class StoreController {

    private final ProductService productService;
    private final CartService cartService;
    private final OrderService orderService;
    private final UserService userService;

    public StoreController(ProductService productService,
                           CartService cartService,
                           OrderService orderService,
                           UserService userService) {
        this.productService = productService;
        this.cartService = cartService;
        this.orderService = orderService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/store";
    }

    @GetMapping("/store")
    public String storePage(@RequestParam(name = "search", required = false) String search,
                            HttpSession session,
                            Model model) {
        try {
            model.addAttribute("products",
                search != null && !search.isBlank()
                    ? productService.searchProducts(search)
                    : productService.getAllProducts());
        } catch (Exception e) {
            model.addAttribute("products", java.util.Collections.emptyList());
        }
        model.addAttribute("search", search != null ? search : "");
        model.addAttribute("cartSize", cartService.getCartSize(session));
        model.addAttribute("activePage", "store");
        return "store/index";
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(name = "error", required = false) String error,
                            @RequestParam(name = "logout", required = false) String logout,
                            Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid username or password.");
        }
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully.");
        }
        return "auth/login";
    }

    @GetMapping("/signup")
    public String signupPage(Model model) {
        if (!model.containsAttribute("signupForm")) {
            model.addAttribute("signupForm", new SignupForm());
        }
        return "auth/signup";
    }

    @PostMapping("/signup")
    public String registerUser(@Valid @ModelAttribute("signupForm") SignupForm signupForm,
                               BindingResult bindingResult,
                               Model model) {
        if (bindingResult.hasErrors()) {
            return "auth/signup";
        }

        try {
            userService.register(signupForm.getUsername(),
                signupForm.getEmail(),
                signupForm.getPassword(),
                signupForm.getRole());
            model.addAttribute("message", "Account created successfully. Please log in.");
            return "auth/login";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            return "auth/signup";
        }
    }

    @GetMapping("/orders")
    public String ordersPage(HttpSession session,
                             Principal principal,
                             Model model) {
        if (principal == null || principal.getName() == null || principal.getName().isBlank()) {
            return "redirect:/login";
        }

        List<com.stockflow.model.Order> orders = orderService.getOrdersByCustomer(principal.getName());
        model.addAttribute("orders", orders);
        model.addAttribute("cartSize", cartService.getCartSize(session));
        return "store/orders";
    }

    @GetMapping("/cart")
    public String cartPage(HttpSession session,
                           @RequestParam(name = "error", required = false) String error,
                           @RequestParam(name = "message", required = false) String message,
                           Model model) {
        model.addAttribute("cartItems", cartService.getCart(session));
        model.addAttribute("cartSize", cartService.getCartSize(session));
        model.addAttribute("subtotal", cartService.getSubtotal(session));
        model.addAttribute("tax", cartService.getTax(session));
        model.addAttribute("total", cartService.getTotal(session));
        if (error != null) {
            model.addAttribute("error", error);
        }
        if (message != null) {
            model.addAttribute("message", message);
        }
        return "store/cart";
    }

    @PostMapping("/cart/add/{id}")
    public String addToCart(@PathVariable("id") String id,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        String error = cartService.addToCart(session, id);
        if (error != null) {
            redirectAttributes.addFlashAttribute("error", error);
        } else {
            redirectAttributes.addFlashAttribute("message", "Product added to cart.");
        }
        return "redirect:/store";
    }

    @PostMapping("/cart/remove/{id}")
    public String removeFromCart(@PathVariable("id") String id,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        cartService.removeFromCart(session, id);
        redirectAttributes.addFlashAttribute("message", "Item removed from cart.");
        return "redirect:/cart";
    }

    @PostMapping("/cart/checkout")
    public String checkoutCart(HttpSession session,
                               Principal principal,
                               RedirectAttributes redirectAttributes) {
        if (principal == null || principal.getName() == null || principal.getName().isBlank()) {
            return "redirect:/login";
        }

        var userOptional = userService.findByUsername(principal.getName());
        if (userOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Authenticated user not found.");
            return "redirect:/cart";
        }

        List<CartItem> cartItems = cartService.getCart(session);
        if (cartItems.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Your cart is empty.");
            return "redirect:/cart";
        }

        try {
            orderService.placeOrder(userOptional.get(), cartItems);
            cartService.clearCart(session);
            redirectAttributes.addFlashAttribute("message", "Your order has been placed.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/cart";
    }
}
