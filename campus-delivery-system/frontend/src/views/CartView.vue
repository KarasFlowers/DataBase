<template>
  <div class="cart-view">
    <h1>购物车</h1>
    <div v-if="cartStore.state.items.length === 0" class="alert alert-info">
      您还没有选择菜品。 <router-link to="/">看看吃啥！</router-link>
    </div>

    <div v-else>
      <div class="card mb-3">
        <div class="card-header">
          来自 <strong>{{ cartStore.state.merchantName }}</strong> 的商品
        </div>
        <ul class="list-group list-group-flush">
          <li v-for="item in cartStore.state.items" :key="item.dish.dishId" class="list-group-item d-flex justify-content-between align-items-center">
            <div>
              <h5>{{ item.dish.name }}</h5>
              <p class="mb-0 text-muted">价格: ¥{{ item.dish.price }}</p>
            </div>
            <div class="d-flex align-items-center">
              <div class="input-group input-group-sm" style="width: 120px;">
                <button class="btn btn-outline-secondary" type="button" @click="updateQuantity(item.dish.dishId, item.quantity - 1)">-</button>
                <input type="text" class="form-control text-center" :value="item.quantity" readonly>
                <button class="btn btn-outline-secondary" type="button" @click="updateQuantity(item.dish.dishId, item.quantity + 1)">+</button>
              </div>
              <button @click="removeFromCart(item.dish.dishId)" class="btn btn-danger btn-sm ms-3">移除</button>
            </div>
          </li>
        </ul>
        <div class="card-footer d-flex justify-content-end align-items-center">
          <h4>总计: ¥{{ cartStore.total.value }}</h4>
        </div>
      </div>
      <div class="d-flex justify-content-between">
        <button @click="clearCart" class="btn btn-outline-danger">清空购物车</button>
        <button @click="goToCheckout" class="btn btn-primary">去结算</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router';
import cartStore from '../stores/cartStore';

const router = useRouter();

const updateQuantity = (dishId, newQuantity) => {
  cartStore.updateQuantity(dishId, newQuantity);
};

const removeFromCart = (dishId) => {
  if (confirm('您确定要移除该菜品吗？')) {
    cartStore.removeFromCart(dishId);
  }
};

const clearCart = () => {
    if (confirm('您确定要清空购物车吗？')) {
        cartStore.clearCart();
    }
}

const goToCheckout = () => {
  router.push('/checkout');
};
</script>

<style scoped>
.cart-view {
  max-width: 800px;
  margin: auto;
}
</style>
