import React, { useState } from 'react';
import axios from 'axios';

function LoginComponent() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [greeting, setGreeting] = useState('Hello');

    const handleLogin = async () => {
        try {
            const response = await axios.get(`/api/greeting/${username}/${password}`);
            setGreeting(response.data);
        } catch (error) {
            console.error("There was an error!", error);
        }
    };

    return (
        <div>
            <h1>{greeting}</h1>
            <input type="text" placeholder="Username" onChange={(e) => setUsername(e.target.value)} />
            <input type="password" placeholder="Password" onChange={(e) => setPassword(e.target.value)} />
            <button onClick={handleLogin}>Login</button>
        </div>
    );
}

export default LoginComponent;
