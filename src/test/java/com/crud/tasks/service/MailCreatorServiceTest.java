package com.crud.tasks.service;

import com.crud.tasks.config.AdminConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MailCreatorServiceTest {

    @InjectMocks
    private MailCreatorService mailCreatorService;

    @Mock
    private ITemplateEngine templateEngine;

    @Mock
    private AdminConfiguration adminConfiguration;

    @Mock
    private TrelloService trelloService;

    @Mock
    private TaskService taskService;

    @Test
    void buildTrelloCardEmail() {
        //Given
        String messageBeforeSend = "This is new TrelloCard mail";
        final String HTML_FILE_TRELLO_CARD = "mail/created-trello-card-mail";

        doReturn("Kodilla Administration").when(adminConfiguration).getAdminName();
        when(templateEngine.process((eq(HTML_FILE_TRELLO_CARD)), any(Context.class))).thenReturn("This is new TrelloCard mail");

        //When
        var message = mailCreatorService.buildTrelloCardEmail(messageBeforeSend);

        //Then
        assertEquals(messageBeforeSend, message);
    }

    @Test
    void buildTaskQntInformationEmail() {
        //Given
        String messageBeforeSend = "You have 3 new Tasks. Enjoy!";
        final String HTML_FILE_TASK_QNT = "mail/task-qnt-checkout-mail";

        when(templateEngine.process((eq(HTML_FILE_TASK_QNT)), any(Context.class))).thenReturn("You have 3 new Tasks. Enjoy!");

        //When
        var message = mailCreatorService.buildTaskQntInformationEmail(messageBeforeSend);

        //Then
        assertEquals(messageBeforeSend, message);
    }
}