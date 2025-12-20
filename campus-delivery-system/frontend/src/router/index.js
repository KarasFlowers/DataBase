import { createRouter, createWebHistory } from 'vue-router';
import userStore from '../stores/userStore';

import LoginView from '../views/LoginView.vue';
import MerchantListView from '../views/MerchantListView.vue';
import DishListView from '../views/DishListView.vue';
import OrderListView from '../views/OrderListView.vue';
import AddMerchantView from '../views/AddMerchantView.vue';
import AddDishView from '../views/AddDishView.vue';
import CartView from '../views/CartView.vue';
import CheckoutView from '../views/CheckoutView.vue';
import AddressListView from '../views/AddressListView.vue';
import AddAddressView from '../views/AddAddressView.vue';
import EditAddressView from '../views/EditAddressView.vue';

const routes = [
    {
      path: '/login',
      name: 'login',
      component: LoginView
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/RegisterView.vue')
    },
    {
      path: '/',
      name: 'merchants',
      component: MerchantListView,
      meta: { requiresAuth: true }
    },
    {
      path: '/merchants/add',
      name: 'add-merchant',
      component: AddMerchantView,
      meta: { requiresAuth: true }
    },
    {
      path: '/merchants/:id/edit',
      name: 'edit-merchant',
      component: () => import('../views/EditMerchantView.vue'),
      props: true,
      meta: { requiresAuth: true }
    },
    {
      path: '/merchant/:id/dishes',
      name: 'dishes',
      component: DishListView,
      props: true,
      meta: { requiresAuth: true }
    },
    {
      path: '/merchant/:merchantId/dishes/add',
      name: 'add-dish',
      component: AddDishView,
      props: true,
      meta: { requiresAuth: true }
    },
    {
      path: '/dishes/:dishId/edit',
      name: 'edit-dish',
      component: () => import('../views/EditDishView.vue'),
      props: true,
      meta: { requiresAuth: true }
    },
    {
      path: '/orders',
      name: 'orders',
      component: OrderListView,
      meta: { requiresAuth: true }
    },
    {
      path: '/orders/:orderId',
      name: 'order-details',
      component: () => import('../views/OrderDetailView.vue'),
      props: true,
      meta: { requiresAuth: true }
    },
    {
      path: '/orders/:orderId/pay',
      name: 'pay-order',
      component: () => import('../views/PaymentView.vue'),
      props: true,
      meta: { requiresAuth: true }
    },
    {
      path: '/users',
      name: 'users',
      component: () => import('../views/UserListView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/users/add',
      name: 'add-user',
      component: () => import('../views/AddUserView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/users/:id/edit',
      name: 'edit-user',
      component: () => import('../views/EditUserView.vue'),
      props: true,
      meta: { requiresAuth: true }
    },
    {
      path: '/cart',
      name: 'cart',
      component: CartView,
      meta: { requiresAuth: true }
    },
    {
      path: '/checkout',
      name: 'checkout',
      component: CheckoutView,
      meta: { requiresAuth: true }
    },
    {
      path: '/addresses',
      name: 'address-list',
      component: AddressListView,
      meta: { requiresAuth: true }
    },
    {
      path: '/addresses/add',
      name: 'add-address',
      component: AddAddressView,
      meta: { requiresAuth: true }
    },
    {
      path: '/addresses/:id/edit',
      name: 'edit-address',
      component: EditAddressView,
      props: true,
      meta: { requiresAuth: true }
    },
    {
      path: '/riders',
      name: 'rider-list',
      component: () => import('../views/RiderListView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/riders/add',
      name: 'add-rider',
      component: () => import('../views/AddRiderView.vue'),
      meta: { requiresAuth: true }
    },
    {
        path: '/riders/:id/edit',
        name: 'edit-rider',
        component: () => import('../views/EditRiderView.vue'),
        props: true,
        meta: { requiresAuth: true }
    },
    {
        path: '/admin-dashboard',
        name: 'admin-dashboard',
        component: () => import('../views/AdminDashboardView.vue'),
        meta: { requiresAuth: true }
    },
    {
      path: '/admin/reviews',
      name: 'admin-reviews',
      component: () => import('../views/AdminReviewView.vue'),
      meta: { requiresAuth: true }
    },
    // --- New Route for Rider Dashboard ---
    {
        path: '/rider-dashboard',
        name: 'rider-dashboard',
        component: () => import('../views/RiderDashboardView.vue'),
        meta: { requiresAuth: true }
    },
    {
      path: '/category-management',
      name: 'category-management',
      component: () => import('../views/CategoryManagementView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/merchant-reviews',
      name: 'merchant-reviews',
      component: () => import('../views/MerchantReviewsView.vue'),
      meta: { requiresAuth: true }
    },
    // --- New Route for Merchant Dashboard ---
    {
        path: '/merchant-dashboard',
        name: 'merchant-dashboard',
        component: () => import('../views/MerchantDashboardView.vue'),
        meta: { requiresAuth: true }
    },
    {
      path: '/my-reviews',
      name: 'my-reviews',
      component: () => import('../views/MyReviewsView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/reviews/:id/edit',
      name: 'edit-review',
      component: () => import('../views/EditReviewView.vue'),
      props: true,
      meta: { requiresAuth: true }
    }
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
});

// Global navigation guard
router.beforeEach((to, from, next) => {
  const isLoggedIn = !!userStore.state.userRole;
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth);

  if (requiresAuth && !isLoggedIn) {
    // Redirect to login page if trying to access a protected route without being logged in
    next({ name: 'login' });
  } else if (to.name === 'login' && isLoggedIn) {
    // Redirect to home if trying to access login page while already logged in
    next({ name: 'merchants' });
  } else {
    // Otherwise, allow navigation
    next();
  }
});

export default router;
