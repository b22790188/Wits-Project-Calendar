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
      <el-form-item label="全天">
        <el-checkbox v-model="localEvent.allDay">全天</el-checkbox>
      </el-form-item>
      <el-form-item label="開始日期">
        <el-date-picker
          v-model="localEvent.startDate"
          :type="localEvent.allDay ? 'date' : 'datetime'"
        />
      </el-form-item>
      <el-form-item label="結束日期">
        <el-date-picker
          v-model="localEvent.endDate"
          :type="localEvent.allDay ? 'date' : 'datetime'"
        />
      </el-form-item>
      <el-form-item>
        <el-input type="textarea" v-model="localEvent.description" placeholder="事件描述" />
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
  if (localEvent.value.title && localEvent.value.startDate && localEvent.value.endDate) {
    if (props.mode === 'edit') {
      emit('edit-event', {
        id: localEvent.value.id,
        title: localEvent.value.title,
        startDate: formatDate(localEvent.value.startDate, localEvent.value.allDay),
        endDate: formatDate(localEvent.value.endDate, localEvent.value.allDay),
        description: localEvent.value.description,
        allDay: localEvent.value.allDay
      })
    } else {
      emit('add-event', {
        title: localEvent.value.title,
        startDate: formatDate(localEvent.value.startDate, localEvent.value.allDay),
        endDate: formatDate(localEvent.value.endDate, localEvent.value.allDay),
        description: localEvent.value.description,
        allDay: localEvent.value.allDay
      })
    }
    dialogVisible.value = false
    resetForm()
  }
}

const TimeConstants = {
  MILLISECONDS_PER_MINUTE: 60 * 1000
}

const formatDate = (date, isAllDay) => {
  const dateObj = typeof date === 'string' ? new Date(date) : date

  if (isAllDay) {
    return new Date(
      // Use local time offset UTC to prevent date offset
      dateObj.getTime() - dateObj.getTimezoneOffset() * TimeConstants.MILLISECONDS_PER_MINUTE
    )
      .toISOString()
      .split('T')[0]
  } else {
    return dateObj.toISOString()
  }
}

const resetForm = () => {
  localEvent.value = { title: '', startDate: '', endDate: '', description: '', allDay: false }
}
</script>
