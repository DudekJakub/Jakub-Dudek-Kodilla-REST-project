package com.crud.tasks.service;

import com.crud.tasks.config.AdminConfiguration;
import com.crud.tasks.domain.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MailCreatorService {

    private static final String FRONTEND_URL = "https://dudekjakub.github.io/";
    private static final String TRELLO_URL = "https://trello.com/b/rbKP3Hsq/kodilla-aplication";
    private static final String HTML_FILE_TRELLO_CARD = "mail/created-trello-card-mail";
    private static final String HTML_FILE_TASK_QNT = "mail/task-qnt-checkout-mai";

    @Qualifier("templateEngine")
    private final TemplateEngine _templateEngine;

    private final AdminConfiguration _adminConfiguration;
    private final TrelloService _trelloService;
    private final TaskService _taskService;

    @Autowired
    public MailCreatorService(TemplateEngine templateEngine, AdminConfiguration adminConfiguration,@Lazy TrelloService trelloService, TaskService taskService) {
        this._templateEngine = templateEngine;
        this._adminConfiguration = adminConfiguration;
        this._trelloService = trelloService;
        this._taskService = taskService;
    }

    public String buildTrelloCardEmail(String message) {

        List<String> functionality = new ArrayList<>();
        functionality.add("You can manage your tasks");
        functionality.add("Provides connection with Trello Account");
        functionality.add("Application allows sending tasks to Trello");

        Context context = new Context();
        setContext(context, "Thymeleaf-Test", message, FRONTEND_URL, "Visit website",true, false);
        context.setVariable("application_functionality", functionality);
        context.setVariable("goodbye_message", "Yours faithfully " + _adminConfiguration.getAdminName());
        return _templateEngine.process(HTML_FILE_TRELLO_CARD, context);
    }

    public String buildTaskQntInformationEmail(String message) {

        Map<String, String> mapOfTasksTitlesAndTrelloStatus = _taskService.getAllTasks()
                .stream()
                .collect(Collectors.toMap(
                        Task::getTitle,
                        task -> _taskService.checkIfTaskIsOnTrello(task.getId()) ?
                                " (" + _trelloService.getTrelloListById(_trelloService.getTrelloCardByName(task.getTitle()).get().getListId()).getName() + ")"
                                :
                                " (Not found on Trello)" ));

        Context context = new Context();
        setContext(context, "Task Quantity Checkout", message, TRELLO_URL, "Check Trello",true, true);
        context.setVariable("task_and_trello_map", mapOfTasksTitlesAndTrelloStatus);
        context.setVariable("goodbye_message", "See ya soon ~DJ");
        return _templateEngine.process(HTML_FILE_TASK_QNT, context);
    }

    private void setContext(Context context, String previewMessage, String message, String defaultUrl, String buttonValue, boolean showButton, boolean isFriend) {
        context.setVariable("preview_message", previewMessage);
        context.setVariable("message", message);
        context.setVariable("user_nickName", _adminConfiguration);
        context.setVariable("button", buttonValue);
        context.setVariable("show_button", showButton);
        context.setVariable("default_url", defaultUrl);
        context.setVariable("is_friend", isFriend);
        context.setVariable("company_detail_name", _adminConfiguration.getCompanyName());
        context.setVariable("company_detail_goal", _adminConfiguration.getCompanyGoal());
        context.setVariable("company_detail_email", _adminConfiguration.getCompanyEmail());
        context.setVariable("company_detail_phone", _adminConfiguration.getCompanyPhone());
    }
}
