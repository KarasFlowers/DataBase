<template>
  <div class="rider-list">
    <div class="d-flex justify-content-between align-items-center mb-3">
      <h1>骑手列表</h1>
      <router-link v-if="userStore.state.userRole === 'admin'" to="/riders/add" class="btn btn-primary">添加骑手</router-link>
    </div>

    <!-- Search Input -->
    <div class="mb-3">
        <input type="text" class="form-control" placeholder="按姓名或手机号搜索..." v-model="searchQuery">
    </div>

    <div v-if="loading" class="alert alert-info">正在加载...</div>
    <div v-if="error" class="alert alert-danger">{{ error }}</div>

    <table v-if="filteredRiders.length > 0" class="table table-striped table-hover">
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
        <tr v-for="rider in filteredRiders" :key="rider.riderId">
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

    <div v-if="!loading && filteredRiders.length === 0 && !error" class="alert alert-warning">
      <span v-if="searchQuery">未找到匹配的骑手。</span>
      <span v-else>未找到任何骑手。</span>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import axios from 'axios';
import userStore from '../stores/userStore';
import { useRouter } from 'vue-router';

const riders = ref([]);
const loading = ref(true);
const error = ref(null);
const router = useRouter();
const searchQuery = ref('');

const filteredRiders = computed(() => {
    if (!searchQuery.value) {
        return riders.value;
    }
    const query = searchQuery.value.toLowerCase();
    return riders.value.filter(rider => 
        rider.name.toLowerCase().includes(query) ||
        rider.phoneNumber.includes(query)
    );
});

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
