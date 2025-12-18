<template>
  <div class="merchant-list">
    <div class="d-flex justify-content-between align-items-center mb-3">
      <h1>商家列表</h1>
      <router-link v-if="userStore.state.userRole === 'admin'" to="/merchants/add" class="btn btn-primary">添加商家</router-link>
    </div>

    <div v-if="loading" class="alert alert-info">正在加载...</div>
    <div v-if="error" class="alert alert-danger">{{ error }}</div>

    <div v-if="merchants.length > 0" class="list-group">
      <div v-for="merchant in merchants" :key="merchant.merchantId" class="list-group-item list-group-item-action d-flex justify-content-between align-items-center">
        <router-link :to="{ name: 'dishes', params: { id: merchant.merchantId } }" class="flex-grow-1 text-decoration-none text-dark">
          <h5>{{ merchant.name }}</h5>
          <p class="mb-0">{{ merchant.address }} - 评分: {{ merchant.rating }}</p>
        </router-link>
        <div v-if="userStore.state.userRole === 'admin'" class="btn-group" role="group">
          <router-link :to="{ name: 'edit-merchant', params: { id: merchant.merchantId } }" class="btn btn-secondary btn-sm">编辑</router-link>
          <button @click.prevent="deleteMerchant(merchant.merchantId)" class="btn btn-danger btn-sm">删除</button>
        </div>
      </div>
    </div>

    <div v-if="!loading && merchants.length === 0 && !error" class="alert alert-warning">
      未找到任何商家。
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import userStore from '../stores/userStore';

const merchants = ref([]);
const loading = ref(true);
const error = ref(null);

const fetchMerchants = async () => {
  try {
    const response = await axios.get('/api/merchants');
    merchants.value = response.data;
  } catch (err) {
    error.value = '加载商家列表失败，请确认后端服务是否运行。';
    console.error('Error fetching merchants:', err);
  }
};

onMounted(async () => {
  loading.value = true;
  await fetchMerchants();
  loading.value = false;
});

const deleteMerchant = async (merchantId) => {
  if (confirm('您确定要删除该商家吗？其下所有菜品也将被一并删除。')) {
    try {
      await axios.delete(`/api/merchants/${merchantId}`);
      // Refresh the list
      loading.value = true;
      await fetchMerchants();
      loading.value = false;
    } catch (err) {
      alert('删除商家失败。');
      console.error('Error deleting merchant:', err);
    }
  }
};
</script>

<style scoped>
/* Add any specific styles for the merchant list here */
</style>