package com.ssginc.unnie.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/community")
public class AdminReportViewController {

    @GetMapping("/report")
    public String reportPage(Model model) {
        model.addAttribute("activePage", "report");
        return "admin/community/getAllReport";
    }


}