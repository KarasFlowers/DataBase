<template>
  <div class="rider-list">
    <div class="d-flex justify-content-between align-items-center mb-3">
      <h1>骑手列表</h1>
      <router-link v-if="userStore.state.userRole === 'admin'" to="/riders/add" class="btn btn-primary">添加骑手</router-link>
    </div>

    <div v-if="loading" class="alert alert-info">正在加载...</div>
    <div v-if="error" class="alert alert-danger">{{ error }}</div>

    <table v-if="riders.length > 0" class="table table-striped table-hover">
      <thead>
        <tr>
          <th>ID</th>
          <th>姓名</th>
          <th>电话号码</th>
          <th>状态</th>
          <th v-if="userStore.state.userRole === 'admin'">操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="rider in riders" :key="rider.riderId">
          <td>{{ rider.riderId }}</td>
          <td>{{ rider.name }}</td>
          <td>{{ rider.phoneNumber }}</td>
          <td><span class="badge" :class="getStatusClass(rider.status)">{{ translateStatus(rider.status) }}</span></td>
          <td v-if="userStore.state.userRole === 'admin'">
            <div class="btn-group" role="group">
              <router-link :to="{ name: 'edit-rider', params: { id: rider.riderId } }" class="btn btn-secondary btn-sm">编辑</router-link>
              <button @click="deleteRider(rider.riderId)" class="btn btn-danger btn-sm">删除</button>
            </div>
          </td>
        </tr>
      </tbody>
    </table>

    <div v-if="!loading && riders.length === 0 && !error" class="alert alert-warning">
      未找到任何骑手。
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import userStore from '../stores/userStore';
import { useRouter } from 'vue-router';

const riders = ref([]);
const loading = ref(true);
const error = ref(null);
const router = useRouter();

const fetchRiders = async () => {
  loading.value = true;
  try {
    const response = await axios.get('/api/riders');
    riders.value = response.data;
  } catch (err) {
    error.value = '获取骑手列表失败。';
    console.error('Error fetching riders:', err);
  } finally {
    loading.value = false;
  }
};

onMounted(async () => {
  if (userStore.state.userRole !== 'admin') {
    router.push('/'); // Redirect if not admin
    return;
  }
  await fetchRiders();
});

const deleteRider = async (riderId) => {
  if (confirm('您确定要删除该骑手吗？')) {
    try {
      await axios.delete(`/api/riders/${riderId}`);
      await fetchRiders(); // Refresh the list
    } catch (err) {
      alert('删除骑手失败。');
      console.error('Error deleting rider:', err);
    }
  }
};

const getStatusClass = (status) => {
  switch (status) {
    case 'available': return 'bg-success';
    case 'delivering': return 'bg-warning text-dark';
    case 'offline': return 'bg-secondary';
    default: return 'bg-light text-dark';
  }
};

const translateStatus = (status) => {
    const map = {
        available: '空闲',
        delivering: '配送中',
        offline: '离线'
    };
    return map[status] || status;
}
</script>
