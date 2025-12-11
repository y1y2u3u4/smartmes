import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Auth/LoginView.vue'),
    meta: {
      title: 'Login',
      public: true
    }
  },
  {
    path: '/',
    redirect: '/workorders'
  },
  {
    path: '/workorders',
    name: 'WorkOrderList',
    component: () => import('@/views/WorkOrder/WorkOrderList.vue'),
    meta: {
      title: 'Work Order List',
      breadcrumb: ['Work Order', 'List']
    }
  },
  {
    path: '/workorders/create',
    name: 'CreateWorkOrder',
    component: () => import('@/views/WorkOrder/CreateWorkOrder.vue'),
    meta: {
      title: 'Create Work Order',
      breadcrumb: ['Work Order', 'Create']
    }
  },
  {
    path: '/workorders/:id',
    name: 'WorkOrderDetail',
    component: () => import('@/views/WorkOrder/WorkOrderDetail.vue'),
    meta: {
      title: 'Work Order Detail',
      breadcrumb: ['Work Order', 'Detail']
    }
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('@/views/Dashboard/ProductionDashboard.vue'),
    meta: {
      title: 'Production Dashboard',
      breadcrumb: ['Dashboard']
    }
  },
  {
    path: '/downtime/report',
    name: 'DowntimeReport',
    component: () => import('@/views/Downtime/DowntimeReport.vue'),
    meta: {
      title: 'Downtime Report',
      breadcrumb: ['Downtime', 'Report']
    }
  },
  {
    path: '/downtime/list',
    name: 'DowntimeList',
    component: () => import('@/views/Downtime/DowntimeList.vue'),
    meta: {
      title: 'Downtime List',
      breadcrumb: ['Downtime', 'List']
    }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Route guard - Authentication check
router.beforeEach((to, from, next) => {
  // Set page title
  if (to.meta.title) {
    document.title = `${to.meta.title} - SmartMES Lite`
  }

  // Check if route requires authentication
  const isPublicRoute = to.meta.public === true
  const isAuthenticated = !!localStorage.getItem('access_token')

  if (!isPublicRoute && !isAuthenticated) {
    // Redirect to login with return url
    next({
      path: '/login',
      query: { redirect: to.fullPath }
    })
  } else if (to.path === '/login' && isAuthenticated) {
    // Already logged in, redirect to home
    next('/workorders')
  } else {
    next()
  }
})

export default router
