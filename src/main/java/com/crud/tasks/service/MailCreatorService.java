package com.crud.tasks.service;

import com.crud.tasks.config.AdminConfiguration;
import com.crud.tasks.domain.Task;
import com.crud.tasks.repository.TaskRepository;
import com.crud.tasks.trello.client.TrelloClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MailCreatorService {

    @Qualifier("templateEngine")
    private final TemplateEngine _templateEngine;

    private final AdminConfiguration _adminConfiguration;
    private final TaskRepository _taskRepository;
    private final TrelloClient _trelloClient;

    @Autowired
    public MailCreatorService(TemplateEngine templateEngine, AdminConfiguration adminConfiguration, TaskRepository taskRepository,@Lazy TrelloClient trelloClient) {
        this._templateEngine = templateEngine;
        this._adminConfiguration = adminConfiguration;
        this._taskRepository = taskRepository;
        this._trelloClient = trelloClient;
    }

    public String buildTrelloCardEmail(String message) {

        List<String> functionality = new ArrayList<>();
        functionality.add("You can manage your tasks");
        functionality.add("Provides connection with Trello Account");
        functionality.add("Application allows sending tasks to Trello");

        Context context = new Context();
        context.setVariable("preview_message", "Thymeleaf-Test");
        context.setVariable("message", message);
        context.setVariable("application_functionality", functionality);
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

    public String buildTaskQntInformationEmail(String message) {

        //task_name_isOnTrello (task_list)
        //task_name_isNotOnTrello) (not on trello)

        var tasks = _taskRepository.findAll();

        return "";
    }
}
