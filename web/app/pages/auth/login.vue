<script setup lang="ts">
definePageMeta({
  auth: false,
})

const auth = useAdminAuth()
const form = reactive({
  account: '',
  password: '',
})
const showPassword = ref(false)
const errorMessage = ref('')

onMounted(async () => {
  auth.restore()
  if (auth.session.value) {
    await navigateTo(auth.session.value.entryPath, { replace: true })
  }
})

const submit = async () => {
  errorMessage.value = ''
  try {
    await auth.login(form)
  } catch {
    errorMessage.value = '账号或密码错误'
  }
}
</script>

<template>
  <main class="grid min-h-screen place-items-center bg-ink-100 px-4 py-8">
    <section class="grid w-full max-w-md gap-6 rounded-lg border border-ink-200 bg-white p-6 shadow-panel sm:p-8">
      <div>
        <span class="badge">StallMart Admin</span>
        <h1 class="mt-3 text-3xl font-semibold text-ink-900">后台登录</h1>
        <p class="mt-2 text-sm leading-6 text-ink-500">平台管理员进入平台管理，商家管理员进入自己的 H5 商家后台。</p>
      </div>
      <form class="form-grid" @submit.prevent="submit">
        <div class="field full">
          <label>账号</label>
          <input v-model="form.account" autocomplete="username" required>
        </div>
        <div class="field full">
          <label>密码</label>
          <div class="grid grid-cols-[minmax(0,1fr)_auto] gap-2">
            <input v-model="form.password" :type="showPassword ? 'text' : 'password'" autocomplete="current-password" required>
            <button class="button" type="button" @click="showPassword = !showPassword">{{ showPassword ? '隐藏' : '显示' }}</button>
          </div>
        </div>
        <p v-if="errorMessage" class="form-error full">{{ errorMessage }}</p>
        <button class="button primary full" type="submit">登录</button>
      </form>
      <div class="grid gap-1.5 text-sm text-ink-500">
        <span>平台：platform / platform123</span>
        <span>商家：vendor / vendor123</span>
      </div>
    </section>
  </main>
</template>
