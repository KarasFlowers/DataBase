<template>
  <div>
    <h1>编辑地址</h1>
    <div v-if="loading" class="alert alert-info">正在加载地址...</div>
    <form v-if="address" @submit.prevent="updateAddress">
      <div class="mb-3">
        <label for="addressDetails" class="form-label">详细地址</label>
        <textarea class="form-control" id="addressDetails" v-model="address.addressDetails" rows="3" required></textarea>
      </div>
      <div class="mb-3 form-check">
        <input type="checkbox" class="form-check-input" id="isDefault" v-model="address.default">
        <label class="form-check-label" for="isDefault">设为默认地址</label>
      </div>
      <button type="submit" class="btn btn-primary">更新</button>
      <router-link to="/addresses" class="btn btn-secondary ms-2">取消</router-link>
    </form>
    <div v-if="error" class="alert alert-danger mt-3">{{ error }}</div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { useRouter, useRoute } from 'vue-router';
import userStore from '../stores/userStore';

const router = useRouter();
const route = useRoute();
const address = ref(null);
const loading = ref(true);
const error = ref(null);
const addressId = route.params.id;

onMounted(async () => {
  if (userStore.state.userRole !== 'user') {
    router.push('/');
    return;
  }
  try {
    const response = await axios.get(`/api/addresses/${addressId}`);
    address.value = response.data;
  } catch (err) {
    error.value = '加载地址数据失败。';
    console.error('Error fetching address:', err);
  } finally {
    loading.value = false;
  }
});

const updateAddress = async () => {
  if (!address.value) return;
  try {
    await axios.put('/api/addresses', address.value);
    router.push('/addresses');
  } catch (err) {
    error.value = '更新地址失败。';
    console.error('Error updating address:', err);
  }
};
</script>
