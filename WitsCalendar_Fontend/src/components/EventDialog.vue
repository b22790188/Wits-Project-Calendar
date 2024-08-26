<template>
  <el-dialog title="新增事件" v-model="dialogVisible" width="30%" @close="resetForm">
    <el-form :model="localEvent">
      <el-form-item label="事件標題">
        <el-input v-model="localEvent.title" />
      </el-form-item>
      <el-form-item label="日期">
        <el-date-picker v-model="localEvent.date" type="date" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="handleAddEvent">確定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  visible: Boolean,
  event: Object
})

const emit = defineEmits(['update:visible', 'add-event'])

const dialogVisible = ref(props.visible)
const localEvent = ref({ ...props.event })

watch(
  () => props.visible,
  (newVal) => {
    dialogVisible.value = newVal
  }
)

watch(dialogVisible, (newVal) => {
  emit('update:visible', newVal)
})

watch(
  () => props.event,
  (newVal) => {
    localEvent.value = { ...newVal }
  },
  { deep: true }
)

const handleAddEvent = () => {
  if (localEvent.value.title && localEvent.value.date) {
    emit('add-event', { ...localEvent.value })
    dialogVisible.value = false
    resetForm()
  }
}

const resetForm = () => {
  localEvent.value = { title: '', date: '' }
}
</script>
