<template>
  <div class="address-list-view">
    <div class="d-flex justify-content-between align-items-center mb-3">
      <h1>我的地址</h1>
      <router-link to="/addresses/add" class="btn btn-primary">添加新地址</router-link>
    </div>

    <div v-if="loading" class="alert alert-info">正在加载地址...</div>
    <div v-else-if="error" class="alert alert-danger">{{ error }}</div>

    <div v-else-if="addresses.length > 0" class="list-group">
      <div v-for="address in addresses" :key="address.addressId" class="list-group-item d-flex justify-content-between align-items-center">
        <div>
          <p class="mb-1">{{ address.addressDetails }}</p>
          <span v-if="address.default" class="badge bg-success">默认</span>
        </div>
        <div class="btn-group">
           <button v-if="!address.default" @click="setDefault(address)" class="btn btn-outline-secondary btn-sm">设为默认</button>
          <router-link :to="{ name: 'edit-address', params: { id: address.addressId } }" class="btn btn-secondary btn-sm">编辑</router-link>
          <button @click="deleteAddress(address.addressId)" class="btn btn-danger btn-sm">删除</button>
        </div>
      </div>
    </div>
    <div v-else class="alert alert-secondary">
      您还没有添加任何地址。快去设置吧！
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import userStore from '../stores/userStore';

const loading = ref(true);
const error = ref(null);
const addresses = ref([]);

const fetchAddresses = async () => {
  if (!userStore.state.userId) {
    error.value = "请选择一个用户视角以查看地址。";
    loading.value = false;
    return;
  }
  loading.value = true;
  error.value = null;
  try {
    const response = await axios.get(`/api/addresses/user/${userStore.state.userId}`);
    addresses.value = response.data;
  } catch (err) {
    error.value = '加载地址失败。';
    console.error('Error fetching addresses:', err);
  } finally {
    loading.value = false;
  }
};

const deleteAddress = async (addressId) => {
  if (confirm('您确定要删除此地址吗？')) {
    try {
      await axios.delete(`/api/addresses/${addressId}`);
      await fetchAddresses(); // Refresh list
    } catch (err) {
      alert('删除地址失败。');
      console.error('Error deleting address:', err);
    }
  }
};

const setDefault = async (addressToSet) => {
    // This is a bit complex as it requires resetting the old default address.
    // We'll update all addresses for this user.
    const updates = addresses.value.map(addr => {
        const updatedAddr = { ...addr, isDefault: addr.addressId === addressToSet.addressId };
        return axios.put('/api/addresses', updatedAddr);
    });

    try {
        await Promise.all(updates);
        await fetchAddresses(); // Refresh to show the change
    } catch (err) {
        alert('设置默认地址失败。');
        console.error('Error setting default address:', err);
    }
};

onMounted(fetchAddresses);
</script>

