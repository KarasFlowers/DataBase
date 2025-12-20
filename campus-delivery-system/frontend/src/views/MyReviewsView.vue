<template>
  <div class="my-reviews-view">
    <h1>我的评价</h1>
    <div v-if="loading" class="alert alert-info">正在加载...</div>
    <div v-else-if="error" class="alert alert-danger">{{ error }}</div>

    <div v-else>
      <!-- Section for Orders to be Reviewed -->
      <section class="mb-5">
        <h3>待评价订单</h3>
        <div v-if="reviewableOrders.length > 0" class="list-group">
          <div v-for="order in reviewableOrders" :key="order.orderId" class="list-group-item">
            <div class="d-flex w-100 justify-content-between">
              <h5 class="mb-1">订单 #{{ order.orderId }}</h5>
              <small>{{ new Date(order.orderTime).toLocaleString() }}</small>
            </div>
            <p class="mb-2"><small>来自: {{ order.merchantName }}</small></p>
            
            <form @submit.prevent="submitReview(order.orderId)">
              <div class="mb-2">
                <label :for="`rating-${order.orderId}`" class="form-label visually-hidden">评分</label>
                 <div class="rating-stars">
                    <span v-for="star in 5" :key="star" @click="reviewForms[order.orderId].rating = star" :class="{ 'filled': star <= reviewForms[order.orderId].rating }">★</span>
                </div>
              </div>
              <div class="mb-2">
                <label :for="`comment-${order.orderId}`" class="form-label visually-hidden">评论</label>
                <textarea class="form-control" :id="`comment-${order.orderId}`" v-model="reviewForms[order.orderId].comment" rows="2" placeholder="输入您的评价..."></textarea>
              </div>
              <button type="submit" class="btn btn-primary btn-sm">提交评价</button>
            </form>
          </div>
        </div>
        <div v-else class="alert alert-secondary">
          没有需要评价的订单。
        </div>
      </section>

      <hr>

      <!-- Section for Already Submitted Reviews -->
      <section class="mt-4">
        <h3>已发表的评价</h3>
        <div v-if="submittedReviews.length > 0" class="list-group">
          <div v-for="review in submittedReviews" :key="review.reviewId" class="list-group-item">
            <div class="d-flex w-100 justify-content-between">
                <router-link :to="{ name: 'order-details', params: { orderId: review.orderId } }">
                     订单 #{{ review.orderId }}
                </router-link>
                <small>
                    {{ new Date(review.reviewTime).toLocaleString() }}
                    <span v-if="new Date(review.lastModifiedTime).getTime() > new Date(review.reviewTime).getTime()" class="text-muted" style="font-size: 0.8em;">
                        (已编辑: {{ new Date(review.lastModifiedTime).toLocaleString() }})
                    </span>
                </small>
            </div>
            <p class="mt-2"><strong>评分:</strong> {{ review.rating }}/5</p>
            <p class="mb-1"><strong>评论:</strong> {{ review.comment }}</p>
            <div class="mt-2">
                <router-link :to="{ name: 'edit-review', params: { id: review.reviewId } }" class="btn btn-secondary btn-sm">
                    修改
                </router-link>
            </div>
          </div>
        </div>
        <div v-else class="alert alert-secondary">
          你还没有发表过评论。
        </div>
      </section>
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
const submittedReviews = ref([]);
const reviewableOrders = ref([]);
const reviewForms = ref({});

const fetchData = async () => {
    if (!userStore.state.userId) {
        router.push('/login');
        return;
    }
    loading.value = true;
    error.value = null;

    try {
        const [submittedRes, reviewableRes] = await Promise.all([
            axios.get(`/api/reviews/user/${userStore.state.userId}`),
            axios.get(`/api/orders/user/${userStore.state.userId}/to-review`)
        ]);

        submittedReviews.value = submittedRes.data;
        reviewableOrders.value = reviewableRes.data;

        // Initialize form data for each reviewable order
        reviewableOrders.value.forEach(order => {
            reviewForms.value[order.orderId] = {
                rating: 5,
                comment: ''
            };
        });

    } catch (err) {
        error.value = '加载评价信息失败。';
        console.error('Error fetching review data:', err);
    } finally {
        loading.value = false;
    }
};

onMounted(fetchData);

const submitReview = async (orderId) => {
    const form = reviewForms.value[orderId];
    if (!form || form.rating < 1 || form.rating > 5) {
        alert('评分必须在1到5之间。');
        return;
    }

    try {
        const payload = {
            orderId: orderId,
            userId: userStore.state.userId,
            rating: form.rating,
            comment: form.comment
        };
        await axios.post('/api/reviews', payload);
        alert('评价提交成功！');
        
        // Refresh all data to show the new review and remove the form
        await fetchData();

    } catch (err) {
        alert('提交评价失败。');
        console.error('Error submitting review:', err);
    }
};

</script>

<style scoped>
.rating-stars span {
    font-size: 1.5rem;
    cursor: pointer;
    color: #ccc;
}
.rating-stars span.filled {
    color: #ffc107;
}
</style>