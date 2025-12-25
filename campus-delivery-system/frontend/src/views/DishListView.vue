<template>
  <div class="dish-list">
    <div v-if="loading" class="alert alert-info">正在加载...</div>
    <div v-if="error" class="alert alert-danger">{{ error }}</div>

    <div v-if="merchant">
      <div class="d-flex justify-content-between align-items-center mb-3">
        <div>
          <h1 class="d-inline-block me-3">{{ merchant.name }}</h1>
          <span class="badge fs-5" :class="merchant.open ? 'bg-success' : 'bg-secondary'">
              {{ merchant.open ? '营业中' : '已打烊' }}
          </span>
          <p class="mb-0 text-muted">地址: {{ merchant.address }} | 评分: {{ merchant.rating }}</p>
        </div>
        <div class="btn-group" v-if="userStore.state.userRole === 'admin' || (userStore.state.userRole === 'merchant' && userStore.state.merchantId === merchant.merchantId)">
            <router-link :to="{ name: 'edit-merchant', params: { id: merchant.merchantId } }" class="btn btn-success">店铺设置</router-link>
            <router-link :to="{ name: 'category-management' }" class="btn btn-info">管理分区</router-link>
            <router-link :to="{ name: 'add-dish', params: { merchantId: merchant.merchantId } }" class="btn btn-primary">添加菜品</router-link>
        </div>
      </div>

      <div v-if="!merchant.open" class="alert alert-warning text-center">
          <h5>本店已打烊，暂不接单。</h5>
      </div>

      <div v-if="dishes.length > 0">
        <div v-for="group in groupedDishes" :key="group.categoryId" class="mb-4">
            <h3 class="border-bottom pb-2 mb-3">{{ group.categoryName }}</h3>
            <div class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
                 <div class="col" v-for="dish in group.dishes" :key="dish.dishId">
                    <div class="card h-100 shadow-sm" :class="{ 'bg-secondary bg-opacity-25': !dish.available }">
                        <div class="card-body d-flex flex-column">
                            <div class="d-flex justify-content-between align-items-baseline mb-2">
                                <h5 class="card-title mb-0 fw-bold">{{ dish.name }}</h5>
                                <span class="text-primary fs-5 fw-bold">¥{{ dish.price }}</span>
                            </div>
                            <p class="card-text text-muted flex-grow-1">{{ dish.description }}</p>
                            <div class="d-flex justify-content-between align-items-center mt-2 gap-2">
                                <div class="d-flex align-items-center"> <!-- New div to group badges -->
                                    <span class="badge" :class="dish.available ? 'bg-success' : 'bg-danger'">
                                    {{ dish.available ? '在售' : '售罄' }}
                                    </span>
                                    <span v-if="dish.purchaseLimit && dish.purchaseLimit > 0" class="badge bg-info ms-1">
                                        限购 {{ dish.purchaseLimit }} 件
                                    </span>
                                </div>
                                <!-- Action Buttons -->
                                <div v-if="userStore.state.userRole === 'admin' || (userStore.state.userRole === 'merchant' && userStore.state.merchantId === merchant.merchantId)">
                                    <div class="btn-group" role="group">
                                        <router-link :to="{ name: 'edit-dish', params: { dishId: dish.dishId } }" class="btn btn-secondary btn-sm">编辑</router-link>
                                        <button @click="deleteDish(dish.dishId)" class="btn btn-danger btn-sm">删除</button>
                                    </div>
                                </div>
                                <!-- Customer Add-to-cart controls -->
                                <div v-if="userStore.state.userRole === 'user'">
                                    <div v-if="getQuantityInCart(dish.dishId) === 0">
                                        <button @click="handleInitialAddToCart(dish)" class="btn btn-primary btn-sm" :disabled="!dish.available || !merchant.open">
                                            加入购物车
                                        </button>
                                    </div>
                                    <div v-else class="quantity-selector-compact">
                                        <button @click="decrementInCart(dish)" class="btn btn-outline-secondary btn-sm" :disabled="!dish.available || !merchant.open">-</button>
                                        <span class="quantity-text">{{ getQuantityInCart(dish.dishId) }}</span>
                                        <button @click="incrementInCart(dish)" class="btn btn-outline-secondary btn-sm" :disabled="!dish.available || !merchant.open || (dish.purchaseLimit && getQuantityInCart(dish.dishId) >= dish.purchaseLimit)">+</button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
      </div>
      <div v-if="!loading && dishes.length === 0" class="alert alert-warning">
        该商家暂未上架任何菜品。
      </div>
    </div>

    <button @click="goBack" class="btn btn-secondary mt-3">返回商家列表</button>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';
import cartStore from '../stores/cartStore';
import userStore from '../stores/userStore';

const route = useRoute();
const router = useRouter();

const merchant = ref(null);
const dishes = ref([]);
const categories = ref([]);
const loading = ref(true);
const error = ref(null);

const groupedDishes = computed(() => {
  if (!dishes.value) return [];
  
  const sortedDishes = [...dishes.value].sort((a, b) => {
      if (a.available === b.available) return 0;
      return a.available ? -1 : 1;
  });

  let groups = categories.value.map(category => ({
    ...category,
    dishes: sortedDishes.filter(dish => dish.categoryId === category.categoryId)
  }));

  const uncategorizedDishes = sortedDishes.filter(dish => !dish.categoryId);
  if (uncategorizedDishes.length > 0) {
    groups.push({
      categoryId: 'uncategorized',
      categoryName: '未分区菜品',
      dishes: uncategorizedDishes
    });
  }

  return groups.filter(group => group.dishes.length > 0);
});

const fetchDataForMerchant = async (merchantId) => {
  loading.value = true;
  error.value = null;
  try {
    const [merchantRes, dishesRes, categoriesRes] = await Promise.all([
        axios.get(`/api/merchants/${merchantId}`),
        axios.get(`/api/dishes/merchant/${merchantId}`),
        axios.get(`/api/categories/merchant/${merchantId}`)
    ]);
    
    merchant.value = merchantRes.data;
    dishes.value = dishesRes.data;
    categories.value = categoriesRes.data;
  } catch (err) {
    error.value = '加载数据失败，请确认后端服务是否运行以及商家ID是否正确。';
    console.error('Error fetching data:', err);
  } finally {
    loading.value = false;
  }
};

const getQuantityInCart = (dishId) => {
    const item = cartStore.findItem(dishId);
    return item ? item.quantity : 0;
};

const handleInitialAddToCart = (dish) => {
  if (!dish.available || !merchant.value.open) {
      if (!merchant.value.open) alert('商家已打烊，无法添加商品。');
      return;
  }
  // Check purchase limit for the very first item
  if (dish.purchaseLimit && 1 > dish.purchaseLimit) {
      alert(`抱歉，该商品每单限购 ${dish.purchaseLimit} 件。`);
      return;
  }
  cartStore.addToCart(dish, merchant.value, 1);
};

const incrementInCart = (dish) => {
    const currentQuantity = getQuantityInCart(dish.dishId);
    if (dish.purchaseLimit && currentQuantity + 1 > dish.purchaseLimit) {
        alert(`抱歉，该商品每单限购 ${dish.purchaseLimit} 件。`);
        return;
    }
    cartStore.updateQuantity(dish.dishId, currentQuantity + 1);
};

const decrementInCart = (dish) => {
    const currentQuantity = getQuantityInCart(dish.dishId);
    cartStore.updateQuantity(dish.dishId, currentQuantity - 1); // store handles removal if quantity is 0
};

onMounted(() => {
  fetchDataForMerchant(route.params.id);
});

watch(() => route.params.id, (newId, oldId) => {
    if (newId && newId !== oldId) {
      fetchDataForMerchant(newId);
    }
});

const deleteDish = async (dishId) => {
  if (confirm('您确定要删除此菜品吗？')) {
    try {
      await axios.delete(`/api/dishes/${dishId}`);
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
.quantity-selector-compact {
    display: flex;
    align-items: center;
}
.quantity-selector-compact .quantity-text {
    width: 30px;
    text-align: center;
    font-weight: bold;
}
p {
  margin-bottom: 0.5rem;
}
</style>
