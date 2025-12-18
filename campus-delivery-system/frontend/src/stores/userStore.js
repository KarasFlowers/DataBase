import { reactive, readonly } from 'vue';

const state = reactive({
  userRole: 'admin', // Default role
  userId: null,
  merchantId: null,
  riderId: null,
});

const setUserRole = (role, id = null) => {
  state.userRole = role;
  state.userId = null;
  state.merchantId = null;
  state.riderId = null;
  
  if (role === 'user') {
    state.userId = id;
  } else if (role === 'merchant') {
    state.merchantId = id;
  } else if (role === 'rider') {
    state.riderId = id;
  }
  // No ID needed for admin
};

export default {
  state: readonly(state), // Make state readonly for external components
  setUserRole,
};
