import React, { useState } from 'react';
import axios from 'axios';

const TaskForm: React.FC = () => {
  const [title, setTitle] = useState('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!title.trim()) return;

    try {
      await axios.post('/todo-app/api/tasks', { title, completed: false });
      setTitle('');
      window.location.reload(); // Pro jednoduchost, v produkci použít lepší refresh mechanismus
    } catch (error) {
      console.error('Chyba při vytváření úkolu:', error);
    }
  };

  return (
    <div className="w-full max-w-md mb-6">
      <form onSubmit={handleSubmit} className="flex gap-2">
        <input
          type="text"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          placeholder="Zadejte název úkolu"
          className="flex-1 p-2 border rounded"
        />
        <button
          type="submit"
          className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
        >
          Přidat
        </button>
      </form>
    </div>
  );
};

export default TaskForm;