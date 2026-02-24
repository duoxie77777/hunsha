import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/HomeView.vue')
  },
  {
    path: '/services',
    name: 'Services',
    component: () => import('../views/ServicesView.vue')
  },
  {
    path: '/portfolio',
    name: 'Portfolio',
    component: () => import('../views/PortfolioView.vue')
  },
  {
    path: '/pricing',
    name: 'Pricing',
    component: () => import('../views/PricingView.vue')
  },
  {
    path: '/reviews',
    name: 'Reviews',
    component: () => import('../views/ReviewsView.vue')
  },
  {
    path: '/about',
    name: 'About',
    component: () => import('../views/AboutView.vue')
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('../views/ProfileView.vue')
  },
  {
    path: '/admin',
    component: () => import('../views/admin/AdminLayout.vue'),
    children: [
      {
        path: '',
        name: 'AdminDashboard',
        component: () => import('../views/admin/DashboardView.vue')
      },
      {
        path: 'orders',
        name: 'AdminOrders',
        component: () => import('../views/admin/OrdersView.vue')
      },
      {
        path: 'chat',
        name: 'AdminChat',
        component: () => import('../views/admin/AdminChatView.vue')
      },
      {
        path: 'customers',
        name: 'AdminCustomers',
        component: () => import('../views/admin/CustomersView.vue')
      },
      {
        path: 'portfolio',
        name: 'AdminPortfolio',
        component: () => import('../views/admin/PortfolioManageView.vue')
      },
      {
        path: 'packages',
        name: 'AdminPackages',
        component: () => import('../views/admin/PackagesView.vue')
      },
      {
        path: 'schedule',
        name: 'AdminSchedule',
        component: () => import('../views/admin/ScheduleView.vue')
      },
      {
        path: 'reviews',
        name: 'AdminReviews',
        component: () => import('../views/admin/ReviewsManageView.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

export default router
