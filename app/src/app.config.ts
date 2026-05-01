export default defineAppConfig({
  pages: [
    'pages/customer/index/index',
    'pages/customer/cart/cart',
    'pages/customer/my-orders/my-orders',
    'pages/customer/my/my',
    'pages/customer/confirm-order/confirm-order',
  ],

  subpackages: [
    {
      root: 'pages/vendor',
      pages: [
        'my-stall/my-stall',
        'order-manage/order-manage',
        'stall-settings/stall-settings',
      ],
    },
  ],

  window: {
    navigationBarBackgroundColor: '#FF6B35',
    navigationBarTitleText: '摊位商城',
    navigationBarTextStyle: 'white',
    backgroundColor: '#FFF8F0',
    backgroundTextStyle: 'dark',
    enablePullDownRefresh: true,
    backgroundColorBottom: '#FFF8F0',
    backgroundColorTop: '#FFF8F0',
  },

  tabBar: {
    color: '#999999',
    selectedColor: '#FF6B35',
    backgroundColor: '#FFFFFF',
    borderStyle: 'black',
    list: [
      {
        pagePath: 'pages/customer/index/index',
        text: '首页',
        iconPath: 'static/tabbar/home.png',
        selectedIconPath: 'static/tabbar/home-active.png',
      },
      {
        pagePath: 'pages/customer/cart/cart',
        text: '购物车',
        iconPath: 'static/tabbar/cart.png',
        selectedIconPath: 'static/tabbar/cart-active.png',
      },
      {
        pagePath: 'pages/customer/my-orders/my-orders',
        text: '订单',
        iconPath: 'static/tabbar/order.png',
        selectedIconPath: 'static/tabbar/order-active.png',
      },
      {
        pagePath: 'pages/customer/my/my',
        text: '我的',
        iconPath: 'static/tabbar/my.png',
        selectedIconPath: 'static/tabbar/my-active.png',
      },
    ],
  },

  permission: {
    'scope.userLocation': {
      desc: '你的位置信息将用于展示附近摊位',
    },
  },

  requiredPrivateInfos: ['getLocation'],

  usingComponents: {},
})
