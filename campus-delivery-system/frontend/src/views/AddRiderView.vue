<template>
  <div>
    <h1>添加新骑手 (并创建登录账户)</h1>
    <form @submit.prevent="addRiderAndUser">
      <h5 class="mt-4">骑手信息</h5>
      <hr>
      <div class="mb-3">
        <label for="riderName" class="form-label">骑手姓名</label>
        <input type="text" class="form-control" id="riderName" v-model="formData.riderName" required>
      </div>
      <div class="mb-3">
        <label for="phone" class="form-label">电话号码</label>
        <input type="text" class="form-control" id="phone" v-model="formData.phoneNumber" required>
      </div>

      <h5 class="mt-4">骑手登录账户</h5>
      <hr>
       <div class="mb-3">
        <label for="username" class="form-label">登录用户名</label>
        <input type="text" class="form-control" id="username" v-model="formData.username" required>
      </div>
      <div class="mb-3">
        <label for="password" class="form-label">登录密码</label>
        <input type="password" class="form-control" id="password" v-model="formData.password" required>
      </div>

      <button type="submit" class="btn btn-primary">确认创建</button>
      <router-link to="/riders" class="btn btn-secondary ms-2">取消</router-link>
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
const formData = ref({
  // Rider Info
  riderName: '',
  // Common Info
  phoneNumber: '',
  // User Info
  username: '',
  password: '',
  // Role
  role: 'rider'
});
const error = ref(null);

onMounted(() => {
  if (userStore.state.userRole !== 'admin') {
    router.push('/'); // Redirect if not admin
  }
});

const addRiderAndUser = async () => {
  try {
    await axios.post('/api/auth/register', formData.value);
    router.push('/riders');
  } catch (err) {
    if (err.response && err.response.data) {
        error.value = `添加失败: ${err.response.data}`;
    } else {
        error.value = '添加骑手失败，请检查数据后重试。';
    }
    console.error('Error adding rider:', err);
  }
};
</script>
