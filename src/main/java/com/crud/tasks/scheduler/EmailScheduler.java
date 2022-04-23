package com.crud.tasks.scheduler;

import com.crud.tasks.config.AdminConfiguration;
import com.crud.tasks.domain.Mail;
import com.crud.tasks.repository.TaskRepository;
import com.crud.tasks.service.SimpleEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailScheduler {

    private final SimpleEmailService simpleEmailService;
    private final TaskRepository taskRepository;
    private final AdminConfiguration adminConfig;

    private static final String SUBJECT = "Tasks: Once a day email";

//    @Scheduled(fixedRate = 60000) //send mail every 10 sec
//    @Scheduled(cron = "0 0 10 * * MON-FRI") //send mail at 10 o'clock every day between MON-FRI
    public void sendInformationEmail() {
        long size = taskRepository.count();

        String task;
        if(size == 1) {
            task = " task";
        } else {
            task = " tasks";
        }

        simpleEmailService.sendTaskQntMail(
                new Mail(
                        adminConfig.getAdminMail(),
                        SUBJECT,
                        "Currently in database you got: " + size + task,
                        null
                ));
    }
}
