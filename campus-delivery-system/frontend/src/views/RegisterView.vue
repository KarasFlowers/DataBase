<template>
  <div class="register-container">
    <div class="register-card">
      <h2 class="text-center mb-4">创建新账户</h2>
      <form @submit.prevent="handleRegister">
        <div v-if="error" class="alert alert-danger">{{ error }}</div>
        
        <!-- Role Selection -->
        <div class="mb-3">
            <label class="form-label">选择您的角色:</label>
            <div class="d-flex justify-content-around">
                <div class="form-check">
                    <input class="form-check-input" type="radio" id="roleUser" value="user" v-model="role">
                    <label class="form-check-label" for="roleUser">我是顾客</label>
                </div>
                <div class="form-check">
                    <input class="form-check-input" type="radio" id="roleMerchant" value="merchant" v-model="role">
                    <label class="form-check-label" for="roleMerchant">我是商家</label>
                </div>
                <div class="form-check">
                    <input class="form-check-input" type="radio" id="roleRider" value="rider" v-model="role">
                    <label class="form-check-label" for="roleRider">我是骑手</label>
                </div>
            </div>
        </div>

        <hr>

        <!-- Common Fields -->
        <div class="mb-3">
          <label for="username" class="form-label">用户名</label>
          <input type="text" class="form-control" id="username" v-model="username" required>
        </div>
        <div class="mb-3">
          <label for="password" class="form-label">密码</label>
          <input type="password" class="form-control" id="password" v-model="password" required>
        </div>
        <div class="mb-3">
          <label for="phone" class="form-label">手机号</label>
          <input type="text" class="form-control" id="phone" v-model="phoneNumber" required>
        </div>

        <!-- Merchant-specific Fields -->
        <div v-if="role === 'merchant'">
            <hr>
            <h5 class="mb-3">商家信息</h5>
            <div class="mb-3">
                <label for="merchantName" class="form-label">店铺名称</label>
                <input type="text" class="form-control" id="merchantName" v-model="merchantName" required>
            </div>
            <div class="mb-3">
                <label for="merchantAddress" class="form-label">店铺地址</label>
                <input type="text" class="form-control" id="merchantAddress" v-model="merchantAddress" required>
            </div>
        </div>

        <!-- Rider-specific Fields -->
        <div v-if="role === 'rider'">
            <hr>
            <h5 class="mb-3">骑手信息</h5>
             <div class="mb-3">
                <label for="riderName" class="form-label">真实姓名</label>
                <input type="text" class="form-control" id="riderName" v-model="riderName" required>
            </div>
        </div>

        <div class="d-grid mt-4">
          <button type="submit" class="btn btn-primary" :disabled="loading">
            <span v-if="loading" class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
            {{ loading ? '注册中...' : '注册' }}
          </button>
        </div>
        <div class="text-center mt-3">
            <router-link to="/login">已有账户？返回登录</router-link>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

const router = useRouter();

// Form fields
const role = ref('user');
const username = ref('');
const password = ref('');
const phoneNumber = ref('');
const merchantName = ref('');
const merchantAddress = ref('');
const riderName = ref('');

const error = ref(null);
const loading = ref(false);

const handleRegister = async () => {
  loading.value = true;
  error.value = null;

  const payload = {
      username: username.value,
      password: password.value,
      phoneNumber: phoneNumber.value,
      role: role.value,
  };

  if (role.value === 'merchant') {
      payload.merchantName = merchantName.value;
      payload.merchantAddress = merchantAddress.value;
  } else if (role.value === 'rider') {
      payload.riderName = riderName.value;
  }

  try {
    await axios.post('/api/auth/register', payload);
    alert('注册成功！现在您可以登录了。');
    router.push('/login');
  } catch (err) {
    if (err.response && err.response.data) {
        error.value = err.response.data;
    } else {
        error.value = '注册失败，请稍后重试。';
    }
    console.error('Registration failed:', err);
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.register-container {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem 0;
  min-height: 100vh;
}
.register-card {
  width: 100%;
  max-width: 500px;
  padding: 2rem;
  border: 1px solid #ddd;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
}
</style>
