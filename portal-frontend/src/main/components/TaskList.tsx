import React, { useState, useEffect } from 'react';
import axios from 'axios';

interface Task {
  id: number;
  title: string;
  completed: boolean;
}

const TaskList: React.FC = () => {
  const [tasks, setTasks] = useState<Task[]>([]);

  useEffect(() => {
    fetchTasks();
  }, []);

  const fetchTasks = async () => {
    try {
      const response = await axios.get('/todo-app/api/tasks');
      setTasks(response.data);
    } catch (error) {
      console.error('Chyba při načítání úkolů:', error);
    }
  };

  const toggleTask = async (id: number, completed: boolean) => {
    try {
      await axios.put(`/todo-app/api/tasks/${id}`, { completed: !completed });
      fetchTasks();
    } catch (error) {
      console.error('Chyba při aktualizaci úkolu:', error);
    }
  };

  const deleteTask = async (id: number) => {
    try {
      await axios.delete(`/todo-app/api/tasks/${id}`);
      fetchTasks();
    } catch (error) {
      console.error('Chyba při mazání úkolu:', error);
    }
  };

  return (
    <div className="w-full max-w-md">
      {tasks.map((task) => (
        <div key={task.id} className="flex items-center justify-between p-2 bg-white rounded mb-2 shadow">
          <div className="flex items-center">
            <input
              type="checkbox"
              checked={task.completed}
              onChange={() => toggleTask(task.id, task.completed)}
              className="mr-2"
            />
            <span className={task.completed ? 'line-through' : ''}>{task.title}</span>
          </div>
          <button
            onClick={() => deleteTask(task.id)}
            className="text-red-500 hover:text-red-700"
          >
            Smazat
          </button>
        </div>
      ))}
    </div>
  );
};

export default TaskList;