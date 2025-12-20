<template>
  <div class="rider-dashboard">
    <h1>骑手工作台</h1>
    <div v-if="loading" class="alert alert-info">正在加载工作台数据...</div>
    <div v-else-if="error" class="alert alert-danger">{{ error }}</div>

    <div v-else>
      <!-- Assigned Orders Section -->
      <div class="assigned-orders mb-5">
        <h2>我的当前配送</h2>
        <div v-if="assignedOrders.length === 0" class="alert alert-secondary">
          您当前没有正在配送的订单。
        </div>
        <div v-else class="list-group">
          <div v-for="order in assignedOrders" :key="order.orderId" class="list-group-item">
            <h5>订单 #{{ order.orderId }} (商家: {{ order.merchantName }})</h5>
            <p><strong>订单状态:</strong> <span class="badge bg-primary">{{ translateStatus(order.status) }}</span></p>
            <p><strong>配送地址:</strong> {{ order.addressDetails }}</p>
            <div v-if="order.deliveryStatus">
                <p><strong>配送状态:</strong> <span class="badge" :class="getDeliveryStatusClass(order.deliveryStatus)">{{ translateDeliveryStatus(order.deliveryStatus) }}</span></p>
                <div class="btn-group mt-2">
                    <button v-if="order.deliveryStatus === 'assigned'" @click="updateDeliveryStatus(order, 'picked_up')" class="btn btn-sm btn-info">我已取货</button>
                    <button v-if="order.deliveryStatus === 'picked_up'" @click="updateDeliveryStatus(order, 'delivered')" class="btn btn-sm btn-success">确认送达</button>
                </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Available Orders Section -->
      <div class="available-orders">
        <h2>可接订单</h2>
        <div v-if="availableOrders.length === 0" class="alert alert-secondary">
          当前没有可接的订单。
        </div>
        <div v-else class="list-group">
          <div v-for="order in availableOrders" :key="order.orderId" class="list-group-item d-flex justify-content-between align-items-center">
            <div>
              <h5>订单 #{{ order.orderId }} (商家: {{ order.merchantName }})</h5>
              <p><strong>配送地址:</strong> {{ order.addressDetails }}</p>
            </div>
            <button @click="acceptOrder(order.orderId)" class="btn btn-primary">接受订单</button>
          </div>
        </div>
      </div>

      <!-- Completed Orders Section -->
      <div class="completed-orders mt-5">
        <h2>我的配送历史</h2>
        <div v-if="completedOrders.length === 0" class="alert alert-secondary">
          您还没有已完成的配送记录。
        </div>
        <div v-else class="list-group">
          <div v-for="order in completedOrders" :key="order.orderId" class="list-group-item">
            <h5>订单 #{{ order.orderId }} (商家: {{ order.merchantName }})</h5>
            <p><strong>状态:</strong> <span class="badge bg-success">{{ translateStatus(order.status) }}</span></p>
            <p class="mb-0"><strong>送达至:</strong> {{ order.addressDetails }}</p>
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
const assignedOrders = ref([]);
const availableOrders = ref([]);
const completedOrders = ref([]);

const enrichOrders = async (orders) => {
    return await Promise.all(orders.map(async (order) => {
        try {
            const [merchantRes, addressRes] = await Promise.all([
                axios.get(`/api/merchants/${order.merchantId}`),
                axios.get(`/api/addresses/${order.addressId}`)
            ]);
            return {
                ...order,
                merchantName: merchantRes.data.name,
                addressDetails: addressRes.data.addressDetails
            };
        } catch (enrichError) {
            console.error(`Failed to enrich order #${order.orderId}`, enrichError);
            return { ...order, merchantName: 'Unknown', addressDetails: 'Unknown' };
        }
    }));
};

const fetchData = async () => {
  if (userStore.state.userRole !== 'rider' || !userStore.state.riderId) {
    error.value = "您必须以骑手身份登录。";
    loading.value = false;
    return;
  }
  loading.value = true;
  error.value = null;

  try {
    const riderId = userStore.state.riderId;

    // Fetch available orders and rider's own records concurrently
    const [availableRes, deliveryRecordsRes] = await Promise.all([
        axios.get('/api/orders/available'), // New endpoint for 'ready_for_pickup' orders
        axios.get(`/api/deliveryrecords/rider/${riderId}`)
    ]);
    
    // Enrich available orders
    availableOrders.value = await enrichOrders(availableRes.data);

    // Process rider's own delivery records
    const riderRecords = deliveryRecordsRes.data;
    if (riderRecords.length > 0) {
        const orderIds = riderRecords.map(rec => rec.orderId);
        
        // Fetch full details for only the orders this rider is associated with
        const associatedOrders = [];
        for (const id of orderIds) {
            const response = await axios.get(`/api/orders/${id}`);
            associatedOrders.push(response.data);
        }
        const enrichedAssociatedOrders = await enrichOrders(associatedOrders);

        assignedOrders.value = enrichedAssociatedOrders
            .filter(order => order.status === 'delivering')
            .map(order => {
                const record = riderRecords.find(r => r.orderId === order.orderId);
                return { ...order, deliveryStatus: record?.status, deliveryId: record?.deliveryId };
            });

        completedOrders.value = enrichedAssociatedOrders
            .filter(order => order.status === 'completed');
    } else {
        // If rider has no records, their lists are empty
        assignedOrders.value = [];
        completedOrders.value = [];
    }

  } catch (err) {
    error.value = '加载工作台数据失败。';
    console.error('Error fetching data:', err);
  } finally {
    loading.value = false;
  }
};

onMounted(fetchData);

const acceptOrder = async (orderId) => {
    try {
        // 1. Update order status to 'delivering'
        await axios.put(`/api/orders/${orderId}/status/delivering`);

        // 2. Create a delivery record
        const deliveryPayload = {
            orderId: orderId,
            riderId: userStore.state.riderId,
            status: 'assigned', // Initial status
        };
        await axios.post('/api/deliveryrecords', deliveryPayload);

        alert(`订单 #${orderId} 已成功接受！`);
        await fetchData(); // Refresh data

    } catch (err) {
        alert('接受订单失败。');
        console.error('Error accepting order:', err);
    }
};

const updateDeliveryStatus = async (order, newStatus) => {
    try {
        // 1. Update delivery record
        const deliveryUpdatePayload = {
            deliveryId: order.deliveryId,
            orderId: order.orderId,
            riderId: userStore.state.riderId,
            status: newStatus,
        };
        await axios.put('/api/deliveryrecords', deliveryUpdatePayload);

        // 2. If delivered, update the main order status
        if (newStatus === 'delivered') {
            await axios.put(`/api/orders/${order.orderId}/status/completed`);
             alert(`订单 #${order.orderId} 已被标记为“已送达”！`);
        } else {
             alert(`订单 #${order.orderId} 状态已更新为“${translateDeliveryStatus(newStatus)}”。`);
        }

        await fetchData(); // Refresh data

    } catch (err) {
        alert('更新配送状态失败。');
        console.error('Error updating delivery status:', err);
    }
};

const getDeliveryStatusClass = (status) => {
    switch(status) {
        case 'assigned': return 'bg-info text-dark';
        case 'picked_up': return 'bg-primary';
        case 'delivered': return 'bg-success';
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
};

const translateDeliveryStatus = (status) => {
    const map = {
        assigned: '已分配',
        picked_up: '已取货',
        delivered: '已送达'
    };
    return map[status] || status;
};

</script>

<style scoped>
.rider-dashboard {
  max-width: 900px;
  margin: auto;
}
</style>
