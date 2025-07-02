package com.example.ejb;

import com.example.entity.Task;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class TaskServiceImpl implements TaskService {
    @PersistenceContext(unitName = "portalPU")
    private EntityManager em;

    @Override
    public List<Task> getAllTasks() {
        return em.createQuery("SELECT t FROM Task t", Task.class).getResultList();
    }

    @Override
    public Task getTask(Long id) {
        return em.find(Task.class, id);
    }

    @Override
    public Task createTask(Task task) {
        em.persist(task);
        return task;
    }

    @Override
    public Task updateTask(Task task) {
        return em.merge(task);
    }

    @Override
    public void deleteTask(Long id) {
        Task task = em.find(Task.class, id);
        if (task != null) {
            em.remove(task);
        }
    }
}