<template>
  <div class="merchant-reviews-view">
    <h1>评价管理</h1>

    <div v-if="loading" class="alert alert-info">正在加载评价...</div>
    <div v-else-if="error" class="alert alert-danger">{{ error }}</div>
    
    <div v-else-if="reviews.length > 0">
        <p>这里是您的店铺收到的所有顾客评价。</p>
        <div class="list-group">
          <router-link 
            v-for="review in reviews" 
            :key="review.reviewId" 
            :to="{ name: 'order-details', params: { orderId: review.orderId } }"
            class="list-group-item list-group-item-action"
          >
            <div class="d-flex w-100 justify-content-between">
              <h5 class="mb-1">{{ review.username }} (评分: {{ review.rating }}/5)</h5>
              <small>
                {{ new Date(review.reviewTime).toLocaleString() }}
                <span v-if="new Date(review.lastModifiedTime).getTime() > new Date(review.reviewTime).getTime()" class="text-muted fst-italic ms-2">
                    (已编辑)
                </span>
              </small>
            </div>
            <p class="mb-1">{{ review.comment }}</p>
          </router-link>
        </div>
    </div>
    <div v-else class="alert alert-secondary">
        您的店铺还没有收到任何评价。
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import userStore from '../stores/userStore';
import { useRouter } from 'vue-router';

const router = useRouter();
const reviews = ref([]);
const loading = ref(true);
const error = ref(null);

const fetchAndMarkReviews = async () => {
    const merchantId = userStore.state.merchantId;
    if (!merchantId) {
        error.value = "无法获取商家信息，请重新登录。";
        loading.value = false;
        return;
    }

    loading.value = true;
    error.value = null;

    try {
        // Fetch all reviews for the merchant
        const response = await axios.get(`/api/reviews/merchant/${merchantId}`);
        reviews.value = response.data;

        // Mark all as seen (fire and forget is okay here for UX)
        axios.post(`/api/reviews/merchant/${merchantId}/mark-as-seen`).catch(err => {
            console.error("未能标记评价为已读:", err); // Log error but don't block user
        });

    } catch (err) {
        error.value = "加载评价列表失败。";
        console.error("Error fetching reviews:", err);
    } finally {
        loading.value = false;
    }
};


onMounted(() => {
    if (userStore.state.userRole !== 'merchant') {
        router.push('/');
        return;
    }
    fetchAndMarkReviews();
});

</script>
