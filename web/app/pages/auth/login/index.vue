<script setup lang="ts">
import { useStallmartApi } from '~/api/stallmart-api'

definePageMeta({
  auth: false,
})

const auth = useAdminAuth()
const api = useStallmartApi()
const form = reactive({
  account: '',
  password: '',
  captchaAnswer: '',
})
const showPassword = ref(false)
const errorMessage = ref('')
const failedAttempts = ref(0)
const captchaVisible = ref(false)
const captcha = ref<{ captchaId: string, imageBase64: string } | null>(null)

onMounted(async () => {
  auth.restore()
  if (auth.session.value) {
    await navigateTo(auth.session.value.entryPath, { replace: true })
  }
})

const loadCaptcha = async () => {
  captcha.value = await api.captcha()
  form.captchaAnswer = ''
}

const submit = async () => {
  errorMessage.value = ''
  try {
    await auth.login({
      account: form.account.trim(),
      password: form.password,
      ...(captchaVisible.value && captcha.value
        ? {
            captchaId: captcha.value.captchaId,
            captchaAnswer: form.captchaAnswer,
          }
        : {}),
    })
  } catch (error: any) {
    failedAttempts.value += 1
    const response = error?.data || error?.response?._data
    const message = response?.message
    const shouldShowCaptcha = response?.data?.captchaRequired || failedAttempts.value >= 3
    if (shouldShowCaptcha) {
      captchaVisible.value = true
      await loadCaptcha()
    }
    if (message === 'captcha_required') {
      errorMessage.value = '请输入验证码后再登录'
    } else if (message === 'captcha_invalid') {
      errorMessage.value = '验证码错误，请重新输入'
    } else {
      errorMessage.value = shouldShowCaptcha ? '账号、密码或验证码错误' : '账号或密码错误'
    }
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
        <div v-if="captchaVisible" class="field full">
          <label>验证码</label>
          <div class="grid grid-cols-[minmax(0,1fr)_auto] gap-2">
            <div class="grid gap-2">
              <img v-if="captcha?.imageBase64" :src="captcha.imageBase64" alt="验证码" class="h-11 w-[130px] rounded-lg border border-ink-200 bg-white object-contain">
              <input v-model="form.captchaAnswer" inputmode="numeric" autocomplete="off" placeholder="输入图片验证码" required>
            </div>
            <button class="button" type="button" @click="loadCaptcha">换一题</button>
          </div>
        </div>
        <p v-if="errorMessage" class="form-error full">{{ errorMessage }}</p>
        <button class="button primary full" type="submit">登录</button>
      </form>
      <div class="grid gap-1.5 text-sm text-ink-500">
        <span>请使用已分配的后台账号登录。</span>
        <span>连续输错后需要完成验证码验证。</span>
      </div>
    </section>
  </main>
</template>
