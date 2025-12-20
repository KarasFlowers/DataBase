<template>
  <div class="admin-dashboard">
    <h1>数据统计</h1>

    <div v-if="loading" class="alert alert-info">正在加载统计数据...</div>
    <div v-else-if="error" class="alert alert-danger">{{ error }}</div>

    <div v-else>
      <h2 class="section-title">平台总览</h2>
      <div class="stats-grid">
        <!-- 总用户账号数 -->
        <div class="stat-card">
          <i class="bi bi-people-fill icon-users"></i>
          <div class="stat-info">
            <span class="stat-value">{{ stats.totalUsers }}</span>
            <span class="stat-label">总用户</span>
          </div>
        </div>
        <!-- 管理员账号数 -->
                <div class="stat-card">
                  <i class="bi bi-person-gear icon-admin"></i>
                  <div class="stat-info">
                    <span class="stat-value">{{ stats.totalAdminUsers }}</span>
                    <span class="stat-label">管理员</span>
                  </div>
                </div>
        <!-- 普通顾客数 -->
        <div class="stat-card">
          <i class="bi bi-person-fill icon-customer"></i>
          <div class="stat-info">
            <span class="stat-value">{{ stats.totalCustomerUsers }}</span>
            <span class="stat-label">顾客</span>
          </div>
        </div>
        <!-- 商家账号数 -->
        <div class="stat-card">
          <i class="bi bi-shop icon-merchants"></i>
          <div class="stat-info">
            <span class="stat-value">{{ stats.totalMerchantUsersByRole }}</span>
            <span class="stat-label">商家</span>
          </div>
        </div>
        <!-- 骑手账号数 -->
        <div class="stat-card">
          <i class="bi bi-bicycle icon-riders"></i>
          <div class="stat-info">
            <span class="stat-value">{{ stats.totalRiderUsersByRole }}</span>
            <span class="stat-label">骑手</span>
          </div>
        </div>

        <!-- 总订单数 -->
        <div class="stat-card">
          <i class="bi bi-box-seam icon-orders"></i>
          <div class="stat-info">
            <span class="stat-value">{{ stats.totalOrders }}</span>
            <span class="stat-label">总订单数</span>
          </div>
        </div>
        <!-- 总营业额 -->
        <div class="stat-card">
          <i class="bi bi-cash-stack icon-revenue"></i>
          <div class="stat-info">
            <span class="stat-value">￥{{ stats.totalRevenue }}</span>
            <span class="stat-label">总营业额</span>
          </div>
        </div>
      </div>

      <h2 class="section-title mt-5">今日动态</h2>
      <div class="stats-grid">
        <div class="stat-card">
          <i class="bi bi-calendar-day icon-today"></i>
          <div class="stat-info">
            <span class="stat-value">{{ stats.ordersToday }}</span>
            <span class="stat-label">今日订单</span>
          </div>
        </div>
        <div class="stat-card">
           <i class="bi bi-currency-dollar icon-today-revenue"></i>
          <div class="stat-info">
            <span class="stat-value">￥{{ stats.revenueToday }}</span>
            <span class="stat-label">今日营业额</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import userStore from '../stores/userStore';

const router = useRouter();
const loading = ref(true);
const error = ref(null);
const stats = ref({
  totalUsers: 0,
  totalAdminUsers: 0,
  totalCustomerUsers: 0,
  totalMerchantUsersByRole: 0,
  totalRiderUsersByRole: 0,
  totalMerchants: 0, // Entity count
  totalRiders: 0, // Entity count
  totalOrders: 0,
  totalRevenue: '0.00',
  ordersToday: 0,
  revenueToday: '0.00'
});

onMounted(async () => {
  if (userStore.state.userRole !== 'admin') {
    router.push('/');
    return;
  }
  
  try {
    const response = await axios.get('/api/analytics/admin-dashboard');
    // Ensure that null values from backend are handled gracefully
    stats.value = {
      ...stats.value,
      ...response.data,
      totalRevenue: response.data.totalRevenue || '0.00',
      revenueToday: response.data.revenueToday || '0.00',
    };
  } catch (err) {
    error.value = 'Failed to load dashboard statistics.';
    console.error(err);
  } finally {
    loading.value = false;
  }
});
</script>

<style scoped>
.admin-dashboard h1 {
  margin-bottom: 2rem;
}

.section-title {
    font-size: 1.5rem;
    color: #495057;
    margin-bottom: 1.5rem;
    border-bottom: 1px solid #dee2e6;
    padding-bottom: 0.5rem;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 1.5rem;
}

.stat-card {
  background-color: #fff;
  border-radius: 8px;
  padding: 1.5rem;
  display: flex;
  align-items: center;
  gap: 1.5rem;
  box-shadow: 0 4px 8px rgba(0,0,0,0.05);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.stat-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 6px R2px rgba(0,0,0,0.1);
}

.stat-card i {
  font-size: 3rem;
  padding: 1rem;
  border-radius: 50%;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 2rem;
  font-weight: 700;
  color: #212529;
}

.stat-label {
  font-size: 1rem;
  color: #6c757d;
}

/* Icon Colors */
.icon-users { background-color: #0d6efd; }
.icon-merchants { background-color: #6c757d; }
.icon-riders { background-color: #0dcaf0; }
.icon-orders { background-color: #6610f2; }
.icon-revenue { background-color: #198754; }
.icon-today { background-color: #ffc107; }
.icon-today-revenue { background-color: #fd7e14; }
/* New Icons */
.icon-customer { background-color: #20c997; }
.icon-admin { background-color: #dc3545; }
.icon-merchants-entity { background-color: #6f42c1; }
.icon-riders-entity { background-color: #fd7e14; }
</style>
