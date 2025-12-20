<template>
  <div class="user-list">
    <div class="d-flex justify-content-between align-items-center mb-3">
      <h1>用户列表</h1>
      <router-link v-if="userStore.state.userRole === 'admin'" to="/users/add" class="btn btn-primary">添加用户</router-link>
    </div>

    <div v-if="loading" class="alert alert-info">正在加载...</div>
    <div v-if="error" class="alert alert-danger">{{ error }}</div>

    <table v-if="users.length > 0" class="table table-striped table-hover">
      <thead>
        <tr>
          <th>ID</th>
          <th>用户名</th>
          <th>电话号码</th>
          <th>注册日期</th>
          <th v-if="userStore.state.userRole === 'admin'">操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="user in users" :key="user.userId">
          <td>{{ user.userId }}</td>
          <td>{{ user.username }}</td>
          <td>{{ user.phoneNumber }}</td>
          <td>{{ new Date(user.registrationDate).toLocaleString() }}</td>
          <td v-if="userStore.state.userRole === 'admin'">
            <div class="btn-group" role="group">
              <router-link :to="{ name: 'edit-user', params: { id: user.userId } }" class="btn btn-secondary btn-sm">编辑</router-link>
              <button @click="deleteUser(user.userId)" class="btn btn-danger btn-sm">删除</button>
            </div>
          </td>
        </tr>
      </tbody>
    </table>

    <div v-if="!loading && users.length === 0 && !error" class="alert alert-warning">
      未找到任何用户。
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import userStore from '../stores/userStore';
import { useRouter } from 'vue-router';

const users = ref([]);
const loading = ref(true);
const error = ref(null);
const router = useRouter();

const fetchUsers = async () => {
  loading.value = true;
  try {
    const response = await axios.get('/api/users');
    users.value = response.data.filter(user => user.role === 'user');
  } catch (err) {
    error.value = '获取用户列表失败。';
    console.error('Error fetching users:', err);
  } finally {
    loading.value = false;
  }
};

onMounted(async () => {
  if (userStore.state.userRole !== 'admin') {
    router.push('/'); // Redirect to home if not admin
    return;
  }
  await fetchUsers();
});

const deleteUser = async (userId) => {
  if (confirm('您确定要删除该用户吗？')) {
    try {
      await axios.delete(`/api/users/${userId}`);
      await fetchUsers(); // Refresh the list
    } catch (err) {
      alert('删除用户失败。');
      console.error('Error deleting user:', err);
    }
  }
};
</script>
