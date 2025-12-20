<template>
    <div class="login-container">
      <div class="login-card">
        <h2 class="text-center mb-4">用户登录</h2>
        <form @submit.prevent="handleLogin">
          <div v-if="error" class="alert alert-danger">{{ error }}</div>
          <div class="mb-3">
            <label for="username" class="form-label">用户名</label>
            <input type="text" class="form-control" id="username" v-model="username" required>
          </div>
          <div class="mb-3">
            <label for="password" class="form-label">密码</label>
            <input type="password" class="form-control" id="password" v-model="password" required>
          </div>
          <div class="d-grid">
            <button type="submit" class="btn btn-primary" :disabled="loading">
              <span v-if="loading" class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
              {{ loading ? '登录中...' : '登录' }}
            </button>
          </div>
          <div class="text-center mt-3">
            <router-link to="/register">还没有账户？立即注册</router-link>
          </div>
        </form>
      </div>
    </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import userStore from '../stores/userStore';

const router = useRouter();
const username = ref('');
const password = ref('');
const error = ref(null);
const loading = ref(false);

const handleLogin = async () => {
  loading.value = true;
  error.value = null;
  try {
    // We will create the login action in userStore next
    await userStore.login(username.value, password.value);
    
    // Redirect based on role
    const role = userStore.state.userRole;
    if (role === 'admin' || role === 'user') {
      router.push('/');
    } else if (role === 'merchant') {
      router.push({ name: 'dishes', params: { id: userStore.state.merchantId } });
    } else if (role === 'rider') {
      router.push('/rider-dashboard');
    } else {
        router.push('/'); // Fallback
    }

  } catch (err) {
    error.value = '登录失败，请检查用户名和密码。';
    console.error('Login failed:', err);
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.login-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 80vh;
}
.login-card {
  width: 100%;
  max-width: 400px;
  padding: 2rem;
  border: 1px solid #ddd;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
}
</style>
