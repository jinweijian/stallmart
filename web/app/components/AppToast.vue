<script setup lang="ts">
withDefaults(defineProps<{
  message: string
  show: boolean
  type?: 'success' | 'error'
}>(), {
  type: 'success',
})

const emit = defineEmits<{
  close: []
}>()
</script>

<template>
  <Teleport to="body">
    <div v-if="show" class="fixed right-4 top-4 z-50 max-w-sm rounded-lg border bg-white px-4 py-3 text-sm font-semibold shadow-lg"
      :class="type === 'error' ? 'border-danger-600 text-danger-600' : 'border-brand-100 text-brand-700'"
    >
      <div class="flex items-start gap-3">
        <span>{{ message }}</span>
        <button class="text-ink-400 hover:text-ink-700" type="button" @click="emit('close')">关闭</button>
      </div>
    </div>
  </Teleport>
</template>
