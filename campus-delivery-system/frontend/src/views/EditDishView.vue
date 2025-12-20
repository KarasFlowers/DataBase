<template>
  <div>
    <h1>编辑菜品</h1>
    <div v-if="loading" class="alert alert-info">正在加载菜品数据...</div>
    <form v-if="dish" @submit.prevent="updateDish">
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
      <div class="mb-3 form-check">
        <input type="checkbox" class="form-check-input" id="isAvailable" v-model="dish.available">
        <label class="form-check-label" for="isAvailable">可购买</label>
      </div>
      <button type="submit" class="btn btn-primary">更新</button>
      <button type="button" @click="goBack" class="btn btn-secondary ms-2">取消</button>
    </form>
    <div v-if="error" class="alert alert-danger mt-3">{{ error }}</div>
  </div>
</template>

<script setup>
import { ref, onMounted, defineProps } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';
import userStore from '../stores/userStore';

const props = defineProps({
  dishId: {
    type: String,
    required: true
  }
});

const router = useRouter();
const dish = ref(null);
const categories = ref([]);
const loading = ref(true);
const error = ref(null);
let originalMerchantId = null;

onMounted(async () => {
  try {
    const response = await axios.get(`/api/dishes/${props.dishId}`);
    dish.value = response.data;
    originalMerchantId = dish.value.merchantId;

    // Access control check AFTER fetching dish details
    if (userStore.state.userRole === 'admin' || (userStore.state.userRole === 'merchant' && userStore.state.merchantId === originalMerchantId)) {
      // Allow access, now fetch categories
       try {
            const catResponse = await axios.get(`/api/categories/merchant/${originalMerchantId}`);
            categories.value = catResponse.data;
        } catch (catErr) {
            console.error("Failed to fetch categories:", catErr);
        }
    } else {
      router.push('/'); // Redirect to home if not authorized
    }

  } catch (err) {
    error.value = '加载菜品数据失败。';
    console.error('Error fetching dish:', err);
  } finally {
    loading.value = false;
  }
});

const updateDish = async () => {
  error.value = null;
  if (!dish.value) return;

  if (dish.value.price <= 0) {
    error.value = 'bro是慈善家';
    return;
  }

  try {
    await axios.put('/api/dishes', dish.value);
    goBack();
  } catch (err) {
    error.value = '更新菜品失败。';
    console.error('Error updating dish:', err);
  }
};

const goBack = () => {
  if (originalMerchantId) {
    router.push({ name: 'dishes', params: { id: originalMerchantId } });
  } else {
    router.push('/'); // Fallback if merchantId is not found
  }
};
</script>
