<template>
  <div class="merchant-dashboard">
    <div v-if="loading" class="alert alert-info">高雅人士加载数据面板中...</div>
    <div v-else-if="error" class="alert alert-danger">{{ error }}</div>

    <div v-else-if="stats">
      <h1>{{ stats.merchantName }}-数据统计</h1>
      
      <!-- Stats Cards -->
      <div class="row">
        <div class="col-md-6 col-lg-3 mb-3">
          <div class="card">
            <div class="card-body">
              <h5 class="card-title">总订单数</h5>
              <p class="card-text fs-4">{{ stats.totalOrders }}</p>
            </div>
          </div>
        </div>
        <div class="col-md-6 col-lg-3 mb-3">
          <div class="card">
            <div class="card-body">
              <h5 class="card-title">总营业额</h5>
              <p class="card-text fs-4">${{ stats.totalRevenue }}</p>
            </div>
          </div>
        </div>
        <div class="col-md-6 col-lg-3 mb-3">
          <div class="card">
            <div class="card-body">
              <h5 class="card-title">今日订单数</h5>
              <p class="card-text fs-4">{{ stats.ordersToday }}</p>
            </div>
          </div>
        </div>
        <div class="col-md-6 col-lg-3 mb-3">
          <div class="card">
            <div class="card-body">
              <h5 class="card-title">今日营业额</h5>
              <p class="card-text fs-4">${{ stats.revenueToday }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Top Selling Dishes -->
      <div class="top-dishes mt-4">
        <h2>最受欢迎的菜品（前五道）</h2>
        <div v-if="stats.topSellingDishes && stats.topSellingDishes.length > 0">
          <table class="table table-striped">
            <thead>
              <tr>
                <th>菜品</th>
                <th>已售数量</th>
                <th>总营收</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="dish in stats.topSellingDishes" :key="dish.dishName">
                <td>{{ dish.dishName }}</td>
                <td>{{ dish.totalQuantitySold }}</td>
                <td>${{ dish.totalDishRevenue.toFixed(2) }}</td>
              </tr>
            </tbody>
          </table>
        </div>
        <div v-else class="alert alert-secondary">
          您的店铺还没有销售记录。
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import userStore from '../stores/userStore';

const router = useRouter();
const loading = ref(true);
const error = ref(null);
const stats = ref(null);

const fetchDashboardData = async (merchantId) => {
  if (!merchantId) return;
  loading.value = true;
  error.value = null;
  try {
    const response = await axios.get(`/api/analytics/merchant/${merchantId}/dashboard`);
    stats.value = response.data;
  } catch (err) {
    error.value = 'Failed to load dashboard statistics.';
    console.error(err);
    stats.value = null; // Clear old data on error
  } finally {
    loading.value = false;
  }
};

// Initial data fetch
onMounted(() => {
  const { userRole, merchantId } = userStore.state;
  if (userRole === 'merchant' && merchantId) {
    fetchDashboardData(merchantId);
  } else if (userRole !== 'admin') { 
    // Admins might view this page in the future with a prop, so don't redirect them.
    // For others, redirect.
    router.push('/');
  }
});

// Watch for changes in the merchantId from the store
watch(() => userStore.state.merchantId, (newId, oldId) => {
  if (newId && newId !== oldId && userStore.state.userRole === 'merchant') {
    fetchDashboardData(newId);
  }
});

</script>
