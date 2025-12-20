<template>
  <div class="order-list">
    <h1>订单列表</h1>

    <!-- Filters for Admin -->
    <div v-if="userStore.state.userRole === 'admin'" class="card mb-3">
        <div class="card-body">
            <h5 class="card-title">筛选订单</h5>
            <div class="row g-2">
                <div class="col-md-4">
                    <label for="userFilter" class="form-label">按用户</label>
                    <select id="userFilter" class="form-select" v-model="selectedUserId">
                        <option :value="null">所有用户</option>
                        <option v-for="user in users" :key="user.userId" :value="user.userId">{{ user.username }}</option>
                    </select>
                </div>
                <div class="col-md-4">
                    <label for="merchantFilter" class="form-label">按商家</label>
                    <select id="merchantFilter" class="form-select" v-model="selectedMerchantId">
                        <option :value="null">所有商家</option>
                        <option v-for="merchant in merchants" :key="merchant.merchantId" :value="merchant.merchantId">{{ merchant.name }}</option>
                    </select>
                </div>
                <div class="col-md-4">
                    <label for="statusFilter" class="form-label">按状态</label>
                    <select id="statusFilter" class="form-select" v-model="selectedStatusFilter">
                        <option :value="null">所有状态</option>
                        <option v-for="s in statuses" :key="s" :value="s">{{ translateStatus(s) }}</option>
                    </select>
                </div>
            </div>
        </div>
    </div>

    <!-- Date and Merchant Filter for User View -->
    <div v-if="userStore.state.userRole === 'user'" class="card mb-3">
      <div class="card-body">
        <h5 class="card-title">筛选订单</h5>
        <div class="row g-3">
          <div class="col-md-4">
            <label for="startDate" class="form-label">起始日期</label>
            <input type="date" class="form-control" id="startDate" v-model="startDate">
          </div>
          <div class="col-md-4">
            <label for="endDate" class="form-label">结束日期</label>
            <input type="date" class="form-control" id="endDate" v-model="endDate">
          </div>
          <div class="col-md-4">
            <label for="merchantSearch" class="form-label">按商家名称搜索</label>
            <input type="text" class="form-control" id="merchantSearch" v-model="merchantSearchTerm" placeholder="输入商家名...">
          </div>
        </div>
         <button @click="clearDates" class="btn btn-secondary btn-sm mt-3">清除日期</button>
      </div>
    </div>

    <div v-if="loading" class="alert alert-info">正在加载...</div>
    <div v-if="error" class="alert alert-danger">{{ error }}</div>

    <div v-else>
      <!-- Preparing Orders Section for Merchants -->
      <div v-if="userStore.state.userRole === 'merchant' && preparingOrders.length > 0" class="mb-4">
        <h3>待出餐订单</h3>
        <div class="list-group">
          <div v-for="order in preparingOrders" :key="order.orderId" class="list-group-item">
            <div class="d-flex w-100 justify-content-between">
                <router-link :to="{ name: 'order-details', params: { orderId: order.orderId } }" class="text-decoration-none text-dark flex-grow-1">
                  <h5 class="mb-1">订单 #{{ order.orderId }}</h5>
                  <p class="mb-1">总计: ¥{{ order.totalPrice }}</p>
                  <small>状态: <span class="badge" :class="getStatusClass(order.status)">{{ translateStatus(order.status) }}</span></small>
                </router-link>
                <div class="d-flex align-items-center ms-3">
                    <button @click="updateOrderStatus(order.orderId, 'ready_for_pickup')" class="btn btn-sm btn-success">已出餐</button>
                </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Other/Filtered Orders Section -->
      <div v-if="filteredOrders.length > 0">
        <h3 v-if="userStore.state.userRole === 'merchant'">已出餐订单</h3>
        <h3 v-else>订单列表</h3>
        <div class="list-group">
          <div v-for="order in filteredOrders" :key="order.orderId" class="list-group-item">
            <div class="d-flex w-100 justify-content-between align-items-center">
                <router-link :to="{ name: 'order-details', params: { orderId: order.orderId } }" class="text-decoration-none text-dark flex-grow-1">
                  <h5 class="mb-1">订单 #{{ order.orderId }}</h5>
                  <p class="mb-1" v-if="userStore.state.userRole !== 'admin'">总计: ¥{{ order.totalPrice }}</p>
                  <small>状态: <span class="badge" :class="getStatusClass(order.status)">{{ translateStatus(order.status) }}</span></small>
                  <br v-if="order.username || order.merchantName"/>
                  <small v-if="userStore.state.userRole === 'admin' || userStore.state.userRole === 'user'">商家: {{order.merchantName}}</small>
                  <small v-if="userStore.state.userRole === 'admin'"> | 用户: {{order.username}}</small>
                </router-link>
                
                <div v-if="userStore.state.userRole === 'user' && order.status === 'unpaid'" class="ms-3">
                    <router-link :to="{ name: 'pay-order', params: { orderId: order.orderId } }" class="btn btn-sm btn-warning">立即支付</router-link>
                </div>

                <div v-else-if="userStore.state.userRole === 'admin'" class="d-flex align-items-center ms-3">
                    <form @submit.prevent="updateOrderStatus(order.orderId)" class="d-flex align-items-center">
                        <select v-model="selectedStatus[order.orderId]" class="form-select form-select-sm me-2">
                            <option v-for="s in statuses" :key="s" :value="s">{{ translateStatus(s) }}</option>
                        </select>
                        <button type="submit" class="btn btn-sm btn-outline-primary">更新</button>
                    </form>
                    <button @click.prevent="deleteOrder(order.orderId)" class="btn btn-sm btn-danger ms-2">删除</button>
                </div>
            </div>
          </div>
        </div>
      </div>

      <div v-if="!loading && (filteredOrders.length === 0 && (userStore.state.userRole !== 'merchant' || preparingOrders.length === 0))" class="alert alert-warning">
        未找到任何订单。
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watchEffect, computed } from 'vue';
import axios from 'axios';
import userStore from '../stores/userStore';

const orders = ref([]);
const users = ref([]);
const merchants = ref([]);
const loading = ref(true);
const error = ref(null);
const statuses = ['unpaid', 'preparing', 'ready_for_pickup', 'delivering', 'completed', 'cancelled'];
const selectedStatus = ref({});

// --- Filter refs ---
const startDate = ref(null);
const endDate = ref(null);
const merchantSearchTerm = ref('');
const selectedUserId = ref(null);
const selectedMerchantId = ref(null);
const selectedStatusFilter = ref(null);

// --- Computed properties for filtering ---
const filteredOrders = computed(() => {
    let filtered = orders.value;

    // Apply role-specific filtering
    if (userStore.state.userRole === 'merchant') {
        return filtered.filter(order => order.status !== 'preparing');
    }
    
    if (userStore.state.userRole === 'admin') {
        return filtered.filter(order => {
            const userMatch = !selectedUserId.value || order.userId === selectedUserId.value;
            const merchantMatch = !selectedMerchantId.value || order.merchantId === selectedMerchantId.value;
            const statusMatch = !selectedStatusFilter.value || order.status === selectedStatusFilter.value;
            return userMatch && merchantMatch && statusMatch;
        });
    }

    if (userStore.state.userRole === 'user') {
        if (merchantSearchTerm.value) {
            filtered = filtered.filter(order => 
                order.merchantName && order.merchantName.toLowerCase().includes(merchantSearchTerm.value.toLowerCase())
            );
        }
    }
    
    return filtered;
});

const preparingOrders = computed(() => {
    if (userStore.state.userRole !== 'merchant') return [];
    return orders.value.filter(order => order.status === 'preparing');
});

// --- Data Fetching ---
const fetchOrders = async () => {
  loading.value = true;
  error.value = null;
  
  try {
    if (userStore.state.userRole === 'admin') {
        const [ordersRes, usersRes, merchantsRes] = await Promise.all([
            axios.get('/api/orders'),
            axios.get('/api/users'),
            axios.get('/api/merchants'),
        ]);
        orders.value = ordersRes.data;
        users.value = usersRes.data.filter(u => u.role === 'user'); // Filter for customer users
        merchants.value = merchantsRes.data;
    }
    else if (userStore.state.userRole === 'user' && userStore.state.userId && startDate.value && endDate.value) {
        const start = new Date(startDate.value);
        start.setHours(0, 0, 0, 0);
        const end = new Date(endDate.value);
        end.setHours(23, 59, 59, 999);
        const response = await axios.get(`/api/orders/user/${userStore.state.userId}/bydate`, {
            params: { startDate: start.toISOString(), endDate: end.toISOString() }
        });
        orders.value = response.data;
    } else { 
        let response;
        if (userStore.state.userRole === 'user' && userStore.state.userId) {
          response = await axios.get(`/api/orders/user/${userStore.state.userId}`);
        } else if (userStore.state.userRole === 'merchant' && userStore.state.merchantId) {
          response = await axios.get(`/api/orders/merchant/${userStore.state.merchantId}`);
        } else {
          orders.value = [];
          return;
        }
        orders.value = response.data;
    }

    orders.value.forEach(order => {
      selectedStatus.value[order.orderId] = order.status;
    });

  } catch (err) {
    error.value = '加载订单失败。';
  } finally {
    loading.value = false;
  }
};

const clearDates = () => {
    startDate.value = null;
    endDate.value = null;
}

watchEffect(fetchOrders);

const updateOrderStatus = async (orderId, newStatusParam = null) => {
  const newStatus = newStatusParam || selectedStatus.value[orderId];
  if (!newStatus) return alert('请选择一个状态。');
  if (newStatus === 'cancelled' && !confirm(`您确定要取消订单 #${orderId} 吗？`)) return;

  try {
    await axios.put(`/api/orders/${orderId}/status/${newStatus}`);
    const order = orders.value.find(o => o.orderId === orderId);
    if (order) order.status = newStatus;
    alert(`订单 #${orderId} 状态已更新为 ${translateStatus(newStatus)}`);
    if(newStatusParam) await fetchOrders();
  } catch (err) {
    alert('更新订单状态失败。');
  }
};

const deleteOrder = async (orderId) => {
    if (confirm(`您确定要永久删除订单 #${orderId} 吗？`)) {
        try {
            await axios.delete(`/api/orders/${orderId}`);
            alert(`订单 #${orderId} 已成功删除。`);
            await fetchOrders();
        } catch (err) {
            alert('删除订单失败。');
        }
    }
};

const getStatusClass = (status) => {
  switch (status) {
    case 'unpaid': return 'bg-warning text-dark';
    case 'preparing': return 'bg-info text-dark';
    case 'ready_for_pickup': return 'bg-success';
    case 'delivering': return 'bg-primary';
    case 'completed': return 'bg-secondary';
    case 'cancelled': return 'bg-danger';
    default: return 'bg-secondary';
  }
};

const translateStatus = (status) => {
    const map = {
        unpaid: '待支付',
        preparing: '备餐中',
        ready_for_pickup: '待取餐',
        delivering: '配送中',
        completed: '已完成',
        cancelled: '已取消'
    };
    return map[status] || status;
}
</script>

<style scoped>
/* Add any specific styles for the order list here */
</style>
