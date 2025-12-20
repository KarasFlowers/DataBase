<template>
  <div class="category-management">
    <h2>菜品分区管理</h2>

    <div v-if="loading" class="alert alert-info">加载中...</div>
    <div v-if="error" class="alert alert-danger">{{ error }}</div>

    <!-- Add Category Form -->
    <div class="card mb-4">
        <div class="card-body">
            <h5 class="card-title">添加新分区</h5>
            <form @submit.prevent="addCategory" class="d-flex">
                <input type="text" class="form-control me-2" v-model="newCategoryName" placeholder="输入分区名称" required>
                <button type="submit" class="btn btn-primary">添加</button>
            </form>
        </div>
    </div>

    <!-- Category List -->
    <h4>我的菜品分区</h4>
    <div v-if="categories.length > 0" class="list-group">
      <div v-for="category in categories" :key="category.categoryId" class="list-group-item d-flex justify-content-between align-items-center">
        <span>{{ category.categoryName }}</span>
        <button @click="deleteCategory(category.categoryId)" class="btn btn-danger btn-sm">删除</button>
      </div>
    </div>
    <div v-else class="alert alert-secondary">
      您还没有创建菜品分区！
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import userStore from '../stores/userStore';
import { useRouter } from 'vue-router';

const router = useRouter();
const categories = ref([]);
const newCategoryName = ref('');
const loading = ref(true);
const error = ref(null);

const fetchCategories = async () => {
  if (!userStore.state.merchantId) {
    error.value = "请先选择商家";
    return;
  }
  loading.value = true;
  try {
    const response = await axios.get(`/api/categories/merchant/${userStore.state.merchantId}`);
    categories.value = response.data;
  } catch (err) {
    error.value = "Failed to load categories.";
    console.error(err);
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
    if (userStore.state.userRole !== 'merchant' && userStore.state.userRole !== 'admin') {
        router.push('/');
        return;
    }
    fetchCategories();
});

const addCategory = async () => {
  if (!newCategoryName.value.trim()) return;
  try {
    const payload = {
      merchantId: userStore.state.merchantId,
      categoryName: newCategoryName.value,
    };
    await axios.post('/api/categories', payload);
    newCategoryName.value = '';
    await fetchCategories(); // Refresh list
  } catch (err) {
    if (err.response && err.response.status === 409) {
        alert('分区名称已存在！');
    } else {
        alert('添加分区失败！请检查是否已经存在');
    }
    console.error(err);
  }
};

const deleteCategory = async (categoryId) => {
  if (confirm('Are you sure you want to delete this category? Dishes in it will become uncategorized.')) {
    try {
      await axios.delete(`/api/categories/${categoryId}`);
      await fetchCategories(); // Refresh list
    } catch (err) {
      alert('Failed to delete category.');
      console.error(err);
    }
  }
};
</script>
