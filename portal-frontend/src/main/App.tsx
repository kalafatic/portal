import React from 'react';
import { createRoot } from 'react-dom/client';
import TaskList from './components/TaskList';
import TaskForm from './components/TaskForm';

const App: React.FC = () => {
  return (
    <div className="min-h-screen bg-gray-100 flex flex-col items-center p-4">
      <h1 className="text-3xl font-bold mb-6">Správa úkolů</h1>
      <TaskForm />
      <TaskList />
    </div>
  );
};

const root = createRoot(document.getElementById('root')!);
root.render(<App />);