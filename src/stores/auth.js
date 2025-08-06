import axios from 'axios';
import { defineStore } from 'pinia';
import { ref } from 'vue';

const initState = {
  token:
    'yJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbnRlaHl1bjQ4ODAiLCJpYXQiOjE3NTQ0NjkzNjgsImV4cCI6MTc1NDQ3Mjk2OH0.NMQ10HNM6OQR8C1RlP5gHsRL1ZofRYcb5FBXEPQ56Pk', // 접근 토큰(JWT)
  user: {},
};

export const useAuthStore = defineStore('auth', () => {
  const state = ref({ ...initState });

  const login = async (member) => {
    // api호출
    // const { data } = await axios.post('/api/auth/login', member);
    // state.value = { ...data };
    // localStorage.setItem('auth', JSON.stringify(state.value));
  };

  const logout = () => {
    localStorage.clear();
    state.value = { ...initState };
  };

  const getToken = () => state.value.token;

  const setToken = (token) => {
    state.value.token = token;
    localStorage.setItem('auth', JSON.stringify(state.value));
  };

  const load = () => {
    const auth = localStorage.getItem('auth');
    if (auth != null) {
      state.value = JSON.parse(auth);
      console.log(state.value);
    }
  };

  load();

  return {
    state,
    login,
    logout,
    getToken,
    setToken,
  };
});
