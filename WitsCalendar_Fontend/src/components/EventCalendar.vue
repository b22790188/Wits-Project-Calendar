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
const events = ref([])

const calendarOptions = ref({
  plugins: [dayGridPlugin, interactionPlugin],
  initialView: 'dayGridMonth',
  weekends: props.weekends,
  dateClick: (arg) => {
    console.log('Date clicked in Calendar:', arg.dateStr)
    emit('date-click', arg.dateStr)
  },

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
    let fetchedevents = []
  
    for(const event of response.data) {
      fetchedevents.push(formattedEvent(event)) 
    }

    console.log('Fetched events:', events)
    events.value = fetchedevents 
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

const formattedEvent = (event) => {
  const startDate = formattedDate(event.start.date.value)
  const summary = event.summary

  return { title: summary, date: startDate }
}
const formattedDate = (timestamp) => {
  const date = new Date(timestamp)
  return format(date, 'yyyy-MM-dd')
}

defineExpose({ addEvent })
</script>
