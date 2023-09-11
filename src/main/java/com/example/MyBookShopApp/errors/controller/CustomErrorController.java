//package com.example.MyBookShopApp.errors.controller;
//import com.example.MyBookShopApp.aspect.annotations.CustomErrorControllerCatchable;
//import org.springframework.boot.web.servlet.error.ErrorController;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//@Controller
//public class CustomErrorController implements ErrorController {
//    @RequestMapping("/error")
//    @CustomErrorControllerCatchable
//    public String getWrongUrl() {
//        return "redirect:/";
//    }
//
//    @Override
//    public String getErrorPath() {
//        return "/error";
//    }
//}
