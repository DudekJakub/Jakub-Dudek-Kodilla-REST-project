package com.crud.tasks.service;

import com.crud.tasks.config.AdminConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class MailCreatorService {

    @Qualifier("templateEngine")
    private final TemplateEngine _templateEngine;

    private final AdminConfiguration _adminConfiguration;

    @Autowired
    public MailCreatorService(TemplateEngine templateEngine, AdminConfiguration adminConfiguration) {
        this._templateEngine = templateEngine;
        this._adminConfiguration = adminConfiguration;
    }

    public String buildTrelloCardEmail(String message) {
        Context context = new Context();
        context.setVariable("preview_message", "Thymeleaf-Test");
        context.setVariable("message", message);
        context.setVariable("tasks_url", "https://dudekjakub.github.io/");
        context.setVariable("button", "Visit website");
        context.setVariable("show_button", false);
        context.setVariable("is_friend", true);
        context.setVariable("user_nickName", _adminConfiguration);
        context.setVariable("goodbye_message", "Yours faithfully " + _adminConfiguration.getAdminName());
        context.setVariable("admin_name", _adminConfiguration);
        context.setVariable("company_detail_name", _adminConfiguration.getCompanyName());
        context.setVariable("company_detail_goal", _adminConfiguration.getCompanyGoal());
        context.setVariable("company_detail_email", _adminConfiguration.getCompanyEmail());
        context.setVariable("company_detail_phone", _adminConfiguration.getCompanyPhone());
        return _templateEngine.process("mail/created-trello-card-mail",context);
    }
}
