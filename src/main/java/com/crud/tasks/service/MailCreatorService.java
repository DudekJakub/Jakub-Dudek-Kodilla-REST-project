package com.crud.tasks.service;

import com.crud.tasks.config.AdminConfiguration;
import com.crud.tasks.domain.Task;
import com.crud.tasks.exception.TrelloNotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Service
public class MailCreatorService {

    private static final String FRONTEND_URL = "https://dudekjakub.github.io/";
    private static final String TRELLO_URL = "https://trello.com/b/rbKP3Hsq/kodilla-aplication";
    private static final String TASKS_URL = "https://kodilla-task-application.herokuapp.com/v1/task/getTasks";
    private static final String HTML_FILE_TRELLO_CARD = "mail/created-trello-card-mail";
    private static final String HTML_FILE_TASK_QNT = "mail/task-qnt-checkout-mail";

    @Qualifier("templateEngine")
    private final ITemplateEngine _templateEngine;
    private final AdminConfiguration _adminConfiguration;
    private final TrelloService _trelloService;
    private final TaskService _taskService;

    @Autowired
    public MailCreatorService(ITemplateEngine templateEngine, AdminConfiguration adminConfiguration,@Lazy TrelloService trelloService,@Lazy TaskService taskService) {
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

        List<String> tasksWithTrelloStatus = this.returnTasksWithTrelloStatusList();

        Context context = new Context();
        setContext(context, "Task Quantity Checkout", message, TRELLO_URL, "Check Trello",true, true);
        context.setVariable("tasks_button", "Check Tasks");
        context.setVariable("tasks_url", TASKS_URL);
        context.setVariable("task_and_trello_map", tasksWithTrelloStatus);
        context.setVariable("goodbye_message", "Have a good day & good luck with tasks!");
        return _templateEngine.process(HTML_FILE_TASK_QNT, context);
    }

    private List<String> returnTasksWithTrelloStatusList() {
        return _taskService.getAllTasks().stream()
                .map(this::mapTaskToString)
                .collect(Collectors.toList());
    }

    private String mapTaskToString(Task task) {
        var isOnTrello = _taskService.checkIfTaskIsOnTrello(task.getId());
        var getTrelloCardByName = _trelloService.getTrelloCardByName(task.getTitle())
                .orElseThrow(() -> new TrelloNotFound(TrelloNotFound.CARD_NF));
        var getCardOwnerList = _trelloService.getNameOfTrelloListThatContainsCard(getTrelloCardByName.getId());

        if (isOnTrello) {
            return " (" + task.getTitle() + getCardOwnerList + ")";
        } else {
            return " (Not found on Trello)";
        }
    }

    private void setContext(Context context, String previewMessage, String message, String defaultUrl, String buttonValue, boolean showButton, boolean isFriend) {
        context.setVariable("preview_message", previewMessage);
        context.setVariable("message", message);
        context.setVariable("user_nickName", _adminConfiguration);
        context.setVariable("default_button", buttonValue);
        context.setVariable("show_button", showButton);
        context.setVariable("default_url", defaultUrl);
        context.setVariable("is_friend", isFriend);
        context.setVariable("company_detail_name", _adminConfiguration.getCompanyName());
        context.setVariable("company_detail_goal", _adminConfiguration.getCompanyGoal());
        context.setVariable("company_detail_email", _adminConfiguration.getCompanyEmail());
        context.setVariable("company_detail_phone", _adminConfiguration.getCompanyPhone());
    }
}
