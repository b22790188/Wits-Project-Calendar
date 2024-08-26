<template>
  <FullCalendar ref="fullCalendar" :options="calendarOptions" />
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import FullCalendar from '@fullcalendar/vue3'
import dayGridPlugin from '@fullcalendar/daygrid'
import interactionPlugin from '@fullcalendar/interaction'
import axios from 'axios'

const emit = defineEmits(['date-click'])
const props = defineProps(['weekends'])
const fullCalendar = ref(null)
const events = ref([])

const calendarOptions = ref({
  plugins: [dayGridPlugin, interactionPlugin],
  initialView: 'dayGridMonth',
  weekends: props.weekends,
  dateClick: (arg) => {
    console.log('Date clicked in Calendar:', arg.dateStr)
    emit('date-click', arg.dateStr)
  },
  // events: [
  //         { title: 'event 1', date: '2019-04-01' },
  //         { title: 'event 2', date: '2019-04-02' }
  //       ]

  events: events.value
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

watch(events, (newEvents) => {
  if (fullCalendar.value) {
    const api = fullCalendar.value.getApi()
    api.removeAllEvents()
    api.addEventSource(newEvents)
  }
})

onMounted(async () => {
  console.log('Calendar mounted')
  try {
    const response = await axios.get('http://localhost:8080/events')
    console.log(response.data);
    events.value = response.data
  } catch (error) {
    console.error('Failed to fetch events:', error)
  }
})

const addEvent = (event) => {
  if (fullCalendar.value) {
    const api = fullCalendar.value.getApi()
    api.addEvent(event)
  }
}

defineExpose({ addEvent })
</script>
