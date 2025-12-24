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
    <h4>
      我的菜品分区
      <button v-if="isOrderDirty" @click="saveOrder" class="btn btn-success btn-sm ms-3">保存顺序</button>
    </h4>
    <div v-if="categories.length > 0" class="list-group">
      <div v-for="(category, index) in categories" :key="category.categoryId" class="list-group-item d-flex justify-content-between align-items-center">
        
        <!-- Display or Edit mode -->
        <div class="d-flex align-items-center flex-grow-1">
            <div class="me-3 text-muted">#{{ index + 1 }}</div>
            <div v-if="editingCategoryId === category.categoryId" class="d-flex flex-grow-1">
                <input type="text" class="form-control me-2" v-model="editingCategoryName">
            </div>
            <span v-else>{{ category.categoryName }}</span>
        </div>

        <!-- Action Buttons -->
        <div class="btn-group">
            <template v-if="editingCategoryId === category.categoryId">
                <button @click="saveEdit(category)" class="btn btn-success btn-sm">保存</button>
                <button @click="cancelEdit" class="btn btn-secondary btn-sm">取消</button>
            </template>
            <template v-else>
                <button @click="moveUp(index)" :disabled="index === 0" class="btn btn-light btn-sm">↑</button>
                <button @click="moveDown(index)" :disabled="index === categories.length - 1" class="btn btn-light btn-sm">↓</button>
                <button @click="startEdit(category)" class="btn btn-secondary btn-sm">修改</button>
                <button @click="deleteCategory(category.categoryId)" class="btn btn-danger btn-sm">删除</button>
            </template>
        </div>

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

const editingCategoryId = ref(null);
const editingCategoryName = ref('');
const isOrderDirty = ref(false); // To track if order has changed

const fetchCategories = async () => {
  if (!userStore.state.merchantId) {
    error.value = "请先选择商家";
    return;
  }
  loading.value = true;
  try {
    const response = await axios.get(`/api/categories/merchant/${userStore.state.merchantId}`);
    categories.value = response.data;
    isOrderDirty.value = false; // Reset dirty flag on fetch
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
        alert('添加分区失败！');
    }
    console.error(err);
  }
};

const deleteCategory = async (categoryId) => {
  if (confirm('确定要删除此分区吗？分区下的菜品将变为“未分区”状态。')) {
    try {
      await axios.delete(`/api/categories/${categoryId}`);
      await fetchCategories(); // Refresh list
    } catch (err) {
      alert('删除分区失败。');
      console.error(err);
    }
  }
};

const startEdit = (category) => {
    editingCategoryId.value = category.categoryId;
    editingCategoryName.value = category.categoryName;
};

const cancelEdit = () => {
    editingCategoryId.value = null;
    editingCategoryName.value = '';
};

const saveEdit = async (category) => {
    if (!editingCategoryName.value.trim()) {
        alert('分区名称不能为空。');
        return;
    }
    try {
        const payload = {
            ...category, // includes categoryId and merchantId
            categoryName: editingCategoryName.value
        };
        await axios.put('/api/categories', payload);
        cancelEdit();
        await fetchCategories();
    } catch (err) {
        alert('更新分区失败，可能与其他分区名称重复。');
        console.error(err);
    }
};

const moveUp = (index) => {
    if (index === 0) return;
    const temp = categories.value[index];
    categories.value[index] = categories.value[index - 1];
    categories.value[index - 1] = temp;
    isOrderDirty.value = true;
};

const moveDown = (index) => {
    if (index === categories.value.length - 1) return;
    const temp = categories.value[index];
    categories.value[index] = categories.value[index + 1];
    categories.value[index + 1] = temp;
    isOrderDirty.value = true;
};

const saveOrder = async () => {
    const orderedIds = categories.value.map(c => c.categoryId);
    try {
        await axios.put('/api/categories/reorder', orderedIds);
        alert('顺序已保存！');
        isOrderDirty.value = false;
    } catch (err) {
        alert('保存顺序失败。');
        console.error('Error saving order:', err);
    }
};

</script>
