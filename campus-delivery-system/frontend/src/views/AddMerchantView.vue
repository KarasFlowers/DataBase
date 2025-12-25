<template>
  <div>
    <h1>添加新商家 (并创建登录账户)</h1>
    <form @submit.prevent="addMerchantAndUser">
      <h5 class="mt-4">店铺信息</h5>
      <hr>
      <div class="mb-3">
        <label for="merchantName" class="form-label">商家名称</label>
        <input type="text" class="form-control" id="merchantName" v-model="formData.merchantName" required>
      </div>
      <div class="mb-3">
        <label for="address" class="form-label">地址</label>
        <input type="text" class="form-control" id="address" v-model="formData.merchantAddress" required>
      </div>
      <div class="mb-3">
        <label for="phone" class="form-label">电话号码</label>
        <input type="text" class="form-control" id="phone" v-model="formData.phoneNumber" required>
      </div>

      <h5 class="mt-4">商家登录账户</h5>
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
      <router-link to="/" class="btn btn-secondary ms-2">取消</router-link>
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
  // Merchant Info
  merchantName: '',
  merchantAddress: '',
  // Common Info
  phoneNumber: '',
  // User Info
  username: '',
  password: '',
  // Role
  role: 'merchant'
});
const error = ref(null);

onMounted(() => {
  if (userStore.state.userRole !== 'admin') {
    router.push('/'); // Redirect to home if not admin
  }
});

const addMerchantAndUser = async () => {
  try {
    await axios.post('/api/auth/register', formData.value);
    router.push('/'); // Redirect to merchant list on success
  } catch (err) {
    if (err.response && err.response.data) {
        error.value = `添加失败: ${err.response.data}`;
    } else {
        error.value = '添加商家失败，请检查数据后重试。';
    }
    console.error('Error adding merchant:', err);
  }
};
</script>

<style scoped>
</style>
