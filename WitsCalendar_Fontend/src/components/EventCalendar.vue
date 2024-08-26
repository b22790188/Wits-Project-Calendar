<template>
  <FullCalendar ref="fullCalendar" :options="calendarOptions" />
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import FullCalendar from '@fullcalendar/vue3'
import dayGridPlugin from '@fullcalendar/daygrid'
import interactionPlugin from '@fullcalendar/interaction'

const emit = defineEmits(['date-click'])
const props = defineProps(['weekends'])
const fullCalendar = ref(null)

const calendarOptions = ref({
  plugins: [dayGridPlugin, interactionPlugin],
  initialView: 'dayGridMonth',
  weekends: props.weekends,
  dateClick: (arg) => {
    console.log('Date clicked in Calendar:', arg.dateStr)
    emit('date-click', arg.dateStr)
  },
  events: [
    { title: '顯示測試事件 01', date: '2024-08-30' },
    { title: '顯示測試事件 02', date: '2024-08-31' }
  ]
})

watch(
  () => props.weekends,
  (newValue) => {
    console.log('Weekends prop changed:', newValue)
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
