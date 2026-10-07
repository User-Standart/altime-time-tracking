<script setup lang="ts">
definePageMeta({
  _layout: 'login',
  get layout() {
    return this._layout
  },
  set layout(value) {
    this._layout = value
  },
})

import { ref, watch } from 'vue'
import { navigateTo } from '#app'

const email = ref('')
const password = ref('')
const errorMessage = ref('')
const isLoading = ref(false)

const isLightTheme = ref(localStorage.getItem('theme') === 'light')

watch(isLightTheme, (newVal) => {
  localStorage.setItem('theme', newVal ? 'light' : 'dark')
})

function toggleTheme() {
  isLightTheme.value = !isLightTheme.value
}

async function handleLogin(event: Event) {
  event.preventDefault()
  isLoading.value = true

  setTimeout(async () => {
    if (email.value === 'altimeadm@gmail.com' && password.value === 'admin') {
      localStorage.setItem('authToken', 'token-valido')
      await navigateTo('/')
    } else {
      errorMessage.value = 'Email ou senha inválidos'
    }
    isLoading.value = false
  }, 1000)
}
</script>

<template>
  <div :class="['login-wrapper', { 'light-theme': isLightTheme }]">
    <button
      @click="toggleTheme"
      class="theme-toggle-button"
      aria-label="Alternar tema claro/escuro"
      title="Alternar tema claro/escuro"
    >
      ◐
    </button>

    <div class="background-logo"></div>
    <div class="login-container">
      <h1 class="title">Login</h1>
      <form @submit="handleLogin" class="form">
        <div class="form-group">
          <label for="email" class="label">Email:</label>
          <input v-model="email" id="email" type="email" required class="input" />
        </div>
        <div class="form-group">
          <label for="password" class="label">Senha:</label>
          <input v-model="password" id="password" type="password" required class="input" />
        </div>
        <button :disabled="isLoading" type="submit" class="button">
          {{ isLoading ? 'Entrando...' : 'Entrar' }}
        </button>
      </form>
      <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
    </div>
  </div>
</template>

<style scoped>
.login-wrapper {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100vh;
  background: #0f172a;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
  transition: background 0.3s ease;
}

.login-wrapper.light-theme {
  background: #f0f0f3;
  color: #222;
}

.theme-toggle-button {
  position: fixed;
  top: 10px;
  right: 10px;
  background-color: #3b82f6;
  color: white;
  border: none;
  padding: 8px 12px;
  border-radius: 6px;
  font-size: 20px;
  cursor: pointer;
  transition: background-color 0.3s;
  z-index: 10;
}

.theme-toggle-button:hover {
  background-color: #2563eb;
}

.background-logo {
  position: absolute;
  background-image: url('/logo.svg');
  background-repeat: no-repeat;
  background-position: center;
  background-size: 40%;
  opacity: 0.15;
  width: 100%;
  height: 100%;
  z-index: 0;
}

.login-container {
  z-index: 1;
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  border-radius: 10px;
  padding: 40px;
  width: 100%;
  max-width: 380px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.6);
  text-align: center;
  transition: background-color 0.3s, border 0.3s, color 0.3s;
  border: none;
  color: white;
}

.login-wrapper.light-theme .login-container {
  background: rgba(240, 240, 240, 0.3);
  backdrop-filter: blur(10px);
  color: #222;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 2px solid #444;
}

.title {
  font-size: 1.8rem;
  margin-bottom: 20px;
  font-weight: bold;
  color: #ffffff;
  transition: color 0.3s;
}

.login-wrapper.light-theme .title {
  color: #111;
}

.form {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.form-group {
  text-align: left;
}

.label {
  display: block;
  margin-bottom: 5px;
  font-weight: 600;
  color: #cbd5e1;
  transition: color 0.3s;
}

.login-wrapper.light-theme .label {
  color: #555;
}

.input {
  width: 100%;
  padding: 10px;
  border: none;
  border-radius: 5px;
  background-color: #1e293b;
  color: white;
  font-size: 14px;
  transition: background-color 0.3s, color 0.3s, border 0.3s;
}

.login-wrapper.light-theme .input {
  background-color: white;
  border: 1.5px solid black;
  color: #222;
}

.input:focus {
  outline: 2px solid #3b82f6;
}

.login-wrapper.light-theme .input:focus {
  outline: 2px solid #3b82f6;
  border-color: #3b82f6;
}

.button {
  width: 100%;
  padding: 12px;
  background-color: #3b82f6;
  color: white;
  font-weight: bold;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.button:hover {
  background-color: #2563eb;
}

.button:disabled {
  background-color: #93c5fd;
  cursor: not-allowed;
}

.error-message {
  color: #f87171;
  margin-top: 15px;
  font-weight: 600;
}
</style>
