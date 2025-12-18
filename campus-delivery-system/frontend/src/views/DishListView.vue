<template>
  <div class="dish-list">
    <div v-if="loading" class="alert alert-info">正在加载...</div>
    <div v-if="error" class="alert alert-danger">{{ error }}</div>

    <div v-if="merchant">
      <div class="d-flex justify-content-between align-items-center mb-3">
        <div>
          <h1>{{ merchant.name }} 的菜品</h1>
          <p class="mb-0">地址: {{ merchant.address }} | 评分: {{ merchant.rating }}</p>
        </div>
        <router-link
          v-if="userStore.state.userRole === 'admin' || (userStore.state.userRole === 'merchant' && userStore.state.merchantId === merchant.merchantId)"
          :to="{ name: 'add-dish', params: { merchantId: merchant.merchantId } }"
          class="btn btn-primary"
        >
          添加菜品
        </router-link>
      </div>

      <div v-if="dishes.length > 0" class="list-group">
        <div v-for="dish in dishes" :key="dish.dishId" class="list-group-item d-flex justify-content-between align-items-center">
          <div>
            <h5>{{ dish.name }} - ¥{{ dish.price }}</h5>
            <p class="mb-1">{{ dish.description }}</p>
            <span class="badge" :class="dish.available ? 'bg-success' : 'bg-danger'">
              {{ dish.available ? '在售' : '卖完了' }}
            </span>
          </div>
          <div>
            <div class="btn-group" role="group" v-if="userStore.state.userRole === 'admin' || (userStore.state.userRole === 'merchant' && userStore.state.merchantId === merchant.merchantId)">
              <router-link :to="{ name: 'edit-dish', params: { dishId: dish.dishId } }" class="btn btn-secondary btn-sm">编辑</router-link>
              <button @click="deleteDish(dish.dishId)" class="btn btn-danger btn-sm">删除</button>
            </div>
            <div v-if="userStore.state.userRole === 'user'">
              <button @click="handleAddToCart(dish)" class="btn btn-primary btn-sm" :disabled="!dish.available">
                加入购物车
              </button>
            </div>
          </div>
        </div>
      </div>
      <div v-if="!loading && dishes.length === 0" class="alert alert-warning">
        该商家暂未上架任何菜品。
      </div>

      <!-- Reviews Section -->
      <div v-if="reviews.length > 0" class="mt-4">
        <h3>{{ merchant.name }} 的评价</h3>
        <div class="list-group">
          <div v-for="review in reviews" :key="review.reviewId" class="list-group-item">
            <div class="d-flex w-100 justify-content-between">
              <h5 class="mb-1">{{ review.username }} (评分: {{ review.rating }}/5)</h5>
              <small>
                {{ new Date(review.reviewTime).toLocaleString() }}
                <button v-if="userStore.state.userRole === 'admin'" @click="deleteReview(review.reviewId)" class="btn btn-danger btn-sm ms-2">删除</button>
              </small>
            </div>
            <p class="mb-1">{{ review.comment }}</p>
          </div>
        </div>
      </div>
      <div v-else class="alert alert-info mt-4">
        该商家暂无评价。
      </div>
    </div>

    <button @click="goBack" class="btn btn-secondary mt-3">返回商家列表</button>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';
import cartStore from '../stores/cartStore';
import userStore from '../stores/userStore';

const route = useRoute();
const router = useRouter();

const merchant = ref(null);
const dishes = ref([]);
const reviews = ref([]); // New ref for reviews
const loading = ref(true);
const error = ref(null);

const fetchDataForMerchant = async (merchantId) => {
  loading.value = true;
  error.value = null;
  try {
    const merchantResponse = await axios.get(`/api/merchants/${merchantId}`);
    merchant.value = merchantResponse.data;

    const dishesResponse = await axios.get(`/api/dishes/merchant/${merchantId}`);
    dishes.value = dishesResponse.data;

    // Fetch reviews
    const reviewsResponse = await axios.get(`/api/reviews/merchant/${merchantId}`);
    reviews.value = reviewsResponse.data;

  } catch (err) {
    error.value = '加载数据失败，请确认后端服务是否运行以及商家ID是否正确。';
    console.error('Error fetching merchant data:', err);
  } finally {
    loading.value = false;
  }
};

const handleAddToCart = (dish) => {
  if (!dish.available) return;
  cartStore.addToCart(dish, merchant.value);
  alert(`“${dish.name}” 已加入您的购物车。`);
};

// Fetch data when the component is first mounted
onMounted(() => {
  fetchDataForMerchant(route.params.id);
});

// Watch for changes in the route parameter (merchant id) and refetch data
watch(
  () => route.params.id,
  (newId, oldId) => {
    if (newId && newId !== oldId) {
      fetchDataForMerchant(newId);
    }
  }
);

const deleteReview = async (reviewId) => {
  if (confirm('您确定要删除此评价吗？')) {
    try {
      await axios.delete(`/api/reviews/${reviewId}`);
      alert('评价删除成功！');
      await fetchDataForMerchant(route.params.id); // Refresh reviews
    } catch (err) {
      alert('删除评价失败。');
      console.error('Error deleting review:', err);
    }
  }
};

const deleteDish = async (dishId) => {
  if (confirm('您确定要删除此菜品吗？')) {
    try {
      await axios.delete(`/api/dishes/${dishId}`);
      // Refresh the list after deleting by fetching data again
      await fetchDataForMerchant(route.params.id);
    } catch (err) {
      alert('删除菜品失败。');
      console.error('Error deleting dish:', err);
    }
  }
};

const goBack = () => {
  router.push('/');
};
</script>

<style scoped>
p {
  margin-bottom: 0.5rem;
}
</style>