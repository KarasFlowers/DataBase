<template>
  <div>
    <h1>添加新骑手</h1>
    <form @submit.prevent="addRider">
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
      <button type="submit" class="btn btn-primary">确定</button>
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
const rider = ref({
  name: '',
  phoneNumber: '',
  status: 'offline'
});
const error = ref(null);

onMounted(() => {
  if (userStore.state.userRole !== 'admin') {
    router.push('/'); // Redirect if not admin
  }
});

const addRider = async () => {
  try {
    await axios.post('/api/riders', rider.value);
    router.push('/riders');
  } catch (err) {
    error.value = '添加骑手失败，请检查数据后重试。';
    console.error('Error adding rider:', err);
  }
};
</script>
