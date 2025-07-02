package com.example.rest;

import com.example.ejb.TaskService;
import com.example.entity.Task;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/tasks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TaskResource {
    @EJB
    private TaskService taskService;

    @GET
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    @GET
    @Path("/{id}")
    public Task getTask(@PathParam("id") Long id) {
        return taskService.getTask(id);
    }

    @POST
    public Task createTask(Task task) {
        return taskService.createTask(task);
    }

    @PUT
    @Path("/{id}")
    public Task updateTask(@PathParam("id") Long id, Task task) {
        task.setId(id);
        return taskService.updateTask(task);
    }

    @DELETE
    @Path("/{id}")
    public Response deleteTask(@PathParam("id") Long id) {
        taskService.deleteTask(id);
        return Response.noContent().build();
    }
}