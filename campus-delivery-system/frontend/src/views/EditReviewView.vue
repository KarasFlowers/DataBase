<template>
  <div>
    <h1>修改评论</h1>
    <div v-if="loading" class="alert alert-info">加载评论中...</div>
    <div v-else-if="error" class="alert alert-danger">{{ error }}</div>
    
    <form v-if="reviewForm" @submit.prevent="submitUpdate">
      <div class="mb-3">
        <label for="rating" class="form-label">打分(1-5)</label>
        <input type="number" class="form-control" id="rating" v-model.number="reviewForm.rating" min="1" max="5" required>
      </div>
      <div class="mb-3">
        <label for="comment" class="form-label">评论</label>
        <textarea class="form-control" id="comment" v-model="reviewForm.comment" rows="4"></textarea>
      </div>
      <button type="submit" class="btn btn-primary" :disabled="isSubmitting">
        <span v-if="isSubmitting" class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
        {{ isSubmitting ? 'Updating...' : '更新' }}
      </button>
      <router-link to="/my-reviews" class="btn btn-secondary ms-2">取消</router-link>
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';
import userStore from '../stores/userStore';

const route = useRoute();
const router = useRouter();
const reviewId = route.params.id;

const reviewForm = ref(null);
const loading = ref(true);
const error = ref(null);
const isSubmitting = ref(false);

onMounted(async () => {
  if (userStore.state.userRole !== 'user') {
    router.push('/');
    return;
  }
  
  try {
    const response = await axios.get(`/api/reviews/${reviewId}`);
    // Security check: ensure the review belongs to the current user
    if (response.data.userId !== userStore.state.userId) {
        error.value = "You are not authorized to edit this review.";
        return;
    }
    reviewForm.value = {
        reviewId: response.data.reviewId,
        orderId: response.data.orderId,
        userId: response.data.userId,
        rating: response.data.rating,
        comment: response.data.comment,
    };
  } catch (err) {
    error.value = "Failed to load review data.";
    console.error("Error fetching review:", err);
  } finally {
    loading.value = false;
  }
});

const submitUpdate = async () => {
  if (!reviewForm.value) return;

  if (reviewForm.value.rating < 1 || reviewForm.value.rating > 5) {
      alert('Rating must be between 1 and 5.');
      return;
  }

  isSubmitting.value = true;
  try {
    await axios.put('/api/reviews', reviewForm.value);
    alert('Review updated successfully!');
    router.push('/my-reviews');
  } catch (err) {
    error.value = "Failed to update review.";
    console.error("Error updating review:", err);
  } finally {
    isSubmitting.value = false;
  }
};
</script>
