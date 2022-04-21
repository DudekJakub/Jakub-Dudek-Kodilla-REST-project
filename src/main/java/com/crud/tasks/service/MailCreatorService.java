package com.crud.tasks.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class MailCreatorService {

    @Qualifier("templateEngine")
    TemplateEngine _templateEngine;

    @Autowired
    public MailCreatorService(TemplateEngine templateEngine) {
        this._templateEngine = templateEngine;
    }

    public String buildTrelloCardEmail(String message) {
        Context context = new Context();
        context.setVariable("message", message);
        context.setVariable("tasks_url", "https://dudekjakub.github.io/");
        context.setVariable("button", "Visit website");
        return _templateEngine.process("mail/created-trello-card-mail",context);
    }
}
