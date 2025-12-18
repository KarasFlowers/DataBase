<script setup>
import { RouterLink, RouterView, useRouter } from 'vue-router'
import userStore from './stores/userStore';
import cartStore from './stores/cartStore';
import { ref, watch, onMounted, computed } from 'vue';
import axios from 'axios';

const selectedView = ref('admin');
const users = ref([]);
const merchants = ref([]);
const riders = ref([]);
const router = useRouter();

// Computed property for cart item count
const cartItemCount = computed(() => cartStore.state.items.reduce((sum, item) => sum + item.quantity, 0));


// Fetch users, merchants, and riders when the component mounts
onMounted(async () => {
  try {
    const [usersResponse, merchantsResponse, ridersResponse] = await Promise.all([
      axios.get('/api/users'),
      axios.get('/api/merchants'),
      axios.get('/api/riders')
    ]);
    users.value = usersResponse.data;
    merchants.value = merchantsResponse.data;
    riders.value = ridersResponse.data;

    // Sync UI state with current route on initial load
    syncStateWithRoute();

  } catch (error) {
    console.error("Failed to fetch data for role selector:", error);
  }
});

const syncStateWithRoute = () => {
    const route = router.currentRoute.value;
    const params = route.params;

    if ((route.name === 'dishes' || route.name === 'add-dish' || route.name === 'edit-merchant') && (params.id || params.merchantId)) {
        const merchantId = parseInt(params.id || params.merchantId);
        selectedView.value = `merchant-${merchantId}`;
        userStore.setUserRole('merchant', merchantId);
    } else if ((route.name === 'users' || route.name === 'add-user' || route.name === 'edit-user' || route.name === 'rider-list' || route.name === 'add-rider' || route.name === 'edit-rider')) {
        selectedView.value = 'admin';
        userStore.setUserRole('admin');
    } else if (route.name === 'rider-dashboard' && userStore.state.riderId) {
        selectedView.value = `rider-${userStore.state.riderId}`;
    }
    // For other routes like user-specific pages, more logic can be added.
    // This covers the main inconsistency issues.
}

// Watch for changes in selectedView and update the store and route
watch(selectedView, (newVal) => {
  if (newVal === 'admin') {
    userStore.setUserRole('admin');
    router.push('/');
  } else if (newVal.startsWith('merchant-')) {
    const id = parseInt(newVal.split('-')[1]);
    userStore.setUserRole('merchant', id);
    router.push({ name: 'dishes', params: { id: id } });
  } else if (newVal.startsWith('user-')) {
    const id = parseInt(newVal.split('-')[1]);
    userStore.setUserRole('user', id);
    router.push('/');
  } else if (newVal.startsWith('rider-')) {
    const id = parseInt(newVal.split('-')[1]);
    userStore.setUserRole('rider', id);
    router.push('/rider-dashboard');
  }
});
</script>

<template>
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
                  <RouterLink class="nav-link" to="/my-reviews">我的评价</RouterLink>
                </li>
              </template>

              <!-- Merchant Links -->
              <template v-if="userStore.state.userRole === 'merchant'">
                <li class="nav-item">
                    <RouterLink class="nav-link" to="/merchant-dashboard" exact-active-class="active">数据统计</RouterLink>
                </li>
                <li class="nav-item">
                    <RouterLink class="nav-link" :to="{ name: 'dishes', params: { id: userStore.state.merchantId } }">我的菜品</RouterLink>
                </li>
                <li class="nav-item">
                  <RouterLink class="nav-link" to="/orders">店铺订单</RouterLink>
                </li>
              </template>

              <!-- Rider Links -->
              <template v-if="userStore.state.userRole === 'rider'">
                <li class="nav-item">
                  <RouterLink class="nav-link" to="/rider-dashboard" exact-active-class="active">工作台</RouterLink>
                </li>
              </template>
            </ul>

            <!-- Cart Icon for User View -->
            <ul class="navbar-nav" v-if="userStore.state.userRole === 'user'">
              <li class="nav-item">
                <RouterLink to="/cart" class="nav-link">
                  购物车
                  <span v-if="cartItemCount > 0" class="badge bg-primary rounded-pill">{{ cartItemCount }}</span>
                </RouterLink>
              </li>
            </ul>
            
            <div class="d-flex ms-3">
              <select class="form-select form-select-sm" v-model="selectedView">
                <option value="admin">管理员视角</option>
                <optgroup label="商家视角">
                  <option v-for="merchant in merchants" :key="merchant.merchantId" :value="`merchant-${merchant.merchantId}`">
                    {{ merchant.name }} (ID: {{ merchant.merchantId }})
                  </option>
                </optgroup>
                <optgroup label="用户视角">
                   <option v-for="user in users" :key="user.userId" :value="`user-${user.userId}`">
                    {{ user.username }} (ID: {{ user.userId }})
                  </option>
                </optgroup>
                <optgroup label="骑手视角">
                   <option v-for="rider in riders" :key="rider.riderId" :value="`rider-${rider.riderId}`">
                    {{ rider.name }} (ID: {{ rider.riderId }})
                  </option>
                </optgroup>
              </select>
            </div>
          </div>
        </div>
      </nav>
    </div>
  </header>

  <div class="container mt-3">
    <RouterView />
  </div>
</template>

<style scoped>
</style>
