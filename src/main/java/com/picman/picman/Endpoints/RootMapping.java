package com.picman.picman.Endpoints;

import com.picman.picman.SpringSettings.Settings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/")
public class RootMapping {

    @GetMapping("/contacts")
    public String contacts(Model model) {
        model.addAttribute("path", "/ contacts");
        model.addAttribute("superadmin", Settings.get("super_admin_id"));
        model.addAttribute("orgMail", Settings.get("organization_contact_mail"));
        model.addAttribute("orgPhone", Settings.get("organization_phone_number"));
        model.addAttribute("orgAddress", Settings.get("organization_address"));
        model.addAttribute("orgWebsite", Settings.get("organization_website"));
        model.addAttribute("supportids", Settings.get("support_ids").split(","));
        return "contacts";
    }

    @GetMapping("/pricing")
    public String pricing(Model model) {

        return "pricing";
    }
}
