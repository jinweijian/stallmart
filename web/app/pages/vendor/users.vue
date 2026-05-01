<script setup lang="ts">
import { useStallmartApi } from '~/api/stallmart-api'

const api = useStallmartApi()
const { data: users } = await useAsyncData('vendor-users', () => api.vendorUsers())
</script>

<template>
  <AppShell>
    <div class="page-head">
      <div>
        <h1>用户管理</h1>
        <p>查看与当前商家产生订单或购物车关系的用户。</p>
      </div>
    </div>

    <div class="table-wrap">
      <table>
        <thead>
          <tr>
            <th>用户</th>
            <th>手机号</th>
            <th>授权</th>
            <th>角色</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in users" :key="user.id">
            <td>
              <strong>{{ user.nickname }}</strong>
              <div class="muted">#{{ user.id }}</div>
            </td>
            <td>{{ user.phone || '-' }}</td>
            <td><span :class="['badge', user.hasPhone ? '' : 'gray']">{{ user.hasPhone ? '已授权' : '未授权' }}</span></td>
            <td>{{ user.role }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </AppShell>
</template>
