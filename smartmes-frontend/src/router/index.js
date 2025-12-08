import { createRouter, createWebHistory } from 'vue-router'

const routes = [
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

// 路由守卫 - 设置页面标题
router.beforeEach((to, from, next) => {
  if (to.meta.title) {
    document.title = `${to.meta.title} - SmartMES Lite`
  }
  next()
})

export default router
