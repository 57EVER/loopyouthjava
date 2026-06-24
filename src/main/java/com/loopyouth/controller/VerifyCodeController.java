package com.loopyouth.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.Random;

@Controller
@RequestMapping("/user")
public class VerifyCodeController {

    private static final String CHARS = "abcdefghijklmnopqrstuvwsyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    @GetMapping("/verifycode/")
    public void verifyCode(HttpSession session, HttpServletResponse response) throws Exception {
        int width = 100;
        int height = 34;
        Random random = new Random();

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        g.setColor(new Color(224, 224, 224));
        g.fillRect(0, 0, width, height);

        for (int i = 0; i < 100; i++) {
            g.setColor(new Color(random.nextInt(256), 255, random.nextInt(256)));
            g.drawRect(random.nextInt(width), random.nextInt(height), 1, 1);
        }

        StringBuilder randStr = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            randStr.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }

        g.setFont(new Font("Arial", Font.BOLD, 23));
        g.setColor(new Color(255, random.nextInt(256), random.nextInt(256)));
        g.drawString(String.valueOf(randStr.charAt(0)), 5, 24);
        g.drawString(String.valueOf(randStr.charAt(1)), 25, 24);
        g.drawString(String.valueOf(randStr.charAt(2)), 50, 24);
        g.drawString(String.valueOf(randStr.charAt(3)), 75, 24);
        g.dispose();

        session.setAttribute("verifycode", randStr.toString());

        response.setContentType("image/png");
        response.setHeader("Cache-Control", "no-cache");
        OutputStream out = response.getOutputStream();
        ImageIO.write(image, "png", out);
        out.flush();
        out.close();
    }
}
