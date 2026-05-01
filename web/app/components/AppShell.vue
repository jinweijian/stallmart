<template>
  <div class="layout">
    <aside class="sidebar">
      <NuxtLink class="brand" to="/">
        <strong>StallMart 管理台</strong>
        <span>平台与商家运营</span>
      </NuxtLink>
      <nav class="nav">
        <NuxtLink to="/">平台总览</NuxtLink>
        <NuxtLink to="/platform/vendors">商家列表</NuxtLink>
        <NuxtLink to="/vendor">商家 H5</NuxtLink>
        <NuxtLink to="/vendor/products">商品管理</NuxtLink>
        <NuxtLink to="/vendor/orders">订单管理</NuxtLink>
        <NuxtLink to="/vendor/decoration">装修设置</NuxtLink>
      </nav>
    </aside>
    <main class="content">
      <slot />
    </main>
  </div>
</template>
