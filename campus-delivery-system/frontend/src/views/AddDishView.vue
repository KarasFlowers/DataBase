<template>
  <div>
    <h1>为商家添加新菜品</h1>
    <form @submit.prevent="addDish">
      <div class="mb-3">
        <label for="name" class="form-label">菜品名称</label>
        <input type="text" class="form-control" id="name" v-model="dish.name" required>
      </div>
      <div class="mb-3">
        <label for="description" class="form-label">描述</label>
        <textarea class="form-control" id="description" v-model="dish.description"></textarea>
      </div>
      <div class="mb-3">
        <label for="price" class="form-label">价格</label>
        <input type="number" step="0.01" class="form-control" id="price" v-model.number="dish.price" required>
      </div>
      <div class="mb-3">
        <label for="category" class="form-label">菜品分区</label>
        <select class="form-select" id="category" v-model="dish.categoryId">
            <option :value="null">-- 无分区 --</option>
            <option v-for="category in categories" :key="category.categoryId" :value="category.categoryId">
                {{ category.categoryName }}
            </option>
        </select>
      </div>
      <div class="mb-3">
        <label for="purchaseLimit" class="form-label">每单限购数量</label>
        <input type="number" min="1" class="form-control" id="purchaseLimit" v-model.number="dish.purchaseLimit" placeholder="留空则不限购">
      </div>
      <div class="mb-3 form-check">
        <input type="checkbox" class="form-check-input" id="isAvailable" v-model="dish.available">
        <label class="form-check-label" for="isAvailable">可购买</label>
      </div>
      <button type="submit" class="btn btn-primary">确认</button>
      <button type="button" @click="goBack" class="btn btn-secondary ms-2">取消</button>
    </form>
    <div v-if="error" class="alert alert-danger mt-3">{{ error }}</div>
  </div>
</template>

<script setup>
import { ref, defineProps, onMounted } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';
import userStore from '../stores/userStore';

const props = defineProps({
  merchantId: {
    type: String,
    required: true
  }
});

const router = useRouter();
const categories = ref([]);

const dish = ref({
  name: '',
  description: '',
  price: 0.0,
  available: true,
  merchantId: parseInt(props.merchantId),
  categoryId: null,
  purchaseLimit: null
});

const error = ref(null);

onMounted(async () => {
  const currentMerchantId = parseInt(props.merchantId);
  if (userStore.state.userRole === 'admin' || (userStore.state.userRole === 'merchant' && userStore.state.merchantId === currentMerchantId)) {
    // Allow access
  } else {
    router.push('/'); // Redirect to home if not authorized
    return;
  }

  // Fetch categories for the dropdown
  try {
      const response = await axios.get(`/api/categories/merchant/${currentMerchantId}`);
      categories.value = response.data;
  } catch (err) {
      console.error("Failed to fetch categories:", err);
      // Non-critical error, the form can still be used.
  }
});

const addDish = async () => {
  error.value = null;
  if (dish.value.price <= 0) {
      error.value = '不是哥们，送钱？';
      return;
  }
  try {
    await axios.post('/api/dishes', dish.value);
    goBack(); // Redirect to dish list on success
  } catch (err) {
    error.value = '添加菜品失败，请检查数据后重试。';
    console.error('Error adding dish:', err);
  }
};

const goBack = () => {
  router.push({ name: 'dishes', params: { id: props.merchantId } });
};
</script>

<style scoped>
</style>
