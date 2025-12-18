import { reactive, computed } from 'vue';
import userStore from './userStore';

// The store for the shopping cart
const state = reactive({
  items: [], // Array to hold { dish, quantity }
  merchantId: null, // To ensure all items are from the same merchant
  merchantName: '', // To display in the cart
});

// Helper function to find an item in the cart
const findItem = (dishId) => state.items.find(item => item.dish.dishId === dishId);

// Function to calculate the total price
const total = computed(() => {
  return state.items.reduce((acc, item) => acc + item.dish.price * item.quantity, 0).toFixed(2);
});

// Function to add a dish to the cart
const addToCart = (dish, merchant) => {
  // If the new item is from a different merchant, clear the cart first
  if (state.merchantId !== null && state.merchantId !== merchant.merchantId) {
    if (!confirm(`You have items from ${state.merchantName}. Do you want to clear the cart and add items from ${merchant.name}?`)) {
      return; // User cancelled
    }
    clearCart();
  }

  // Set merchant if it's the first item
  if (state.items.length === 0) {
    state.merchantId = merchant.merchantId;
    state.merchantName = merchant.name;
  }
  
  const existingItem = findItem(dish.dishId);
  if (existingItem) {
    existingItem.quantity++;
  } else {
    state.items.push({ dish: { ...dish }, quantity: 1 });
  }
};

// Function to remove an item from the cart
const removeFromCart = (dishId) => {
  const index = state.items.findIndex(item => item.dish.dishId === dishId);
  if (index !== -1) {
    state.items.splice(index, 1);
  }
  // If the cart becomes empty, reset the merchant
  if (state.items.length === 0) {
    clearCart();
  }
};

// Function to update the quantity of an item
const updateQuantity = (dishId, quantity) => {
  const item = findItem(dishId);
  if (item) {
    if (quantity > 0) {
      item.quantity = quantity;
    } else {
      removeFromCart(dishId);
    }
  }
};

// Function to clear the cart
const clearCart = () => {
  state.items = [];
  state.merchantId = null;
  state.merchantName = '';
};

// Export all the functions and state
export default {
  state,
  total,
  addToCart,
  removeFromCart,
  updateQuantity,
  clearCart,
};
