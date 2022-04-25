package com.crud.tasks.exception;

public class TaskNotFound extends IllegalStateException {
    public static final String TASK_NF = "Couldn't find any task by id";

    public TaskNotFound(String s) {
        super(s);
    }
}
