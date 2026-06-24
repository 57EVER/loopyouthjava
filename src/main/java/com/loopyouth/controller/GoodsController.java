package com.loopyouth.controller;

import com.loopyouth.entity.*;
import com.loopyouth.service.CartService;
import com.loopyouth.service.GoodsService;
import com.loopyouth.service.OrderService;
import com.loopyouth.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class GoodsController {

    private final GoodsService goodsService;
    private final CartService cartService;
    private final UserService userService;
    private final OrderService orderService;

    @GetMapping({"", "/", "/index"})
    public String index(Model model, HttpSession session) {
        String username = (String) session.getAttribute("user_name");
        UserInfo user = null;
        if (username != null) {
            user = userService.findByUname(username);
        }

        String username1 = "customer";
        List<Information> informations = userService.getAllInformation();
        List<Information> informations1 = null;
        if (username != null) {
            informations1 = userService.getInformationBySenderAndReceiver(username, username1);
        }

        UserInfo customerUser = userService.findByUname(username1);
        String nowTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        List<TypeInfo> typeList = goodsService.findAllTypes();

        int cartNum = 0;
        Integer userId = (Integer) session.getAttribute("user_id");
        if (userId != null) {
            cartNum = (int) cartService.countByUserId(userId);
        }

        model.addAttribute("title", "首页");
        model.addAttribute("cart_num", cartNum);
        model.addAttribute("guest_cart", userId != null ? 1 : 0);
        model.addAttribute("user", user);
        model.addAttribute("informations", informations);
        model.addAttribute("informations1", informations1);
        model.addAttribute("username1", username1);
        model.addAttribute("user_name", customerUser);
        model.addAttribute("nowTime", nowTime);

        for (int i = 0; i < typeList.size() && i < 6; i++) {
            TypeInfo type = typeList.get(i);
            model.addAttribute("type" + i, goodsService.findLatestByType(type.getId(), 4));
            model.addAttribute("type" + i + "1", goodsService.findHottestByType(type.getId(), 4));
        }

        return "df_goods/index";
    }

    @PostMapping({"", "/", "/index"})
    public String indexPost(@RequestParam("title") String content, HttpSession session, RedirectAttributes ra) {
        Integer userId = (Integer) session.getAttribute("user_id");
        String username = (String) session.getAttribute("user_name");
        if (userId == null || content == null || content.isEmpty()) {
            ra.addFlashAttribute("message", "请输入内容！");
            return "redirect:/";
        }

        UserInfo user = userService.findById(userId);
        UserInfo customer = userService.findByUname("customer");

        userService.sendMessage("", user.getUname(), customer.getUname(), content, customer.getId());
        ra.addFlashAttribute("message", "消息发送成功");
        return "redirect:/";
    }

    @GetMapping("/list{tid}_{pindex}_{sort}/")
    public String goodList(@PathVariable("tid") Integer tid,
                           @PathVariable("pindex") Integer pindex,
                           @PathVariable("sort") String sort,
                           Model model, HttpSession session) {
        String username = (String) session.getAttribute("user_name");
        UserInfo user = username != null ? userService.findByUname(username) : null;

        Page<GoodsInfo> page = goodsService.findByTypeWithSort(tid, sort, pindex, 4);
        List<GoodsInfo> news = goodsService.findLatestByType(tid, 2);

        int cartNum = 0;
        int guestCart = 0;
        Integer userId = (Integer) session.getAttribute("user_id");
        if (userId != null) {
            guestCart = 1;
            cartNum = (int) cartService.countByUserId(userId);
        }

        List<TypeInfo> allTypes = goodsService.findAllTypes();
        TypeInfo typeinfo = allTypes.stream().filter(t -> t.getId().equals(tid)).findFirst().orElse(null);

        model.addAttribute("title", "商品列表");
        model.addAttribute("guest_cart", guestCart);
        model.addAttribute("cart_num", cartNum);
        model.addAttribute("page", page);
        model.addAttribute("typeinfo", typeinfo);
        model.addAttribute("sort", sort);
        model.addAttribute("news", news);
        model.addAttribute("user", user);

        return "df_goods/list";
    }

    @GetMapping("/detail/{gid}/")
    public String detail(@PathVariable("gid") Integer gid, Model model, HttpSession session) {
        GoodsInfo goods = goodsService.findGoodsById(gid);
        goodsService.incrementClick(gid);

        List<GoodsInfo> news = goodsService.findLatestByType(goods.getGtype().getId(), 2);

        Integer userId = (Integer) session.getAttribute("user_id");
        int guestCart = 0;
        UserInfo user = null;

        if (userId != null) {
            guestCart = 1;
            user = userService.findById(userId);
            userService.recordBrowse(userId, gid);
        }

        model.addAttribute("title", goods.getGtype().getTtitle());
        model.addAttribute("guest_cart", guestCart);
        model.addAttribute("cart_num", userId != null ? cartService.countByUserId(userId) : 0);
        model.addAttribute("goods", goods);
        model.addAttribute("news", news);
        model.addAttribute("id", gid);
        model.addAttribute("user", user);

        return "df_goods/detail";
    }

    @GetMapping("/content/{gid}/{pindex}/")
    public String content(@PathVariable("gid") Integer gid,
                          @PathVariable("pindex") Integer pindex,
                          Model model, HttpSession session) {
        GoodsInfo goods = goodsService.findGoodsById(gid);
        List<GoodsInfo> news = goodsService.findLatestByType(goods.getGtype().getId(), 2);
        Page<GoodsContent> page = goodsService.findCommentsByGoodsId(gid, pindex, 2);
        List<OrderDetailInfo> allDetails = orderService.findAllOrderDetails();

        Integer userId = (Integer) session.getAttribute("user_id");
        int guestCart = 0;
        UserInfo user = null;
        if (userId != null) {
            guestCart = 1;
            user = userService.findById(userId);
        }

        model.addAttribute("title", goods.getGtype().getTtitle());
        model.addAttribute("guest_cart", guestCart);
        model.addAttribute("cart_num", userId != null ? cartService.countByUserId(userId) : 0);
        model.addAttribute("goods", goods);
        model.addAttribute("id", gid);
        model.addAttribute("news", news);
        model.addAttribute("user", user);
        model.addAttribute("page", page);
        model.addAttribute("goodsOrderDetailInfos", allDetails);

        return "df_goods/content";
    }

    @PostMapping("/content/{gid}/{pindex}/")
    public String contentPost(@PathVariable("gid") Integer gid,
                              @PathVariable("pindex") Integer pindex,
                              @RequestParam(value = "pic", required = false) MultipartFile pic,
                              @RequestParam("text") String text,
                              HttpSession session, RedirectAttributes ra) {
        Integer userId = (Integer) session.getAttribute("user_id");
        if (userId == null) {
            return "redirect:/user/login/";
        }

        UserInfo user = userService.findById(userId);
        GoodsInfo goods = goodsService.findGoodsById(gid);

        goodsService.addComment(goods.getGtitle(), pic, user.getUname(), user.getUlogo(), text, gid);
        ra.addFlashAttribute("message", "评论成功！");

        return "redirect:/content/" + gid + "/" + pindex + "/";
    }

    @GetMapping("/search")
    public String search(@RequestParam(value = "q", defaultValue = "") String keyword,
                         @RequestParam(value = "pindex", defaultValue = "1") int pindex,
                         Model model, HttpSession session) {
        String username = (String) session.getAttribute("user_name");
        UserInfo user = username != null ? userService.findByUname(username) : null;

        int cartNum = 0;
        int guestCart = 0;
        Integer userId = (Integer) session.getAttribute("user_id");
        if (userId != null) {
            guestCart = 1;
            cartNum = (int) cartService.countByUserId(userId);
        }

        int searchStatus = 1;
        Page<GoodsInfo> page = goodsService.searchGoods(keyword, pindex, 4);
        if (page.getTotalElements() == 0) {
            searchStatus = 0;
        }

        model.addAttribute("title", "搜索列表");
        model.addAttribute("search_status", searchStatus);
        model.addAttribute("guest_cart", guestCart);
        model.addAttribute("cart_num", cartNum);
        model.addAttribute("page", page);
        model.addAttribute("user", user);

        return "df_goods/ordinary_search";
    }
}
