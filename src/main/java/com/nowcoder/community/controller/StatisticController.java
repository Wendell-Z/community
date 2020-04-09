package com.nowcoder.community.controller;

import com.nowcoder.community.service.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping(value = "/statistic")
public class StatisticController {

    @Autowired
    private StatisticService statisticService;

    //    @GetMapping(value = "")
//    @PostMapping(value = "")
    @RequestMapping(value = "", method = {RequestMethod.POST, RequestMethod.GET})
    public String statistic() {
        return "/site/admin/data";
    }

    @PostMapping(value = "/uv")
    public String getUV(@DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate, @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate, Model model) {
        long uv = statisticService.getUVCount(startDate, endDate);
        model.addAttribute("uvResult", uv);
        model.addAttribute("uvStartDate", startDate);
        model.addAttribute("uvEndDate", endDate);
        return "forward:/statistic/";

    }

    // 统计活跃用户
    @PostMapping(value = "/dau")
    public String getDAU(@DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                         @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate, Model model) {
        long dau = statisticService.getDAUCount(startDate, endDate);
        model.addAttribute("dauResult", dau);
        model.addAttribute("dauStartDate", startDate);
        model.addAttribute("dauEndDate", endDate);
        return "forward:/statistic/";
    }
}
