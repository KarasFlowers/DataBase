import { createRouter, createWebHistory } from 'vue-router'
import MerchantListView from '../views/MerchantListView.vue'
import DishListView from '../views/DishListView.vue'
import OrderListView from '../views/OrderListView.vue'
import AddMerchantView from '../views/AddMerchantView.vue'
import AddDishView from '../views/AddDishView.vue'
import CartView from '../views/CartView.vue'
import CheckoutView from '../views/CheckoutView.vue'
import AddressListView from '../views/AddressListView.vue'
import AddAddressView from '../views/AddAddressView.vue'
import EditAddressView from '../views/EditAddressView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'merchants',
      component: MerchantListView
    },
    {
      path: '/merchants/add',
      name: 'add-merchant',
      component: AddMerchantView
    },
    {
      path: '/merchants/:id/edit',
      name: 'edit-merchant',
      component: () => import('../views/EditMerchantView.vue'),
      props: true
    },
    {
      path: '/merchant/:id/dishes',
      name: 'dishes',
      component: DishListView,
      props: true
    },
    {
      path: '/merchant/:merchantId/dishes/add',
      name: 'add-dish',
      component: AddDishView,
      props: true
    },
    {
      path: '/dishes/:dishId/edit',
      name: 'edit-dish',
      component: () => import('../views/EditDishView.vue'), // Lazy load for variety
      props: true
    },
    {
      path: '/orders',
      name: 'orders',
      component: OrderListView
    },
    {
      path: '/orders/:orderId',
      name: 'order-details',
      component: () => import('../views/OrderDetailView.vue'),
      props: true
    },
    {
      path: '/users',
      name: 'users',
      component: () => import('../views/UserListView.vue')
    },
    {
      path: '/users/add',
      name: 'add-user',
      component: () => import('../views/AddUserView.vue')
    },
    {
      path: '/users/:id/edit',
      name: 'edit-user',
      component: () => import('../views/EditUserView.vue'),
      props: true
    },
    // --- New Routes for Cart & Checkout ---
    {
      path: '/cart',
      name: 'cart',
      component: CartView
    },
    {
      path: '/checkout',
      name: 'checkout',
      component: CheckoutView
    },
    // --- New Routes for Address Management ---
    {
      path: '/addresses',
      name: 'address-list',
      component: AddressListView
    },
    {
      path: '/addresses/add',
      name: 'add-address',
      component: AddAddressView
    },
    {
      path: '/addresses/:id/edit',
      name: 'edit-address',
      component: EditAddressView,
      props: true
    },
    // --- New Routes for Rider Management ---
    {
      path: '/riders',
      name: 'rider-list',
      component: () => import('../views/RiderListView.vue')
    },
    {
      path: '/riders/add',
      name: 'add-rider',
      component: () => import('../views/AddRiderView.vue')
    },
    {
        path: '/riders/:id/edit',
        name: 'edit-rider',
        component: () => import('../views/EditRiderView.vue'),
        props: true
    },
    // --- New Route for Admin Dashboard ---
    {
        path: '/admin-dashboard',
        name: 'admin-dashboard',
        component: () => import('../views/AdminDashboardView.vue')
    },
    // --- New Route for Rider Dashboard ---
    {
        path: '/rider-dashboard',
        name: 'rider-dashboard',
        component: () => import('../views/RiderDashboardView.vue')
    },
    // --- New Route for Merchant Dashboard ---
    {
        path: '/merchant-dashboard',
        name: 'merchant-dashboard',
        component: () => import('../views/MerchantDashboardView.vue')
    }
  ]
})

export default router
