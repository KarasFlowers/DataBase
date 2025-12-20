<template>
  <div class="admin-review-view">
    <h1>评价管理 </h1>
    <p>查看和管理平台上的所有用户评价。</p>

    <div v-if="loading" class="alert alert-info">正在加载...</div>
    <div v-if="error" class="alert alert-danger">{{ error }}</div>

    <div v-else>
      <!-- Filters -->
      <div class="card mb-4">
        <div class="card-body">
            <h5 class="card-title">筛选评价</h5>
            <div class="row">
                <div class="col-md-6">
                    <label for="userFilter" class="form-label">按用户筛选</label>
                    <select id="userFilter" class="form-select" v-model="selectedUserId">
                        <option :value="null">所有顾客</option>
                        <option v-for="user in customerUsers" :key="user.userId" :value="user.userId">
                            {{ user.username }} (ID: {{ user.userId }})
                        </option>
                    </select>
                </div>
                <div class="col-md-6">
                    <label for="merchantFilter" class="form-label">按商家筛选</label>
                    <select id="merchantFilter" class="form-select" v-model="selectedMerchantId">
                        <option :value="null">所有商家</option>
                        <option v-for="merchant in merchants" :key="merchant.merchantId" :value="merchant.merchantId">
                            {{ merchant.name }} (ID: {{ merchant.merchantId }})
                        </option>
                    </select>
                </div>
            </div>
        </div>
      </div>

      <!-- Review List -->
      <div v-if="filteredReviews.length > 0" class="list-group">
        <div v-for="review in filteredReviews" :key="review.reviewId" class="list-group-item">
          <div class="row">
            <div class="col-md-10">
                <div class="d-flex w-100 justify-content-between">
                    <h5 class="mb-1">订单 #{{ review.orderId }}</h5>
                    <small>{{ new Date(review.reviewTime).toLocaleString() }}</small>
                </div>
                <p class="mb-1"><strong>评分:</strong> {{ review.rating }}/5</p>
                <p class="mb-1"><strong>内容:</strong> {{ review.comment || '-' }}</p>
                <p class="mb-0 text-muted">
                    <small>
                        <strong>用户:</strong> {{ review.username }} (ID: {{ review.userId }}) | 
                        <strong>商家:</strong> {{ review.merchantName }} (ID: {{ review.merchantId }})
                    </small>
                </p>
            </div>
            <div class="col-md-2 d-flex align-items-center justify-content-end">
                <button @click="deleteReview(review.reviewId)" class="btn btn-danger btn-sm">删除</button>
            </div>
          </div>
        </div>
      </div>
      <div v-else class="alert alert-secondary">
        没有找到符合条件的评价。
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import axios from 'axios';
import userStore from '../stores/userStore';
import { useRouter } from 'vue-router';

const router = useRouter();

const allReviews = ref([]);
const allUsers = ref([]); // Renamed to avoid confusion
const merchants = ref([]);

const selectedUserId = ref(null);
const selectedMerchantId = ref(null);

const loading = ref(true);
const error = ref(null);

// Computed property to get only 'user' role for the filter dropdown
const customerUsers = computed(() => {
    return allUsers.value.filter(user => user.role === 'user');
});

const filteredReviews = computed(() => {
    return allReviews.value.filter(review => {
        const userMatch = !selectedUserId.value || review.userId === selectedUserId.value;
        const merchantMatch = !selectedMerchantId.value || review.merchantId === selectedMerchantId.value;
        return userMatch && merchantMatch;
    });
});

const fetchData = async () => {
    loading.value = true;
    try {
        const [reviewsRes, usersRes, merchantsRes] = await Promise.all([
            axios.get('/api/reviews'),
            axios.get('/api/users'),
            axios.get('/api/merchants')
        ]);
        allReviews.value = reviewsRes.data;
        allUsers.value = usersRes.data; // Use the new ref name
        merchants.value = merchantsRes.data;
    } catch (err) {
        error.value = "加载数据失败。";
        console.error(err);
    } finally {
        loading.value = false;
    }
};

onMounted(() => {
    if (userStore.state.userRole !== 'admin') {
        router.push('/');
        return;
    }
    fetchData();
});

const deleteReview = async (reviewId) => {
    if (confirm('您确定要删除此条评价吗？')) {
        try {
            await axios.delete(`/api/reviews/${reviewId}`);
            // Refetch all reviews after deletion
            allReviews.value = allReviews.value.filter(r => r.reviewId !== reviewId);
            alert('评价删除成功。');
        } catch (err) {
            alert('删除评价失败。');
            console.error(err);
        }
    }
};

</script>