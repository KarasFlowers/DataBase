<template>
  <div class="merchant-list">
    <div class="d-flex justify-content-between align-items-center mb-3">
      <h1>商家列表</h1>
      <router-link v-if="userStore.state.userRole === 'admin'" to="/merchants/add" class="btn btn-primary">添加商家</router-link>
    </div>

    <!-- Search, Sort, and Filter Controls -->
    <div class="d-flex flex-wrap align-items-center mb-3">
        <div class="input-group me-2 mb-2 flex-grow-1" style="min-width: 200px;">
            <input type="text" class="form-control prominent-search-input" placeholder="搜索商家或菜品..." v-model="searchQuery">
        </div>
        <div class="input-group me-2 mb-2" style="max-width: 160px;">
            <span class="input-group-text">￥</span>
            <input type="number" step="1" min="0" class="form-control" v-model.number="minAov" placeholder="最低人均">
        </div>
        <div class="input-group me-2 mb-2" style="max-width: 160px;">
             <span class="input-group-text">~</span>
            <input type="number" step="1" min="0" class="form-control" v-model.number="maxAov" placeholder="最高人均">
        </div>
        <div class="dropdown mb-2">
            <select class="form-select" v-model="sortBy">
                <option value="default">默认排序</option>
                <option value="rating_desc">评分从高到低</option>
                <option value="sales_desc">销量从高到低</option>
                <option value="aov_asc">人均从低到高</option>
                <option value="aov_desc">人均从高到低</option>
            </select>
        </div>
    </div>

    <div v-if="loading" class="alert alert-info">正在加载...</div>
    <div v-if="error" class="alert alert-danger">{{ error }}</div>

    <div v-if="merchants.length > 0" class="list-group">
      <div v-for="merchant in merchants" :key="merchant.merchantId" class="list-group-item list-group-item-action d-flex justify-content-between align-items-center">
        <router-link :to="{ name: 'dishes', params: { id: merchant.merchantId } }" class="flex-grow-1 text-decoration-none text-dark">
          <h5>
            {{ merchant.name }}
            <span class="badge ms-2" :class="merchant.open ? 'bg-success' : 'bg-secondary'">
              {{ merchant.open ? '营业中' : '已打烊' }}
            </span>
          </h5>
          <p class="mb-0">{{ merchant.address }} - 评分: {{ merchant.rating }} - 销量: {{ merchant.salesCount || 0 }} - 平均客单价: ¥{{ merchant.averageOrderPrice || '0.00' }}</p>
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
import { ref, onMounted, watch } from 'vue';
import axios from 'axios';
import userStore from '../stores/userStore';

const merchants = ref([]);
const loading = ref(true);
const error = ref(null);
const searchQuery = ref('');
const sortBy = ref('default');
const minAov = ref(null);
const maxAov = ref(null);
let debounceTimer = null;

const fetchData = async () => {
    // This function will be triggered by watchers.
    // It handles sorting, filtering, and searching.
    loading.value = true;
    error.value = null;

    let params = {
        sortBy: sortBy.value,
        minPrice: minAov.value,
        maxPrice: maxAov.value,
    };
    
    let url = '/api/merchants';

    // If there is a search query, use the search endpoint.
    // Note: The current backend search endpoint may not support combining with filters/sort.
    // We will prioritize search query if it exists.
    if (searchQuery.value.trim() !== '') {
        url = '/api/merchants/search';
        params = { q: searchQuery.value };
    }

    try {
        const response = await axios.get(url, { params });
        merchants.value = response.data;
    } catch (err) {
        error.value = '加载商家列表失败。';
        console.error('Error fetching merchants:', err);
    } finally {
        loading.value = false;
    }
};

onMounted(() => {
  fetchData();
});

// Watch for any change in controls and fetch data with debounce
watch([searchQuery, sortBy, minAov, maxAov], () => {
    clearTimeout(debounceTimer);
    debounceTimer = setTimeout(() => {
        fetchData();
    }, 300); // 300ms delay for all controls
});

const deleteMerchant = async (merchantId) => {
  if (confirm('您确定要删除该商家吗？其下所有菜品也将被一并删除。')) {
    try {
      await axios.delete(`/api/merchants/${merchantId}`);
      await fetchData(); // Re-fetch data
    } catch (err) {
      alert('删除商家失败。');
      console.error('Error deleting merchant:', err);
    }
  }
};
</script>

<style scoped>
input[type="number"]:invalid {
  border-color: red;
  box-shadow: 0 0 0 0.25rem rgba(255, 0, 0, 0.25);
}
.prominent-search-input:focus {
  border-color: #80bdff;
  box-shadow: 0 0 0 0.25rem rgba(0, 123, 255, 0.25);
}
</style>

