<template>
  <div class="merchant-dashboard">
    <div v-if="loading" class="alert alert-info">加载数据面板中...</div>
    <div v-else-if="error" class="alert alert-danger">{{ error }}</div>

    <div v-else-if="stats && merchantDetails">
      <h1 class="mb-4">{{ stats.merchantName }}-数据统计</h1>
      
      <!-- Stats Cards -->
      <div class="row">
        <div class="col-md-6 col-lg-3 mb-3">
          <div class="card h-100 shadow-sm">
            <div class="card-body">
              <h5 class="card-title text-muted">总订单数</h5>
              <p class="card-text fs-3 text-success fw-bold">{{ stats.totalOrders }}</p>
            </div>
          </div>
        </div>
        <div class="col-md-6 col-lg-3 mb-3">
          <div class="card h-100 shadow-sm">
            <div class="card-body">
              <h5 class="card-title text-muted">总营业额</h5>
              <p class="card-text fs-3 text-success fw-bold">￥{{ stats.totalRevenue.toFixed(2) }}</p>
            </div>
          </div>
        </div>
        <div class="col-md-6 col-lg-3 mb-3">
          <div class="card h-100 shadow-sm">
            <div class="card-body">
              <h5 class="card-title text-muted">今日订单数</h5>
              <p class="card-text fs-3 text-success fw-bold">{{ stats.ordersToday }}</p>
            </div>
          </div>
        </div>
        <div class="col-md-6 col-lg-3 mb-3">
          <div class="card h-100 shadow-sm">
            <div class="card-body">
              <h5 class="card-title text-muted">今日营业额</h5>
              <p class="card-text fs-3 text-success fw-bold">￥{{ stats.revenueToday.toFixed(2) }}</p>
            </div>
          </div>
        </div>
         <div class="col-md-6 col-lg-3 mb-3">
          <div class="card h-100 shadow-sm">
            <div class="card-body">
              <h5 class="card-title text-muted">总销量</h5>
              <p class="card-text fs-3 text-success fw-bold">{{ merchantDetails.salesCount }}</p>
            </div>
          </div>
        </div>
        <div class="col-md-6 col-lg-3 mb-3">
          <div class="card h-100 shadow-sm">
            <div class="card-body">
              <h5 class="card-title text-muted">人均消费</h5>
              <p class="card-text fs-3 text-success fw-bold">￥{{ merchantDetails.averageOrderPrice ? merchantDetails.averageOrderPrice.toFixed(2) : '0.00' }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Top Selling Dishes Charts -->
      <div class="top-dishes mt-4 card shadow-sm">
        <div class="card-body">
            <h2 class="card-title mb-3">最受欢迎的菜品</h2>
            <div v-if="stats.topSellingDishes && stats.topSellingDishes.length > 0">
                <div class="row">
                    <div class="col-md-6">
                        <h4 class="text-center text-muted mb-3">按总营收排名</h4>
                        <Bar :data="revenueChartData" :options="revenueChartOptions" width="600" height="600" />
                    </div>
                    <div class="col-md-6">
                        <h4 class="text-center text-muted mb-3">按销售数量排名</h4>
                        <Bar :data="quantityChartData" :options="quantityChartOptions" width="600" height="600" />
                    </div>
                </div>
            </div>
            <div v-else class="alert alert-secondary mb-0">
                您的店铺还没有销售记录。
            </div>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import userStore from '../stores/userStore';

// Chart.js imports
import { Bar } from 'vue-chartjs';
import { Chart as ChartJS, Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale } from 'chart.js';

ChartJS.register(Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale);

const router = useRouter();
const loading = ref(true);
const error = ref(null);
const stats = ref(null);
const merchantDetails = ref(null);

// Chart data and options
const revenueChartData = computed(() => {
  if (!stats.value || !stats.value.topSellingDishes) {
    return { labels: [], datasets: [] };
  }
  // Sort by revenue before slicing
  const sortedDishes = [...stats.value.topSellingDishes].sort((a, b) => b.totalDishRevenue - a.totalDishRevenue);
  const dishes = sortedDishes.slice(0, 5); // Limit to top 5 dishes
  const labels = dishes.map(dish => dish.dishName);

  return {
    labels: labels,
    datasets: [
      {
        label: '总营收 (￥)',
        backgroundColor: '#42A5F5', // Blue
        data: dishes.map(dish => dish.totalDishRevenue),
      },
    ],
  };
});

const quantityChartData = computed(() => {
  if (!stats.value || !stats.value.topSellingDishes) {
    return { labels: [], datasets: [] };
  }
  // Sort by quantity sold before slicing
  const sortedDishes = [...stats.value.topSellingDishes].sort((a, b) => b.totalQuantitySold - a.totalQuantitySold);
  const dishes = sortedDishes.slice(0, 5); // Limit to top 5 dishes
  const labels = dishes.map(dish => dish.dishName);

  return {
    labels: labels,
    datasets: [
      {
        label: '销售数量',
        backgroundColor: '#66BB6A', // Green
        data: dishes.map(dish => dish.totalQuantitySold),
      },
    ],
  };
});


const revenueChartOptions = {
  // responsive: true, // Removed responsive
  // maintainAspectRatio: false, // Removed maintainAspectRatio
  width: 600, // Explicitly set fixed width
  height: 600, // Explicitly set fixed height
  plugins: {
    legend: {
      display: true,
    },
    title: {
      display: true,
      text: '菜品销售额排名',
    },
  },
  scales: {
    y: {
      beginAtZero: true,
      title: {
        display: true,
        text: '销售额 (￥)',
      },
    },
    x: {
      title: {
        display: true,
        text: '菜品名称',
      },
    },
  },
};

const quantityChartOptions = {
  // responsive: true, // Removed responsive
  // maintainAspectRatio: false, // Removed maintainAspectRatio
  width: 600, // Explicitly set fixed width
  height: 600, // Explicitly set fixed height
  plugins: {
    legend: {
      display: true,
    },
    title: {
      display: true,
      text: '菜品销售数量排名',
    },
  },
  scales: {
    y: {
      beginAtZero: true,
      title: {
        display: true,
        text: '销售数量',
      },
      ticks: {
          stepSize: 1, // Ensure quantity is integer
      }
    },
    x: {
      title: {
        display: true,
        text: '菜品名称',
      },
    },
  },
};


const fetchDashboardData = async (merchantId) => {
  if (!merchantId) return;
  loading.value = true;
  error.value = null;
  try {
    const [dashboardRes, merchantRes] = await Promise.all([
        axios.get(`/api/analytics/merchant/${merchantId}/dashboard`),
        axios.get(`/api/merchants/${merchantId}`)
    ]);
    stats.value = dashboardRes.data;
    merchantDetails.value = merchantRes.data; // Assign merchant details
  } catch (err) {
    error.value = 'Failed to load dashboard statistics.';
    console.error(err);
    stats.value = null; // Clear old data on error
    merchantDetails.value = null;
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
