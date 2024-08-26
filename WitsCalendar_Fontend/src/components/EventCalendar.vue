<template>
  <FullCalendar ref="fullCalendar" :options="calendarOptions" />
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import FullCalendar from '@fullcalendar/vue3'
import dayGridPlugin from '@fullcalendar/daygrid'
import timeGridPlugin from '@fullcalendar/timegrid'
import listGridPlugin from '@fullcalendar/list'
import interactionPlugin from '@fullcalendar/interaction'

const emit = defineEmits(['date-click'])
const props = defineProps(['weekends'])
const fullCalendar = ref(null)

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
  dateClick: (date) => {
    emit('date-click', date.dateStr)
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
  console.log('Calendar mounted')
})

const addEvent = (event) => {
  if (fullCalendar.value) {
    const api = fullCalendar.value.getApi()
    api.addEvent(event)
  }
}

defineExpose({ addEvent })
</script>
