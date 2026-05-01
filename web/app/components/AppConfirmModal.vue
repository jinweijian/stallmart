<script setup lang="ts">
withDefaults(defineProps<{
  cancelText?: string
  confirmText?: string
  message: string
  open: boolean
  title: string
}>(), {
  cancelText: '取消',
  confirmText: '确定',
})

const emit = defineEmits<{
  cancel: []
  confirm: []
}>()
</script>

<template>
  <Teleport to="body">
    <div v-if="open" class="fixed inset-0 z-50 grid place-items-center bg-ink-900/45 px-4">
      <section class="w-full max-w-md rounded-lg border border-ink-200 bg-white p-5 shadow-lg">
        <h2 class="m-0 text-lg font-semibold text-ink-900">{{ title }}</h2>
        <p class="mt-3 text-sm leading-6 text-ink-600">{{ message }}</p>
        <div class="actions mt-5 justify-end">
          <button class="button" type="button" @click="emit('cancel')">{{ cancelText }}</button>
          <button class="button primary" type="button" @click="emit('confirm')">{{ confirmText }}</button>
        </div>
      </section>
    </div>
  </Teleport>
</template>
