<script setup>
import { RouterLink, RouterView, useRouter } from 'vue-router';
import userStore from './stores/userStore';
import cartStore from './stores/cartStore';
import { ref, computed, watch, onMounted, onUnmounted } from 'vue';
import axios from 'axios';

const router = useRouter();

// Notification state
const newOrderCount = ref(0); // For merchant new orders
const newReviewCount = ref(0); // For merchant new reviews
const reviewableOrderCount = ref(0); // For user
let merchantPollingTimer = null;
let userPollingTimer = null;

// Computed property for cart item count
const cartItemCount = computed(() => cartStore.state.items.reduce((sum, item) => sum + item.quantity, 0));

const handleLogout = () => {
    userStore.logout();
    stopAllPolling(); // Stop all polling on logout
    router.push({ name: 'login' });
};

// --- Polling Functions ---

const fetchNewOrderCount = async () => {
    if (userStore.state.userRole === 'merchant' && userStore.state.merchantId) {
        try {
            const response = await axios.get(`/api/orders/merchant/${userStore.state.merchantId}/count?status=preparing`);
            newOrderCount.value = response.data;
        } catch (err) {
            console.error("Failed to fetch new order count:", err);
        }
    }
};

const fetchNewReviewCount = async () => {
    if (userStore.state.userRole === 'merchant' && userStore.state.merchantId) {
        try {
            const response = await axios.get(`/api/reviews/merchant/${userStore.state.merchantId}/unseen/count`);
            newReviewCount.value = response.data;
        } catch (err) {
            console.error("Failed to fetch new review count:", err);
        }
    }
};

const fetchReviewableOrderCount = async () => {
    if (userStore.state.userRole === 'user' && userStore.state.userId) {
        try {
            const response = await axios.get(`/api/orders/user/${userStore.state.userId}/to-review/count`);
            reviewableOrderCount.value = response.data;
        } catch (err) {
            console.error("Failed to fetch reviewable order count:", err);
        }
    }
};

const startMerchantPolling = () => {
    stopMerchantPolling();
    fetchNewOrderCount();
    fetchNewReviewCount();
    merchantPollingTimer = setInterval(() => {
        fetchNewOrderCount();
        fetchNewReviewCount();
    }, 10000);
};

const stopMerchantPolling = () => {
    if (merchantPollingTimer) clearInterval(merchantPollingTimer);
    merchantPollingTimer = null;
    newOrderCount.value = 0;
    newReviewCount.value = 0;
};

const startUserPolling = () => {
    stopUserPolling();
    fetchReviewableOrderCount();
    userPollingTimer = setInterval(fetchReviewableOrderCount, 10000);
};

const stopUserPolling = () => {
    if (userPollingTimer) clearInterval(userPollingTimer);
    userPollingTimer = null;
    reviewableOrderCount.value = 0;
};

const stopAllPolling = () => {
    stopMerchantPolling();
    stopUserPolling();
};

// Watch for role changes to start/stop polling
watch(() => userStore.state.userRole, (newRole) => {
    stopAllPolling();
    if (newRole === 'merchant') {
        startMerchantPolling();
    } else if (newRole === 'user') {
        startUserPolling();
    }
}, { immediate: true }); // Use immediate to run on initial load

// Clean up timer when component is unmounted
onUnmounted(stopAllPolling);

</script>

<template>
  <!-- Main app is rendered only if a user is logged in -->
  <div v-if="userStore.state.userRole">
    <header>
      <div class="wrapper">
        <nav class="navbar navbar-expand-lg navbar-light bg-light">
          <div class="container-fluid">
            <RouterLink class="navbar-brand" to="/">校园外卖</RouterLink>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
              <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
              <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <!-- Admin Links -->
                <template v-if="userStore.state.userRole === 'admin'">
                  <li class="nav-item">
                    <RouterLink class="nav-link" to="/admin-dashboard" exact-active-class="active">数据统计</RouterLink>
                  </li>
                  <li class="nav-item">
                    <RouterLink class="nav-link" to="/" exact-active-class="active">商家管理</RouterLink>
                  </li>
                  <li class="nav-item">
                    <RouterLink class="nav-link" to="/users">用户管理</RouterLink>
                  </li>
                  <li class="nav-item">
                    <RouterLink class="nav-link" to="/riders">骑手管理</RouterLink>
                  </li>
                  <li class="nav-item">
                    <RouterLink class="nav-link" to="/orders">订单管理</RouterLink>
                  </li>
                  <li class="nav-item">
                    <RouterLink class="nav-link" to="/admin/reviews">评价管理</RouterLink>
                  </li>
                </template>

                <!-- User Links -->
                <template v-if="userStore.state.userRole === 'user'">
                  <li class="nav-item">
                    <RouterLink class="nav-link" to="/" exact-active-class="active">浏览商家</RouterLink>
                  </li>
                  <li class="nav-item">
                    <RouterLink class="nav-link" to="/orders">我的订单</RouterLink>
                  </li>
                  <li class="nav-item">
                    <RouterLink class="nav-link" to="/addresses">我的地址</RouterLink>
                  </li>
                   <li class="nav-item">
                    <RouterLink class="nav-link" to="/my-reviews">
                        我的评价
                        <span v-if="reviewableOrderCount > 0" class="badge bg-danger ms-1">{{ reviewableOrderCount }}</span>
                    </RouterLink>
                  </li>
                </template>

                <!-- Merchant Links -->
                <template v-if="userStore.state.userRole === 'merchant'">
                    <li class="nav-item">
                        <RouterLink class="nav-link" to="/merchant-dashboard" exact-active-class="active">数据统计</RouterLink>
                    </li>
                   <li class="nav-item">
                        <RouterLink class="nav-link" :to="{ name: 'dishes', params: { id: userStore.state.merchantId } }" exact-active-class="active">我的菜品</RouterLink>
                    </li>
                    <li class="nav-item">
                        <RouterLink class="nav-link" to="/category-management">分区管理</RouterLink>
                    </li>
                    <li class="nav-item">
                        <RouterLink class="nav-link" to="/merchant-reviews">
                            评价管理
                            <span v-if="newReviewCount > 0" class="badge bg-danger ms-1">{{ newReviewCount }}</span>
                        </RouterLink>
                    </li>
                    <li class="nav-item">
                        <RouterLink class="nav-link" to="/orders">
                            店铺订单
                            <span v-if="newOrderCount > 0" class="badge bg-danger ms-1">{{ newOrderCount }}</span>
                        </RouterLink>
                    </li>
                </template>

                <!-- Rider Links -->
                <template v-if="userStore.state.userRole === 'rider'">
                  <li class="nav-item">
                    <RouterLink class="nav-link" to="/rider-dashboard" exact-active-class="active">工作台</RouterLink>
                  </li>
                </template>
              </ul>

              <!-- Right-aligned items -->
              <ul class="navbar-nav ms-auto mb-2 mb-lg-0 d-flex align-items-center">
                <!-- Cart Icon for User View -->
                <li class="nav-item" v-if="userStore.state.userRole === 'user'">
                  <RouterLink to="/cart" class="nav-link">
                    购物车
                    <span v-if="cartItemCount > 0" class="badge bg-primary rounded-pill">{{ cartItemCount }}</span>
                  </RouterLink>
                </li>
                <!-- User Info and Logout -->
                 <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                        欢迎, {{ userStore.state.username }} ({{ userStore.state.userRole }})
                    </a>
                    <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="navbarDropdown">
                        <li><a class="dropdown-item" href="#" @click.prevent="handleLogout">退出登录</a></li>
                    </ul>
                </li>
              </ul>
            </div>
          </div>
        </nav>
      </div>
    </header>

    <main class="container mt-3">
      <RouterView />
    </main>
  </div>
  
  <!-- Login view is rendered here when user is logged out -->
  <div v-else>
      <RouterView />
  </div>
</template>

<style scoped>
</style>