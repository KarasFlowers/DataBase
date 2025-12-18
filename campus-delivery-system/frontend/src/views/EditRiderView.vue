<template>
  <div>
    <h1>编辑骑手</h1>
    <div v-if="loading" class="alert alert-info">正在加载骑手数据...</div>
    <form v-if="rider" @submit.prevent="updateRider">
      <div class="mb-3">
        <label for="name" class="form-label">骑手姓名</label>
        <input type="text" class="form-control" id="name" v-model="rider.name" required>
      </div>
      <div class="mb-3">
        <label for="phone" class="form-label">电话号码</label>
        <input type="text" class="form-control" id="phone" v-model="rider.phoneNumber" required>
      </div>
       <div class="mb-3">
        <label for="status" class="form-label">状态</label>
        <select class="form-select" id="status" v-model="rider.status">
            <option value="offline">离线</option>
            <option value="available">空闲</option>
            <option value="delivering">配送中</option>
        </select>
      </div>
      <button type="submit" class="btn btn-primary">更新</button>
      <router-link to="/riders" class="btn btn-secondary ms-2">取消</router-link>
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
const rider = ref(null);
const loading = ref(true);
const error = ref(null);
const riderId = route.params.id;

onMounted(async () => {
    if (userStore.state.userRole !== 'admin') {
        router.push('/'); // Redirect if not admin
        return;
    }
  try {
    const response = await axios.get(`/api/riders/${riderId}`);
    rider.value = response.data;
  } catch (err) {
    error.value = '加载骑手数据失败。';
    console.error('Error fetching rider:', err);
  } finally {
    loading.value = false;
  }
});

const updateRider = async () => {
  if (!rider.value) return;
  try {
    await axios.put('/api/riders', rider.value);
    router.push('/riders');
  } catch (err) {
    error.value = '更新骑手失败。';
    console.error('Error updating rider:', err);
  }
};
</script>
