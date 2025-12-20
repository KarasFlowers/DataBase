<template>
  <div class="checkout-view">
    <h1>结算</h1>

    <div v-if="loading" class="alert alert-info">正在加载...</div>
    <div v-else-if="error" class="alert alert-danger">{{ error }}</div>

    <div v-else-if="cartStore.state.items.length > 0">
      <!-- Order Summary -->
      <div class="card mb-4">
        <div class="card-header">
          <h4>订单概览</h4>
          <p>来自: <strong>{{ cartStore.state.merchantName }}</strong></p>
        </div>
        <ul class="list-group list-group-flush">
          <li v-for="item in cartStore.state.items" :key="item.dish.dishId" class="list-group-item d-flex justify-content-between">
            <span>{{ item.dish.name }} x {{ item.quantity }}</span>
            <span>¥{{ (item.dish.price * item.quantity).toFixed(2) }}</span>
          </li>
        </ul>
        <div class="card-footer d-flex justify-content-end">
          <h4>总计: ¥{{ cartStore.total.value }}</h4>
        </div>
      </div>

      <!-- Address Selection -->
      <div class="card mb-4">
        <div class="card-header">
          <h4>选择收货地址</h4>
        </div>
        <div class="card-body">
          <div v-if="addresses.length > 0">
            <div v-for="address in addresses" :key="address.addressId" class="form-check">
              <input class="form-check-input" type="radio" name="address" :id="`addr-${address.addressId}`" :value="address.addressId" v-model="selectedAddressId">
              <label class="form-check-label" :for="`addr-${address.addressId}`">
                {{ address.addressDetails }}
              </label>
            </div>
          </div>
          <div v-else class="alert alert-warning">
            未找到地址。请在下单前添加一个地址。
            <router-link to="/addresses/add" class="btn btn-sm btn-primary mt-2">添加地址</router-link>
          </div>
        </div>
      </div>

      <!-- Place Order Button -->
      <div class="d-grid gap-2">
        <button @click="placeOrder" class="btn btn-success btn-lg" :disabled="!selectedAddressId || isPlacingOrder">
          <span v-if="isPlacingOrder" class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
          {{ isPlacingOrder ? '正在下单...' : '确认下单' }}
        </button>
      </div>
    </div>

    <div v-else>
       <div class="alert alert-warning">您的购物车是空的，先去选购吧！</div>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import userStore from '../stores/userStore';
import cartStore from '../stores/cartStore';

const router = useRouter();
const addresses = ref([]);
const selectedAddressId = ref(null);
const loading = ref(true);
const error = ref(null);
const isPlacingOrder = ref(false);

onMounted(async () => {
  // Redirect if cart is empty
  if (cartStore.state.items.length === 0) {
    router.push('/cart');
    return;
  }
  
  if (userStore.state.userRole !== 'user' || !userStore.state.userId) {
    error.value = '您必须以用户身份登录才能下单。';
    loading.value = false;
    return;
  }
  
  await fetchAddresses();
  loading.value = false;
});

const fetchAddresses = async () => {
  try {
    const response = await axios.get(`/api/addresses/user/${userStore.state.userId}`);
    addresses.value = response.data;
    // Select the default address or the first one
    const defaultAddress = addresses.value.find(a => a.isDefault) || addresses.value[0];
    if (defaultAddress) {
      selectedAddressId.value = defaultAddress.addressId;
    }
  } catch (err) {
    error.value = '获取地址失败。';
    console.error('Error fetching addresses:', err);
  }
};

// This is a helper to extract the ID from the backend's plain text response.
const extractOrderId = (responseText) => {
    const match = responseText.match(/ID: (\d+)/);
    return match ? parseInt(match[1], 10) : null;
};

const placeOrder = async () => {
  if (!selectedAddressId.value) {
    alert('请选择一个收货地址。');
    return;
  }
  
  isPlacingOrder.value = true;
  error.value = null;

  try {
    // Step 1: Create the main order
    const orderPayload = {
      userId: userStore.state.userId,
      merchantId: cartStore.state.merchantId,
      addressId: selectedAddressId.value,
      totalPrice: cartStore.total.value,
    };
    const orderResponse = await axios.post('/api/orders', orderPayload);
    const newOrderId = extractOrderId(orderResponse.data);

    if (!newOrderId) {
        throw new Error("无法从服务器响应中获取订单ID。");
    }
    
    // Step 2: Create the order details
    const detailPromises = cartStore.state.items.map(item => {
      const detailPayload = {
        orderId: newOrderId,
        dishId: item.dish.dishId,
        quantity: item.quantity,
        pricePerItem: item.dish.price
      };
      return axios.post('/api/orderdetails', detailPayload);
    });

    await Promise.all(detailPromises);

    // Step 3: Success
    alert('下单成功！请前往支付页面完成支付。');
    cartStore.clearCart();
    router.push({ name: 'pay-order', params: { orderId: newOrderId } });

  } catch (err) {
    error.value = '下单失败，请重试。';
    console.error('Error placing order:', err);
  } finally {
    isPlacingOrder.value = false;
  }
};

</script>

<style scoped>
.checkout-view {
  max-width: 800px;
  margin: auto;
}
</style>
