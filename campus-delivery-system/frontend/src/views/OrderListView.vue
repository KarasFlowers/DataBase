<template>
  <div class="order-list">
    <h1>订单列表</h1>

    <!-- Date Filter for User View -->
    <div v-if="userStore.state.userRole === 'user'" class="card mb-3">
      <div class="card-body">
        <h5 class="card-title">按日期筛选</h5>
        <div class="row">
          <div class="col-md-5">
            <label for="startDate" class="form-label">起始日期</label>
            <input type="date" class="form-control" id="startDate" v-model="startDate">
          </div>
          <div class="col-md-5">
            <label for="endDate" class="form-label">结束日期</label>
            <input type="date" class="form-control" id="endDate" v-model="endDate">
          </div>
          <div class="col-md-2 d-flex align-items-end">
            <button @click="clearDates" class="btn btn-secondary w-100">清除</button>
          </div>
        </div>
      </div>
    </div>

    <div v-if="loading" class="alert alert-info">正在加载...</div>
    <div v-if="error" class="alert alert-danger">{{ error }}</div>

    <div v-if="orders.length > 0" class="list-group">
      <div v-for="order in orders" :key="order.orderId" class="list-group-item">
        <div class="d-flex w-100 justify-content-between">
            <router-link :to="{ name: 'order-details', params: { orderId: order.orderId } }" class="text-decoration-none text-dark flex-grow-1">
              <h5 class="mb-1">订单 #{{ order.orderId }}</h5>
              <p class="mb-1">总计: ¥{{ order.totalPrice }}</p>
              <small>状态: <span class="badge" :class="getStatusClass(order.status)">{{ translateStatus(order.status) }}</span></small>
               <br/><small v-if="order.merchantName">来自: {{order.merchantName}}</small>
            </router-link>
            <form v-if="userStore.state.userRole === 'admin' || (userStore.state.userRole === 'merchant' && userStore.state.merchantId === order.merchantId)" @submit.prevent="updateOrderStatus(order.orderId)" class="d-flex align-items-center ms-3">
                <select v-model="selectedStatus[order.orderId]" class="form-select form-select-sm me-2">
                    <option v-for="s in statuses" :key="s" :value="s">{{ translateStatus(s) }}</option>
                </select>
                <button type="submit" class="btn btn-sm btn-outline-primary">更新</button>
            </form>
        </div>
      </div>
    </div>
    
    <div v-if="!loading && orders.length === 0 && !error" class="alert alert-warning">
      未找到任何订单。
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watchEffect } from 'vue';
import axios from 'axios';
import userStore from '../stores/userStore';

const orders = ref([]);
const loading = ref(true);
const error = ref(null);
const statuses = ['pending', 'preparing', 'delivering', 'completed', 'cancelled'];
const selectedStatus = ref({});

// --- New refs for date filtering ---
const startDate = ref(null);
const endDate = ref(null);


const fetchOrders = async () => {
  loading.value = true;
  error.value = null;
  
  try {
    let response;
    // --- Logic for Date Filtering ---
    if (userStore.state.userRole === 'user' && userStore.state.userId && startDate.value && endDate.value) {
        // To ensure the whole day is included, set time to end of day for endDate
        const start = new Date(startDate.value);
        start.setHours(0, 0, 0, 0);

        const end = new Date(endDate.value);
        end.setHours(23, 59, 59, 999);

        response = await axios.get(`/api/orders/user/${userStore.state.userId}/bydate`, {
            params: {
                startDate: start.toISOString(),
                endDate: end.toISOString()
            }
        });

    } else { // --- Existing logic ---
        if (userStore.state.userRole === 'admin') {
          response = await axios.get('/api/orders');
        } else if (userStore.state.userRole === 'user' && userStore.state.userId) {
          response = await axios.get(`/api/orders/user/${userStore.state.userId}`);
        } else if (userStore.state.userRole === 'merchant' && userStore.state.merchantId) {
          response = await axios.get(`/api/orders/merchant/${userStore.state.merchantId}`);
        } else {
          orders.value = [];
          loading.value = false;
          return;
        }
    }

    orders.value = response.data;
    // Initialize selectedStatus for each order
    orders.value.forEach(order => {
      selectedStatus.value[order.orderId] = order.status;
    });

  } catch (err) {
    error.value = '加载订单失败。请确认后端服务运行正常且当前角色有相应数据。';
    console.error('Error fetching orders:', err);
  } finally {
    loading.value = false;
  }
};

const clearDates = () => {
    startDate.value = null;
    endDate.value = null;
    // The watchEffect will trigger a re-fetch
}

// Re-fetch orders when role, user/merchant ID, or dates change
watchEffect(() => {
  fetchOrders();
});

const updateOrderStatus = async (orderId) => {
  const newStatus = selectedStatus.value[orderId];
  if (!newStatus) {
    alert('请选择一个状态。');
    return;
  }
  try {
    await axios.put(`/api/orders/${orderId}/status/${newStatus}`);
    const order = orders.value.find(o => o.orderId === orderId);
    if (order) {
      order.status = newStatus;
    }
    alert(`订单 #${orderId} 状态已更新为 ${translateStatus(newStatus)}`);
  } catch (err) {
    alert('更新订单状态失败。');
    console.error('Error updating status:', err);
  }
};

const getStatusClass = (status) => {
  switch (status) {
    case 'pending': return 'bg-warning text-dark';
    case 'preparing': return 'bg-info text-dark';
    case 'delivering': return 'bg-primary';
    case 'completed': return 'bg-success';
    case 'cancelled': return 'bg-danger';
    default: return 'bg-secondary';
  }
};

const translateStatus = (status) => {
    const map = {
        pending: '待处理',
        preparing: '商家火速备餐中',
        delivering: '骑手火速配送中',
        completed: '已完成',
        cancelled: '已取消'
    };
    return map[status] || status;
}
</script>

<style scoped>
/* Add any specific styles for the order list here */
</style>