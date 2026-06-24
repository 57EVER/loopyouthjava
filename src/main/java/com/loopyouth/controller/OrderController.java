package com.loopyouth.controller;

import com.loopyouth.entity.CartInfo;
import com.loopyouth.entity.UserInfo;
import com.loopyouth.service.CartService;
import com.loopyouth.service.OrderService;
import com.loopyouth.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;
    private final UserService userService;

    @GetMapping({"", "/"})
    public String order(@RequestParam("cart_id") List<String> cartIds,
                        Model model,
                        HttpSession session,
                        HttpServletRequest request, HttpServletResponse response) {
        Integer uid = (Integer) session.getAttribute("user_id");
        if (uid == null) {
            // 修复：记录点击立即购买或去结算时的页面
            String referer = request.getHeader("Referer");
            String targetUrl = (referer != null && !referer.isEmpty()) ? referer : request.getRequestURI();
            Cookie urlCookie = new Cookie("url", targetUrl);
            urlCookie.setPath("/");
            urlCookie.setMaxAge(60);
            response.addCookie(urlCookie);
            return "redirect:/user/login/";
        }

        UserInfo user = userService.findById(uid);

        List<CartInfo> carts = new ArrayList<>();
        double totalPrice = 0;
        for (String cartIdStr : cartIds) {
            CartInfo cart = cartService.findById(Integer.parseInt(cartIdStr));
            if (cart != null) {
                carts.add(cart);
                totalPrice += cart.getCount() * cart.getGoods().getGprice().doubleValue();
            }
        }

        totalPrice = Math.round(totalPrice * 100.0) / 100.0;
        double transCost = 10;
        double totalTransPrice = transCost + totalPrice;

        model.addAttribute("title", "提交订单");
        model.addAttribute("page_name", 1);
        model.addAttribute("user", user);
        model.addAttribute("carts", carts);
        model.addAttribute("total_price", totalPrice);
        model.addAttribute("trans_cost", transCost);
        model.addAttribute("total_trans_price", totalTransPrice);

        return "df_order/place_order";
    }

    @PostMapping("/push/")
    @ResponseBody
    public Map<String, Object> orderHandle(@RequestParam("cart_ids") String cartIds,
                                           @RequestParam("total") String total,
                                           HttpSession session) {
        Integer uid = (Integer) session.getAttribute("user_id");
        Map<String, Object> data = new HashMap<>();

        if (uid == null) {
            return data;
        }

        try {
            UserInfo user = userService.findById(uid);
            String result = orderService.createOrder(uid, cartIds, new BigDecimal(total), user.getUaddress());

            if ("OK".equals(result)) {
                data.put("ok", 1);
            }
        } catch (RuntimeException e) {
            if ("STOCK_NOT_ENOUGH".equals(e.getMessage())) {
                data.put("error", "库存不足");
            }
        }

        return data;
    }
}