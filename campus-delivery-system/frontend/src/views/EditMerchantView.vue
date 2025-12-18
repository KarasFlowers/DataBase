<template>
  <div>
    <h1>编辑商家</h1>
    <div v-if="loading" class="alert alert-info">正在加载商家数据...</div>
    <form v-if="merchant" @submit.prevent="updateMerchant">
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
      <button type="submit" class="btn btn-primary">更新</button>
      <router-link to="/" class="btn btn-secondary ms-2">取消</router-link>
    </form>
    <div v-if="error" class="alert alert-danger mt-3">{{ error }}</div>
  </div>
</template>

<script setup>
import { ref, onMounted, defineProps } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';
import userStore from '../stores/userStore';

const props = defineProps({
  id: {
    type: String,
    required: true
  }
});

const router = useRouter();
const merchant = ref(null);
const loading = ref(true);
const error = ref(null);

onMounted(async () => {
  if (userStore.state.userRole !== 'admin') {
    router.push('/'); // Redirect to home if not admin
    return;
  }
  try {
    const response = await axios.get(`/api/merchants/${props.id}`);
    merchant.value = response.data;
  } catch (err) {
    error.value = '加载商家数据失败。';
    console.error('Error fetching merchant:', err);
  } finally {
    loading.value = false;
  }
});

const updateMerchant = async () => {
  if (!merchant.value) return;
  try {
    await axios.put('/api/merchants', merchant.value);
    router.push('/');
  } catch (err) {
    error.value = '更新商家失败。';
    console.error('Error updating merchant:', err);
  }
};
</script>
