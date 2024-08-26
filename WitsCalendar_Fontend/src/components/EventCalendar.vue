<template>
  <FullCalendar ref="fullCalendar" :options="calendarOptions" />
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import FullCalendar from '@fullcalendar/vue3'
import dayGridPlugin from '@fullcalendar/daygrid'
import interactionPlugin from '@fullcalendar/interaction'
import axios from 'axios'
import { format } from 'date-fns'

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

  events: []
})

const addEvent = (event) => {
  if (fullCalendar.value) {
    const api = fullCalendar.value.getApi()
    api.addEvent(event)
  }
}

// format event data to match FullCalendar API
const formattedEvent = (event) => {
  const startDate = formattedDate(event.start.date.value)
  const summary = event.summary

  return { title: summary, date: startDate }
}

// format date to match FullCalendar API
const formattedDate = (timestamp) => {
  const date = new Date(timestamp)
  return format(date, 'yyyy-MM-dd')
}

onMounted(async () => {
  console.log('Calendar mounted')
  try {
    const response = await axios.get('http://localhost:8080/events')
    let fetchedevents = response.data.map(formattedEvent)

    if (fullCalendar.value) {
      const api = fullCalendar.value.getApi()
      api.addEventSource(fetchedevents)
    }
  } catch (error) {
    console.error('Failed to fetch events:', error)
  }
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

defineExpose({ addEvent })
</script>
