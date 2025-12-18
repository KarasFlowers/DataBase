<template>
  <div>
    <h1>添加新商家</h1>
    <form @submit.prevent="addMerchant">
      <div class="mb-3">
        <label for="name" class="form-label">商家名称</label>
        <input type="text" class="form-control" id="name" v-model="merchant.name" required>
      </div>
      <div class="mb-3">
        <label for="address" class="form-label">地址</label>
        <input type="text" class="form-control" id="address" v-model="merchant.address" required>
      </div>
      <div class="mb-3">
        <label for="phone" class="form-label">电话号码</label>
        <input type="text" class="form-control" id="phone" v-model="merchant.phoneNumber">
      </div>
      <button type="submit" class="btn btn-primary">确认</button>
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
const merchant = ref({
  name: '',
  address: '',
  phoneNumber: '',
  rating: 0.0 // Default rating
});
const error = ref(null);

onMounted(() => {
  if (userStore.state.userRole !== 'admin') {
    router.push('/'); // Redirect to home if not admin
  }
});

const addMerchant = async () => {
  try {
    await axios.post('/api/merchants', merchant.value);
    router.push('/'); // Redirect to merchant list on success
  } catch (err) {
    error.value = '添加商家失败，请检查数据后重试。';
    console.error('Error adding merchant:', err);
  }
};
</script>

<style scoped>
</style>
