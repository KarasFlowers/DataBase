<template>
  <div>
    <div v-if="loading" class="alert alert-info">正在加载订单详情...</div>
    <div v-if="error" class="alert alert-danger">{{ error }}</div>

    <div v-if="order && fullOrderDetails.length > 0">
      <h1>订单 #{{ order.orderId }}</h1>
      <div class="card mb-3">
        <div class="card-body">
          <h5 class="card-title">订单概览</h5>
          <p><strong>状态:</strong> <span class="badge" :class="getStatusClass(order.status)">{{ translateStatus(order.status) }}</span></p>
          <p><strong>总金额:</strong> ¥{{ order.totalPrice }}</p>
          <p><strong>下单时间:</strong> {{ new Date(order.orderTime).toLocaleString() }}</p>
          <p><strong>用户ID:</strong> {{ order.userId }}</p>
          <p><strong>商家ID:</strong> {{ order.merchantId }}</p>
        </div>
      </div>

      <!-- Delivery Info Section -->
      <div v-if="userStore.state.userRole === 'admin' || userStore.state.userRole === 'merchant'" class="card mb-3">
        <div class="card-body">
            <h5 class="card-title">配送信息</h5>
            <div v-if="deliveryRecord && riderInfo">
                <p><strong>骑手:</strong> {{ riderInfo.name }}</p>
                <p><strong>骑手电话:</strong> {{ riderInfo.phoneNumber }}</p>
                <p><strong>配送状态:</strong> <span class="badge" :class="getDeliveryStatusClass(deliveryRecord.status)">{{ translateDeliveryStatus(deliveryRecord.status) }}</span></p>
                <p v-if="deliveryRecord.pickupTime"><strong>取货时间:</strong> {{ new Date(deliveryRecord.pickupTime).toLocaleString() }}</p>
                <p v-if="deliveryRecord.deliveryTime"><strong>送达时间:</strong> {{ new Date(deliveryRecord.deliveryTime).toLocaleString() }}</p>
            </div>
            <div v-else>
                <p>等待骑手接单。</p>
            </div>
        </div>
      </div>

      <div class="card">
        <div class="card-body">
          <h5 class="card-title">订单商品</h5>
          <ul class="list-group list-group-flush">
            <li v-for="item in fullOrderDetails" :key="item.orderDetailId" class="list-group-item">
              <div class="d-flex justify-content-between">
                <div>
                  <strong>{{ item.dishName || '加载中...' }}</strong>
                  <p class="mb-0 text-muted">数量: {{ item.quantity }} @ ¥{{ item.pricePerItem }} /件</p>
                </div>
                <div>
                  <strong>¥{{ (item.quantity * item.pricePerItem).toFixed(2) }}</strong>
                </div>
              </div>
            </li>
          </ul>
        </div>
      </div>

      <!-- Review Section -->
      <div class="card mt-3">
        <div class="card-body">
            <h5 class="card-title">评价</h5>
            <div v-if="currentReview">
                <p><strong>您的评分:</strong> {{ currentReview.rating }}/5</p>
                <p><strong>您的评论:</strong> {{ currentReview.comment }}</p>
                <p class="text-muted"><small>评价于: {{ new Date(currentReview.reviewTime).toLocaleString() }}</small></p>
            </div>
            <div v-else-if="userStore.state.userRole === 'user' && order.userId === userStore.state.userId && order.status === 'completed'">
                <h6>提交您的评价</h6>
                <form @submit.prevent="submitReview">
                    <div class="mb-3">
                        <label for="rating" class="form-label">评分 (1-5)</label>
                        <input type="number" class="form-control" id="rating" v-model.number="reviewForm.rating" min="1" max="5" required>
                    </div>
                    <div class="mb-3">
                        <label for="comment" class="form-label">评论</label>
                        <textarea class="form-control" id="comment" v-model="reviewForm.comment" rows="3"></textarea>
                    </div>
                    <button type="submit" class="btn btn-primary">提交评价</button>
                </form>
            </div>
            <div v-else>
                <p>该订单暂无评价。订单完成后用户才能提交评价。</p>
            </div>
        </div>
      </div>

       <router-link to="/orders" class="btn btn-secondary mt-3">返回订单列表</router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, defineProps } from 'vue';
import axios from 'axios';
import userStore from '../stores/userStore';

const props = defineProps({
  orderId: {
    type: String,
    required: true
  }
});

const order = ref(null);
const fullOrderDetails = ref([]);
const loading = ref(true);
const error = ref(null);
const reviewForm = ref({
  rating: 5,
  comment: ''
});
const currentReview = ref(null);
const deliveryRecord = ref(null);
const riderInfo = ref(null);

onMounted(async () => {
  try {
    // Fetch main order data
    const orderResponse = await axios.get(`/api/orders/${props.orderId}`);
    order.value = orderResponse.data;

    // Fetch order detail items
    const detailsResponse = await axios.get(`/api/orderdetails/order/${props.orderId}`);
    const details = detailsResponse.data;

    // For each item, fetch the dish name
    const enrichedDetails = await Promise.all(
      details.map(async (item) => {
        try {
          const dishResponse = await axios.get(`/api/dishes/${item.dishId}`);
          return {
            ...item,
            dishName: dishResponse.data.name,
          };
        } catch (dishError) {
          console.error(`获取菜品名称失败，菜品ID ${item.dishId}`, dishError);
          return { ...item, dishName: '未知菜品' };
        }
      })
    );
    fullOrderDetails.value = enrichedDetails;

    // Fetch existing review for this order
    try {
      const reviewResponse = await axios.get(`/api/reviews/order/${props.orderId}`);
      currentReview.value = reviewResponse.data;
    } catch (reviewErr) {
      if (reviewErr.response && reviewErr.response.status === 404) {
        currentReview.value = null; // No review found, this is normal
      } else {
        console.error('获取已有评价失败:', reviewErr);
      }
    }

    // Fetch delivery record and rider info
    if (userStore.state.userRole === 'admin' || userStore.state.userRole === 'merchant') {
        try {
            const deliveryResponse = await axios.get(`/api/deliveryrecords/order/${props.orderId}`);
            deliveryRecord.value = deliveryResponse.data;
            if (deliveryRecord.value && deliveryRecord.value.riderId) {
                const riderResponse = await axios.get(`/api/riders/${deliveryRecord.value.riderId}`);
                riderInfo.value = riderResponse.data;
            }
        } catch (deliveryErr) {
            if (deliveryErr.response && deliveryErr.response.status === 404) {
                deliveryRecord.value = null; // No delivery record, this is normal
                riderInfo.value = null;
            } else {
                console.error('获取配送信息失败:', deliveryErr);
            }
        }
    }

  } catch (err) {
    error.value = '加载订单详情失败。';
    console.error('Error fetching order details:', err);
  } finally {
    loading.value = false;
  }
});

const submitReview = async () => {
  if (!userStore.state.userId || userStore.state.userRole !== 'user') {
    alert('您必须以用户身份登录才能提交评价。');
    return;
  }
  if (reviewForm.value.rating < 1 || reviewForm.value.rating > 5) {
    alert('评分必须在1到5之间。');
    return;
  }

  try {
    const reviewPayload = {
      orderId: parseInt(props.orderId),
      userId: userStore.state.userId,
      rating: reviewForm.value.rating,
      comment: reviewForm.value.comment
    };
    await axios.post('/api/reviews', reviewPayload);
    alert('评价提交成功！');
    // Refresh the review after submission
    const reviewResponse = await axios.get(`/api/reviews/order/${props.orderId}`);
    currentReview.value = reviewResponse.data;
  } catch (err) {
    alert('提交评价失败。');
    console.error('Error submitting review:', err);
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
        preparing: '备餐中',
        delivering: '配送中',
        completed: '已完成',
        cancelled: '已取消'
    };
    return map[status] || status;
}

const getDeliveryStatusClass = (status) => {
    switch(status) {
        case 'assigned': return 'bg-info text-dark';
        case 'picked_up': return 'bg-primary';
        case 'delivered': return 'bg-success';
        default: return 'bg-secondary';
    }
};

const translateDeliveryStatus = (status) => {
    const map = {
        assigned: '已分配骑手',
        picked_up: '骑手已取货',
        delivered: '已送达'
    };
    return map[status] || status;
}
</script>
