package com.example.ejb;

import com.example.entity.Task;

import javax.ejb.Local;
import java.util.List;

@Local
public interface TaskService {
    List<Task> getAllTasks();
    Task getTask(Long id);
    Task createTask(Task task);
    Task updateTask(Task task);
    void deleteTask(Long id);
}