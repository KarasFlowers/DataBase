import { reactive, readonly } from 'vue';
import axios from 'axios';

// 1. Define the default logged-out state
const defaultState = {
  userRole: null,
  userId: null,
  username: null,
  merchantId: null,
  riderId: null,
};

// 2. Try to load state from sessionStorage on initial load
let initialState;
try {
  const storedState = sessionStorage.getItem('userSession');
  if (storedState) {
    initialState = JSON.parse(storedState);
    // Basic validation
    if (typeof initialState !== 'object' || initialState === null) {
        throw new Error("Invalid state in sessionStorage");
    }
  } else {
    initialState = { ...defaultState };
  }
} catch (e) {
  console.warn("Could not rehydrate state, using default.", e);
  initialState = { ...defaultState };
}

const state = reactive(initialState);

// 3. Login action
const login = async (username, password) => {
    const response = await axios.post('/api/auth/login', { username, password });
    if (response.data && response.data.userId) {
        // Update state with user data from backend
        state.userRole = response.data.role;
        state.userId = response.data.userId;
        state.username = response.data.username;
        // The entity_id from backend corresponds to merchantId or riderId
        if (response.data.role === 'merchant') {
            state.merchantId = response.data.entityId;
        } else if (response.data.role === 'rider') {
            state.riderId = response.data.entityId;
        } else {
            state.merchantId = null;
            state.riderId = null;
        }
        
        // Persist the new state
        sessionStorage.setItem('userSession', JSON.stringify(state));
    } else {
        // This case should ideally not be reached if backend returns error statuses
        throw new Error("Login failed: Invalid data returned from server.");
    }
};

// 4. Logout action
const logout = () => {
    Object.assign(state, defaultState); // Reset state to default
    sessionStorage.removeItem('userSession');
};

export default {
  state: readonly(state),
  login,
  logout,
};