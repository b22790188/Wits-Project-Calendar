<template>
  <div @contextmenu.prevent="handleContextMenu" style="height: 100vh; width: 100%">
    <FullCalendar ref="fullCalendar" :options="calendarOptions" />

    <div
      v-if="isPopoverVisible"
      class="custom-popover"
      :style="{ left: `${popoverX}px`, top: `${popoverY}px` }"
    >
      <div @click="showAddEventDialog">新增事件</div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, onBeforeUnmount } from 'vue'
import FullCalendar from '@fullcalendar/vue3'
import dayGridPlugin from '@fullcalendar/daygrid'
import timeGridPlugin from '@fullcalendar/timegrid'
import listGridPlugin from '@fullcalendar/list'
import interactionPlugin from '@fullcalendar/interaction'

const emit = defineEmits(['date-click'])
const props = defineProps(['weekends'])
const fullCalendar = ref(null)
const isPopoverVisible = ref(false)
const popoverX = ref(0)
const popoverY = ref(0)
const selectedDate = ref('')

const calendarOptions = ref({
  plugins: [dayGridPlugin, interactionPlugin, timeGridPlugin, listGridPlugin],
  initialView: 'dayGridMonth',
  locale: 'zh-tw',
  headerToolbar: {
    left: 'prev,next today',
    center: 'title',
    right: 'dayGridMonth,timeGridWeek,timeGridDay,listMonth'
  },
  weekends: props.weekends,
  dateClick: (info) => {
    console.log(info)
    selectedDate.value = info.dateStr
    emit('date-click', info.dateStr)
  },
  events: [
    { title: '顯示測試事件 01', date: '2024-08-30' },
    { title: '顯示測試事件 02', date: '2024-08-31' }
  ]
})

watch(
  () => props.weekends,
  (newValue) => {
    if (fullCalendar.value) {
      const api = fullCalendar.value.getApi()
      api.setOption('weekends', newValue)
    }
  }
)

onMounted(() => {
  fullCalendar.value.$el.addEventListener('contextmenu', handleContextMenu)
  document.addEventListener('click', handleDocumentClick)
})

onBeforeUnmount(() => {
  if (fullCalendar.value) {
    fullCalendar.value.$el.removeEventListener('contextmenu', handleContextMenu)
  }
  document.removeEventListener('click', handleDocumentClick)
})

const handleDocumentClick = (event) => {
  if (!event.target.closest('.custom-popover') && !event.target.closest('.fc-daygrid-day')) {
    isPopoverVisible.value = false
  }
}

const handleContextMenu = (event) => {
  event.preventDefault()
  popoverX.value = event.clientX
  popoverY.value = event.clientY
  isPopoverVisible.value = true

  const hitEl = document.elementFromPoint(event.clientX, event.clientY)

  if (hitEl && (hitEl.classList.contains('fc-daygrid-day') || hitEl.closest('.fc-daygrid-day'))) {
    const dateEl = hitEl.classList.contains('fc-daygrid-day')
      ? hitEl
      : hitEl.closest('.fc-daygrid-day')
    const dateStr = dateEl.getAttribute('data-date')
    if (dateStr) {
      selectedDate.value = dateStr
      isPopoverVisible.value = true
    } else {
      isPopoverVisible.value = false
      console.warn('無法從點擊位置獲取日期')
    }
  } else {
    isPopoverVisible.value = false
    console.warn('無法從點擊位置獲取日期')
  }
}

const showAddEventDialog = () => {
  isPopoverVisible.value = false
  emit('date-click', selectedDate.value)
}

const addEvent = (event) => {
  if (fullCalendar.value) {
    const api = fullCalendar.value.getApi()
    api.addEvent(event)
  }
}

defineExpose({ addEvent })
</script>

<style scoped>
.custom-popover {
  position: fixed;
  width: 65px;
  background-color: white;
  border: 1px solid #ccc;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  padding: 10px;
  border-radius: 4px;
}
</style>
