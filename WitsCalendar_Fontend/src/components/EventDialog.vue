<template>
  <el-dialog
    :title="mode === 'edit' ? '修改事件' : '新增事件'"
    v-model="dialogVisible"
    width="30%"
    @close="resetForm"
  >
    <el-form :model="localEvent">
      <el-form-item label="事件標題">
        <el-input v-model="localEvent.title" />
      </el-form-item>
      <el-form-item label="日期">
        <el-date-picker v-model="localEvent.date" type="datetime" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="handleSaveEvent">
        {{ mode === 'edit' ? '修改' : '新增' }}
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  visible: Boolean,
  event: Object,
  mode: {
    type: String,
    // default to 'add' mode
    default: 'add'
  }
})

const emit = defineEmits(['update:visible', 'add-event', 'edit-event'])

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

const handleSaveEvent = () => {
  if (localEvent.value.title && localEvent.value.date) {
    if (props.mode === 'edit') {
      emit('edit-event', {
        id: localEvent.value.id,
        title: localEvent.value.title,
        date: localEvent.value.date
      })
      console.log('localEvent:', localEvent.value)
    } else {
      emit('add-event', {
        title: localEvent.value.title,
        date: localEvent.value.date
      })
      console.log('localEvent:', localEvent.value)
    }
    dialogVisible.value = false
    resetForm()
  }
}

const resetForm = () => {
  localEvent.value = { title: '', date: '' }
}
</script>
