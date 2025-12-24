<template>
  <div class="container mt-4">
    <!-- 1. 加载状态 -->
    <div v-if="loading" class="text-center">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">加载中...</span>
      </div>
      <p class="mt-2">加载商家数据中...</p>
    </div>

    <!-- 2. 错误处理 -->
    <div v-else-if="error" class="alert alert-danger">
      {{ error }}
      <router-link to="/" class="btn btn-sm btn-outline-danger ms-3">返回首页</router-link>
    </div>

    <!-- 3. 主内容区（只有当 merchant 加载成功后才显示） -->
    <div v-else-if="merchant">
      <h2 class="mb-4">编辑商家信息</h2>

      <!-- 营业状态切换 -->
      <div class="card mb-4">
        <div class="card-body">
          <h5 class="card-title">营业状态</h5>
          <p>当前状态: 
            <span class="badge" :class="merchant.open ? 'bg-success' : 'bg-secondary'">
              {{ merchant.open ? '营业中' : '已打烊' }}
            </span>
          </p>
          <button @click="toggleManualStatus" class="btn" :class="merchant.isManuallyClosed ? 'btn-success' : 'btn-danger'">
            {{ merchant.isManuallyClosed ? '开始营业' : '立即打烊' }}
          </button>
          
          <template v-if="!merchant.isManuallyClosed">
            <small class="form-text text-muted d-block mt-2">
              店铺将根据您设定的营业时间自动开关。
            </small>
          </template>
          <template v-else>
            <small class="form-text text-muted d-block mt-2">
              店铺已手动打烊，将不会自动开始营业。
            </small>
          </template>
        </div>
      </div>
      
      <!-- 营业时间设置 -->
      <div class="card mb-4">
        <div class="card-body">
          <h5 class="card-title">营业时间设置</h5>
          <form @submit.prevent="updateBusinessHours" class="row align-items-end">
            <div class="col-md-5">
              <label for="openTime" class="form-label">开始营业时间</label>
              <input type="time" class="form-control" id="openTime" v-model="hours.openTime">
            </div>
            <div class="col-md-5">
              <label for="closeTime" class="form-label">打烊时间</label>
              <input type="time" class="form-control" id="closeTime" v-model="hours.closeTime">
            </div>
            <div class="col-md-2">
              <button type="submit" class="btn btn-primary w-100">保存时间</button>
            </div>
          </form>
        </div>
      </div>

      <!-- 基本信息表单 -->
      <div class="card">
        <div class="card-body">
          <h5 class="card-title">基本信息 (仅管理员可修改)</h5>
          <form @submit.prevent="updateMerchant">
            <fieldset :disabled="userStore.state.userRole !== 'admin'">
              <div class="mb-3">
                <label for="name" class="form-label">商家名称</label>
                <input type="text" class="form-control" id="name" v-model="merchant.name" required>
              </div>
              <div class="mb-3">
                <label for="address" class="form-label">地址</label>
                <input type="text" class="form-control" id="address" v-model="merchant.address" required>
              </div>
              <div class="mb-3">
                <label for="phone" class="form-label">电话号码</label>
                <input type="text" class="form-control" id="phone" v-model="merchant.phoneNumber">
              </div>
              <button v-if="userStore.state.userRole === 'admin'" type="submit" class="btn btn-primary">更新基本信息</button>
            </fieldset>
          </form>
        </div>
      </div>

      <router-link to="/" class="btn btn-secondary mt-3">返回</router-link>
    </div> <!-- 对应 v-else-if="merchant" 的闭合 -->
  </div> <!-- 对应 container 的闭合 -->
</template>

<script setup>
import { ref, onMounted, defineProps } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';
import userStore from '../stores/userStore';

const props = defineProps({
  id: {
    type: String,
    required: true
  }
});

const router = useRouter();
const merchant = ref(null);
const loading = ref(true);
const error = ref(null);

const hours = ref({
    openTime: '09:00',
    closeTime: '22:00'
});

const fetchMerchantData = async () => {
    try {
        const response = await axios.get(`/api/merchants/${props.id}`);
        merchant.value = response.data;
        console.log('Data Refreshed. New "isOpen" status:', merchant.value.open, 'New "isManuallyClosed" status:', merchant.value.isManuallyClosed);

        // Initialize hours form with fetched data
        if (merchant.value.openTime) {
            hours.value.openTime = merchant.value.openTime.substring(0, 5);
        }
        if (merchant.value.closeTime) {
            hours.value.closeTime = merchant.value.closeTime.substring(0, 5);
        }
    } catch (err) {
        error.value = '加载商家数据失败。';
        console.error('Error fetching merchant:', err);
    } finally {
        loading.value = false;
    }
};

onMounted(async () => {
  const merchantId = parseInt(props.id);
  const { userRole, merchantId: storeMerchantId } = userStore.state;
  
  if (userRole === 'admin' || (userRole === 'merchant' && storeMerchantId === merchantId)) {
    await fetchMerchantData();
  } else {
    error.value = "无权访问此页面。";
    router.push('/');
  }
});

const updateMerchant = async () => {
  if (!merchant.value || userStore.state.userRole !== 'admin') return;
  try {
    const payload = {
        merchantId: merchant.value.merchantId,
        name: merchant.value.name,
        address: merchant.value.address,
        phoneNumber: merchant.value.phoneNumber
    };
    await axios.put('/api/merchants', payload);
    alert('基本信息更新成功！');
    router.push('/');
  } catch (err) {
    alert('更新商家失败。');
  }
};

const updateBusinessHours = async () => {
    try {
        await axios.put(`/api/merchants/${props.id}/hours`, hours.value);
        alert('营业时间更新成功！');
        await fetchMerchantData();
    } catch(err) {
        alert('更新营业时间失败。');
    }
};

const toggleManualStatus = async () => {
    try {
        const newStatus = !merchant.value.isManuallyClosed;
        await axios.put(`/api/merchants/${props.id}/status`, { isClosed: newStatus });
        alert(`店铺状态已更新为: ${newStatus ? '手动打烊' : '自动营业'}`);
        await fetchMerchantData();
    } catch(err) {
        alert('更新状态失败。');
    }
}
</script>