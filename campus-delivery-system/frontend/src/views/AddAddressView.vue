<template>
  <div>
    <h1>添加新地址</h1>
    <form @submit.prevent="addAddress">
      <div class="mb-3">
        <label for="addressDetails" class="form-label">详细地址</label>
        <textarea class="form-control" id="addressDetails" v-model="address.addressDetails" rows="3" required></textarea>
      </div>
      <div class="mb-3 form-check">
        <input type="checkbox" class="form-check-input" id="isDefault" v-model="address.default">
        <label class="form-check-label" for="isDefault">设为默认地址</label>
      </div>
      <button type="submit" class="btn btn-primary">确认</button>
      <router-link to="/addresses" class="btn btn-secondary ms-2">取消</router-link>
    </form>
    <div v-if="error" class="alert alert-danger mt-3">{{ error }}</div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';
import userStore from '../stores/userStore';

const router = useRouter();
const address = ref({
  addressDetails: '',
  isDefault: false,
  userId: userStore.state.userId,
});
const error = ref(null);

onMounted(() => {
  if (userStore.state.userRole !== 'user') {
    router.push('/'); // Redirect if not a user
  }
});

const addAddress = async () => {
  if (!address.value.userId) {
    error.value = '无法添加地址，未选择用户。';
    return;
  }
  try {
    // If setting this as the new default, we may need to unset the old one first.
    // For simplicity, the backend should handle this logic. If not, this can lead to multiple defaults.
    // Based on AddressDAO, it doesn't, so we'll proceed and recommend a backend fix later.
    await axios.post('/api/addresses', address.value);
    router.push('/addresses');
  } catch (err) {
    error.value = '添加地址失败。';
    console.error('Error adding address:', err);
  }
};
</script>
