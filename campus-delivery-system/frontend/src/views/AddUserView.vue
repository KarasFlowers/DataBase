<template>
  <div>
    <h1>添加新用户</h1>
    <form @submit.prevent="addUser">
      <div class="mb-3">
        <label for="username" class="form-label">用户名</label>
        <input type="text" class="form-control" id="username" v-model="user.username" required>
      </div>
      <div class="mb-3">
        <label for="password" class="form-label">密码</label>
        <input type="password" class="form-control" id="password" v-model="user.passwordHash" required>
        <div class="form-text">注意: 理论上密码要哈希保存，但是这里先直接保存</div>
      </div>
      <div class="mb-3">
        <label for="phone" class="form-label">电话号码</label>
        <input type="text" class="form-control" id="phone" v-model="user.phoneNumber" required>
      </div>
      <button type="submit" class="btn btn-primary">确认</button>
      <router-link to="/users" class="btn btn-secondary ms-2">取消</router-link>
    </form>
    <div v-if="error" class="alert alert-danger mt-3">{{ error }}</div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';
import userStore from '../stores/userStore';

const router = useRouter();
const user = ref({
  username: '',
  passwordHash: '',
  phoneNumber: ''
});
const error = ref(null);

onMounted(() => {
  if (userStore.state.userRole !== 'admin') {
    router.push('/'); // Redirect to home if not admin
  }
});

const addUser = async () => {
  try {
    await axios.post('/api/users', user.value);
    router.push('/users');
  } catch (err) {
    error.value = '添加用户失败，请检查数据后重试。';
    console.error('Error adding user:', err);
  }
};
</script>
