<template>
  <div class="payment-view">
    <h1>订单支付</h1>
    <div v-if="loading" class="alert alert-info">正在加载订单信息...</div>
    <div v-if="error" class="alert alert-danger">{{ error }}</div>

    <div v-if="order && order.status === 'unpaid'">
      <div class="card mb-3">
        <div class="card-body">
          <h5 class="card-title">订单号: #{{ order.orderId }}</h5>
          <p><strong>状态:</strong> <span class="badge bg-warning text-dark">待支付</span></p>
          <p><strong>总金额:</strong> ¥{{ order.totalPrice }}</p>
          <p><strong>下单时间:</strong> {{ new Date(order.orderTime).toLocaleString() }}</p>
          <p>
            请在限定时间内完成支付，否则订单可能被取消。
          </p>
        </div>
      </div>

      <div class="d-flex justify-content-between">
        <button @click="confirmPayment" class="btn btn-success btn-lg" :disabled="isProcessing">
          <span v-if="isProcessing" class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
          确认支付
        </button>
        <button @click="cancelOrder" class="btn btn-danger btn-lg" :disabled="isProcessing">
          <span v-if="isProcessing" class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
          取消订单
        </button>
      </div>
    </div>
    <div v-else-if="order && order.status !== 'unpaid'">
        <div class="alert alert-info">此订单已 {{ translateStatus(order.status) }}。</div>
        <router-link to="/orders" class="btn btn-primary">查看我的订单</router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';
import userStore from '../stores/userStore';

const route = useRoute();
const router = useRouter();
const orderId = parseInt(route.params.orderId);

const order = ref(null);
const loading = ref(true);
const error = ref(null);
const isProcessing = ref(false);

const fetchOrder = async () => {
    loading.value = true;
    try {
        const response = await axios.get(`/api/orders/${orderId}`);
        order.value = response.data;
        // Basic security check: ensure the order belongs to the current user
        if (order.value.userId !== userStore.state.userId) {
            error.value = "您无权查看此订单。";
            router.push('/orders'); // Redirect to order list
            return;
        }
    } catch (err) {
        error.value = '加载订单详情失败。';
        console.error('Error fetching order:', err);
    } finally {
        loading.value = false;
    }
};

onMounted(() => {
    if (!userStore.state.userId || userStore.state.userRole !== 'user') {
        router.push('/login'); // Only users can pay for orders
        return;
    }
    fetchOrder();
});

const confirmPayment = async () => {
  isProcessing.value = true;
  try {
    // Simulate payment processing (actual payment gateway integration would go here)
    // For now, just change status to 'preparing'
    await axios.put(`/api/orders/${orderId}/status/preparing`);
    alert('支付成功！您的订单已进入备餐阶段。');
    router.push('/orders');
  } catch (err) {
    error.value = '支付失败，请重试。';
    console.error('Payment failed:', err);
  } finally {
    isProcessing.value = false;
  }
};

const cancelOrder = async () => {
  if (!confirm('您确定要取消此订单吗？')) {
    return;
  }
  isProcessing.value = true;
  try {
    await axios.put(`/api/orders/${orderId}/status/cancelled`);
    alert('订单已成功取消。');
    router.push('/orders');
  } catch (err) {
    error.value = '取消订单失败，请重试。';
    console.error('Cancel order failed:', err);
  } finally {
    isProcessing.value = false;
  }
};

const translateStatus = (status) => {
    const map = {
        unpaid: '待支付',
        preparing: '备餐中',
        delivering: '配送中',
        completed: '已完成',
        cancelled: '已取消'
    };
    return map[status] || status;
};
</script>

<style scoped>
.payment-view {
    max-width: 600px;
    margin: 2rem auto;
    padding: 1.5rem;
    border: 1px solid #eee;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}
.btn-lg {
    flex-grow: 1;
    margin: 0 5px;
}
</style>
