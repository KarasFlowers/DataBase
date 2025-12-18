<template>
  <div class="my-reviews-view">
    <h1>我的评价</h1>
    <div v-if="loading" class="alert alert-info">正在加载您的评价...</div>
    <div v-else-if="error" class="alert alert-danger">{{ error }}</div>

    <div v-if="reviews.length > 0" class="list-group">
      <div v-for="review in reviews" :key="review.reviewId" class="list-group-item">
        <div class="d-flex w-100 justify-content-between">
          <h5 class="mb-1">订单 #{{ review.orderId }}</h5>
          <small>{{ new Date(review.reviewTime).toLocaleString() }}</small>
        </div>
        <p><strong>评分: {{ review.rating }}/5</strong></p>
        <p class="mb-1">{{ review.comment }}</p>
        <router-link :to="{ name: 'order-details', params: { orderId: review.orderId } }" class="btn btn-sm btn-outline-primary mt-2">查看订单</router-link>
      </div>
    </div>
    <div v-else-if="!loading" class="alert alert-secondary">
      您尚未提交任何评价。
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import userStore from '../stores/userStore';
import { useRouter } from 'vue-router';

const router = useRouter();
const loading = ref(true);
const error = ref(null);
const reviews = ref([]);

onMounted(async () => {
  if (userStore.state.userRole !== 'user' || !userStore.state.userId) {
    error.value = "您必须以用户身份登录才能查看您的评价。";
    loading.value = false;
    router.push('/');
    return;
  }

  try {
    const response = await axios.get(`/api/reviews/user/${userStore.state.userId}`);
    reviews.value = response.data;
  } catch (err) {
    error.value = "加载您的评价失败。";
    console.error('Error fetching reviews:', err);
  } finally {
    loading.value = false;
  }
});
</script>
