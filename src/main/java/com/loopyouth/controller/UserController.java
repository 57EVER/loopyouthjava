package com.loopyouth.controller;

import com.loopyouth.entity.*;
import com.loopyouth.service.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final CartService cartService;
    private final GoodsService goodsService;
    private final OrderService orderService;

    @GetMapping("/register/")
    public String register(Model model) {
        model.addAttribute("title", "用户注册");
        return "df_user/register";
    }

    @PostMapping("/register_handle/")
    public String registerHandle(@RequestParam("user_name") String username,
                                 @RequestParam("pwd") String password,
                                 @RequestParam("confirm_pwd") String confirmPwd,
                                 @RequestParam("email") String email,
                                 Model model) {
        if (!password.equals(confirmPwd)) {
            return "redirect:/user/register/";
        }
        userService.register(username, password, email);
        model.addAttribute("title", "用户登陆");
        model.addAttribute("username", username);
        return "df_user/login";
    }

    @GetMapping("/register_exist/")
    @ResponseBody
    public Map<String, Long> registerExist(@RequestParam(value = "uname", required = false) String uname,
                                           @RequestParam(value = "uemail", required = false) String uemail) {
        Map<String, Long> result = new HashMap<>();
        result.put("count", uname != null ? userService.countByUname(uname) : 0L);
        result.put("email_count", uemail != null ? userService.countByUemail(uemail) : 0L);
        return result;
    }

    @GetMapping("/login/")
    public String login(Model model, HttpServletRequest request) {
        String uname = "";
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("uname".equals(c.getName())) {
                    uname = c.getValue();
                    break;
                }
            }
        }
        model.addAttribute("title", "用户登陆");
        model.addAttribute("error_name", 0);
        model.addAttribute("error_pwd", 0);
        model.addAttribute("error_vc", 0);
        model.addAttribute("uname", uname);
        return "df_user/login";
    }

    @PostMapping("/login_handle/")
    public String loginHandle(@RequestParam("username") String uname,
                              @RequestParam("pwd") String upwd,
                              @RequestParam(value = "jizhu", defaultValue = "0") String jizhu,
                              @RequestParam(value = "vc", defaultValue = "") String vc,
                              HttpSession session, HttpServletRequest request,
                              HttpServletResponse response, Model model) {
        String verifycode = (String) session.getAttribute("verifycode");
        String vcUpper = vc.toUpperCase();
        String verifycodeUpper = verifycode != null ? verifycode.toUpperCase() : "";

        UserInfo user = userService.login(uname, upwd);

        if (user != null) {
            if (!vcUpper.equals(verifycodeUpper)) {
                model.addAttribute("title", "用户名登陆");
                model.addAttribute("error_name", 0);
                model.addAttribute("error_pwd", 0);
                model.addAttribute("error_vc", 1);
                model.addAttribute("uname", uname);
                model.addAttribute("upwd", upwd);
                return "df_user/login";
            }
            if (!user.getUnamePassOrfail()) {
                model.addAttribute("title", "用户名登陆");
                model.addAttribute("uname", uname);
                model.addAttribute("message", "你的账号存在违规行为，已被封禁。");
                return "df_user/login";
            }

            String url = "/";
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie c : cookies) {
                    if ("url".equals(c.getName())) {
                        url = c.getValue();
                        break;
                    }
                }
            }

            if (!"0".equals(jizhu)) {
                Cookie cookie = new Cookie("uname", uname);
                cookie.setPath("/");
                response.addCookie(cookie);
            } else {
                Cookie cookie = new Cookie("uname", "");
                cookie.setMaxAge(0);
                cookie.setPath("/");
                response.addCookie(cookie);
            }

            session.setAttribute("user_id", user.getId());
            session.setAttribute("user_name", uname);
            return "redirect:" + url;
        } else {
            long count = userService.countByUname(uname);
            if (count == 0) {
                model.addAttribute("title", "用户名登陆");
                model.addAttribute("error_name", 1);
                model.addAttribute("error_pwd", 0);
                model.addAttribute("error_vc", 0);
                model.addAttribute("uname", uname);
                return "df_user/login";
            } else {
                model.addAttribute("title", "用户名登陆");
                model.addAttribute("error_name", 0);
                model.addAttribute("error_pwd", 1);
                model.addAttribute("error_vc", 0);
                model.addAttribute("uname", uname);
                return "df_user/login";
            }
        }
    }

    @GetMapping("/logout/")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/info/")
    public String info(Model model, HttpSession session) {
        Integer uid = (Integer) session.getAttribute("user_id");
        if (uid == null) return "redirect:/user/login/";

        UserInfo user = userService.findById(uid);
        List<GoodsBrowser> browserGoods = userService.getBrowseHistory(uid);
        long cartNum = cartService.countByUserId(uid);

        List<GoodsInfo> goodsList = browserGoods.stream().map(GoodsBrowser::getGood).toList();
        String explain = goodsList.isEmpty() ? "无最近浏览" : "最近浏览";

        model.addAttribute("title", "用户中心");
        model.addAttribute("page_name", 1);
        model.addAttribute("guest_cart", 1);
        model.addAttribute("cart_num", cartNum);
        model.addAttribute("user_phone", user.getUphone());
        model.addAttribute("user_address", user.getUaddress());
        model.addAttribute("user_name", user.getUname());
        model.addAttribute("user", user);
        model.addAttribute("ucheck_passOrfail", user.getUcheckPassOrfail());
        model.addAttribute("goods_list", goodsList);
        model.addAttribute("explain", explain);

        return "df_user/user_center_info";
    }

    @GetMapping("/order/{index}")
    public String order(@PathVariable("index") int index, Model model, HttpSession session) {
        Integer uid = (Integer) session.getAttribute("user_id");
        if (uid == null) return "redirect:/user/login/";

        UserInfo user = userService.findById(uid);
        var page = orderService.findOrdersByUserId(uid, index, 2);
        long cartNum = cartService.countByUserId(uid);

        model.addAttribute("title", "用户中心");
        model.addAttribute("page_name", 1);
        model.addAttribute("guest_cart", 1);
        model.addAttribute("cart_num", cartNum);
        model.addAttribute("page", page);
        model.addAttribute("user", user);

        return "df_user/user_center_order";
    }

    @GetMapping("/site/")
    public String site(Model model, HttpSession session) {
        Integer uid = (Integer) session.getAttribute("user_id");
        if (uid == null) return "redirect:/user/login/";

        UserInfo user = userService.findById(uid);
        long cartNum = cartService.countByUserId(uid);

        model.addAttribute("page_name", 1);
        model.addAttribute("title", "用户中心");
        model.addAttribute("user", user);
        model.addAttribute("guest_cart", 1);
        model.addAttribute("cart_num", cartNum);

        return "df_user/user_center_site";
    }

    @PostMapping("/site/")
    public String sitePost(@RequestParam("ushou") String ushou,
                           @RequestParam("uaddress") String uaddress,
                           @RequestParam("uyoubian") String uyoubian,
                           @RequestParam("uphone") String uphone,
                           HttpSession session) {
        Integer uid = (Integer) session.getAttribute("user_id");
        if (uid == null) return "redirect:/user/login/";

        userService.updateAddress(uid, ushou, uaddress, uyoubian, uphone);
        return "redirect:/user/site/";
    }

    @GetMapping("/publishers/")
    public String publishers(Model model, HttpSession session) {
        Integer uid = (Integer) session.getAttribute("user_id");
        if (uid == null) return "redirect:/user/login/";

        UserInfo user = userService.findById(uid);
        long cartNum = cartService.countByUserId(uid);
        List<TypeInfo> typeInfos = goodsService.findAllTypes();

        model.addAttribute("page_name", 1);
        model.addAttribute("title", "用户中心");
        model.addAttribute("user", user);
        model.addAttribute("typeinfos", typeInfos);
        model.addAttribute("guest_cart", 1);
        model.addAttribute("cart_num", cartNum);

        return "df_user/user_publishers";
    }

    @PostMapping("/publishers/")
    public String publishersPost(@RequestParam("title") String gtitle,
                                 @RequestParam(value = "pic", required = false) MultipartFile gpic,
                                 @RequestParam("price") String gprice,
                                 @RequestParam("jianjie") String gjianjie,
                                 @RequestParam("kucun") String gkucun,
                                 @RequestParam("content") String gcontent,
                                 @RequestParam("type_id") Integer gtypeId,
                                 HttpSession session, RedirectAttributes ra) {
        Integer uid = (Integer) session.getAttribute("user_id");
        if (uid == null) return "redirect:/user/login/";

        UserInfo user = userService.findById(uid);

        if (gtitle.isEmpty() || gprice.isEmpty() || gjianjie.isEmpty() || gkucun.isEmpty() || gcontent.isEmpty()) {
            ra.addFlashAttribute("message", "请完整填充信息！");
            return "redirect:/user/publishers/";
        }
        if (Integer.parseInt(gprice) >= 100000) {
            ra.addFlashAttribute("message", "价格不能大于100000！");
            return "redirect:/user/publishers/";
        }

        goodsService.publishGoods(gtitle, gpic, user.getUname(), gprice, gjianjie, gkucun, gcontent, gtypeId);
        ra.addFlashAttribute("message", "发布商品成功");
        return "redirect:/user/publishers/";
    }

    @GetMapping("/changeInformation/")
    public String changeInformation(Model model, HttpSession session) {
        Integer uid = (Integer) session.getAttribute("user_id");
        if (uid == null) return "redirect:/user/login/";

        UserInfo user = userService.findById(uid);
        long cartNum = cartService.countByUserId(uid);

        model.addAttribute("page_name", 1);
        model.addAttribute("title", "用户中心");
        model.addAttribute("user", user);
        model.addAttribute("guest_cart", 1);
        model.addAttribute("cart_num", cartNum);

        return "df_user/user_changeInformation";
    }

    @PostMapping("/changeInformation/")
    public String changeInformationPost(@RequestParam(value = "logo", required = false) MultipartFile logo,
                                        @RequestParam("sex") String sex,
                                        @RequestParam("age") String age,
                                        @RequestParam("personinf") String personinf,
                                        HttpSession session) {
        Integer uid = (Integer) session.getAttribute("user_id");
        if (uid == null) return "redirect:/user/login/";

        userService.updateProfile(uid, sex, age, personinf, logo);
        return "redirect:/user/changeInformation/";
    }

    @GetMapping("/changeInPwd/")
    public String changeInPwd(Model model, HttpSession session) {
        Integer uid = (Integer) session.getAttribute("user_id");
        if (uid == null) return "redirect:/user/login/";

        UserInfo user = userService.findById(uid);
        long cartNum = cartService.countByUserId(uid);

        model.addAttribute("page_name", 1);
        model.addAttribute("title", "用户中心");
        model.addAttribute("user", user);
        model.addAttribute("guest_cart", 1);
        model.addAttribute("cart_num", cartNum);

        return "df_user/user_changePwd";
    }

    @PostMapping("/changeInPwd/")
    public String changeInPwdPost(@RequestParam("password") String password,
                                  @RequestParam("password2") String password2,
                                  HttpSession session, RedirectAttributes ra) {
        Integer uid = (Integer) session.getAttribute("user_id");
        if (uid == null) return "redirect:/user/login/";

        if (password.isEmpty() || password2.isEmpty()) {
            ra.addFlashAttribute("message", "请输入要修改的密码！");
        } else if (password.equals(password2)) {
            userService.changePassword(uid, password);
            ra.addFlashAttribute("message", "修改成功！");
        } else {
            ra.addFlashAttribute("message", "两次密码输入不正确！");
        }
        return "redirect:/user/changeInPwd/";
    }

    @GetMapping("/shoper_information/{cname}/")
    public String shoperInformation(@PathVariable("cname") String cname,
                                    Model model,
                                    HttpSession session,
                                    HttpServletRequest request,    // 🔥 注入 request
                                    HttpServletResponse response) { // 🔥 注入 response

        Integer uid = (Integer) session.getAttribute("user_id");
        if (uid == null) {
            // 1. 获取用户是从哪个页面点过来的（即商品详情页 URL）
            String referer = request.getHeader("Referer");

            // 如果拿到了来源（商品页），就用来源；拿不到，就用当前的卖家页
            String targetUrl = (referer != null && !referer.isEmpty()) ? referer : request.getRequestURI();

            // 2. 写入名为 "url" 的 Cookie，告诉 login_handle 登录完去这里
            Cookie urlCookie = new Cookie("url", targetUrl);
            urlCookie.setPath("/");       // 确保全站可读
            urlCookie.setMaxAge(60);      // 1分钟内有效即可
            response.addCookie(urlCookie);

            // 3. 拦截跳转登录
            return "redirect:/user/login/";
        }

        // 后面原封不动...
        UserInfo user = userService.findById(uid);
        UserInfo shoper = userService.findByUname(cname);
        List<GoodsInfo> goods = goodsService.findBySeller(cname);

        List<OrderDetailInfo> orderinfs = orderService.findOrderDetailsBySeller(cname);
        List<GoodsInfo> infors = goodsService.findBySeller(cname);

        model.addAttribute("goods", goods);
        model.addAttribute("orderinfs", orderinfs);
        model.addAttribute("name", cname);
        model.addAttribute("user", user);
        model.addAttribute("shoper", shoper);
        model.addAttribute("infos", infors);

        return "df_user/shoper_information";
    }

    @PostMapping("/shoper_information/{cname}/")
    public String shoperInformationPost(@PathVariable("cname") String cname,
                                        @RequestParam(value = "title", defaultValue = "") String ctitle,
                                        @RequestParam(value = "Message", defaultValue = "") String message,
                                        HttpSession session, RedirectAttributes ra) {
        Integer uid = (Integer) session.getAttribute("user_id");
        if (uid == null) return "redirect:/user/login/";

        if (ctitle.isEmpty() || message.isEmpty()) {
            ra.addFlashAttribute("message", "请把信息填完整，卖家才能更快回复你哦！");
            return "redirect:/user/shoper_information/" + cname + "/";
        }

        UserInfo user = userService.findById(uid);
        UserInfo shoper = userService.findByUname(cname);

        // 发送消息到数据库
        userService.sendMessage(ctitle, user.getUname(), cname, message, shoper.getId());

        // 🔥 删除了原来那句烦人的 ra.addFlashAttribute("message", "消息发送成功");

        // 🔥 核心修复：发送完毕后，带着卖家的名字，直接重定向到消息中心的专属对话框！
        return "redirect:/user/person_message/?username=" + cname;
    }

    @GetMapping("/myself_information/")
    public String myselfInformation(Model model, HttpSession session) {
        Integer uid = (Integer) session.getAttribute("user_id");
        if (uid == null) return "redirect:/user/login/";

        UserInfo user = userService.findById(uid);
        List<GoodsInfo> goods = goodsService.findBySeller(user.getUname());
        List<OrderDetailInfo> orderinfs = orderService.findOrderDetailsBySeller(user.getUname());

        model.addAttribute("goods", goods);
        model.addAttribute("orderinfs", orderinfs);
        model.addAttribute("user", user);

        return "df_user/myself_information";
    }

    @GetMapping("/message/")
    public String message(Model model, HttpSession session) {
        Integer uid = (Integer) session.getAttribute("user_id");
        if (uid == null) return "redirect:/user/login/";

        UserInfo user = userService.findById(uid);
        long cartNum = cartService.countByUserId(uid);

        List<Object[]> persons = userService.getDistinctSenders(uid);
        List<UserInfo> imgs = userService.findAllUsers();

        model.addAttribute("title", "消息中心");
        model.addAttribute("page_name", 1);
        model.addAttribute("user", user);
        model.addAttribute("persons", persons);
        model.addAttribute("imgs", imgs);
        model.addAttribute("guest_cart", 1);
        model.addAttribute("cart_num", cartNum);

        return "df_user/user_messages";
    }

    @GetMapping("/person_message/")
    public String personMessage(@RequestParam("username") String username, Model model, HttpSession session) {
        Integer uid = (Integer) session.getAttribute("user_id");
        if (uid == null) return "redirect:/user/login/";

        UserInfo user = userService.findById(uid);
        long cartNum = cartService.countByUserId(uid);

        List<Object[]> persons = userService.getDistinctSenders(uid);
        List<UserInfo> imgs = userService.findAllUsers();

        // ✅ 终极修复：严格过滤，只提取“当前用户”和“目标用户”两人之间的对话
        String myName = user.getUname();
        List<Information> informations = userService.getAllInformation().stream()
                .filter(info ->
                        (info.getCusername().equals(myName) && info.getCusername1().equals(username)) ||
                                (info.getCusername().equals(username) && info.getCusername1().equals(myName))
                )
                .toList();

        UserInfo logo = userService.findByUname(username);
        UserInfo userName = userService.findByUname(username);

        userService.markMessagesAsRead(username, uid);

        model.addAttribute("title", "消息中心");
        model.addAttribute("page_name", 1);
        model.addAttribute("user", user);
        model.addAttribute("informations", informations);
        model.addAttribute("persons", persons);
        model.addAttribute("imgs", imgs);
        model.addAttribute("logo", logo);
        model.addAttribute("username", username);
        model.addAttribute("user_name", userName);
        model.addAttribute("guest_cart", 1);
        model.addAttribute("cart_num", cartNum);

        return "df_user/user_messages";
    }

    @PostMapping("/person_message/")
    public String personMessagePost(@RequestParam("title") String content,
                                    @RequestParam("username") String targetUsername,
                                    HttpSession session, RedirectAttributes ra) {
        Integer uid = (Integer) session.getAttribute("user_id");
        if (uid == null) return "redirect:/user/login/";

        if (content == null || content.isEmpty()) {
            ra.addFlashAttribute("message", "请输入内容！");
            // 🔥 修复1：如果输入为空，也别退出去，留在当前聊天界面
            return "redirect:/user/person_message/?username=" + targetUsername;
        }

        UserInfo user = userService.findById(uid);
        UserInfo target = userService.findByUname(targetUsername);

        userService.sendMessage("", user.getUname(), target.getUname(), content, target.getId());

        // 🔥 修复2：删掉原来那句烦人的 ra.addFlashAttribute("message", "消息发送成功");

        // 🔥 修复3：发送成功后，带着目标用户的名字重定向，让你丝滑地留在当前聊天窗口！
        return "redirect:/user/person_message/?username=" + targetUsername;
    }
}
