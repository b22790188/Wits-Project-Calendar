<template>
  <div id="app">
    <Calendar
      ref="calendar"
      @date-click="handleDateClick"
      :weekends="showWeekends"
      @edit-event="handleEditEvent"
    />

    <EventDialog
      v-model:visible="isDialogVisible"
      :event="selectedEventData"
      :mode="dialogMode"
      @add-event="addEvent"
      @edit-event="editEvent"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import Calendar from './components/EventCalendar.vue'
import EventDialog from './components/EventDialog.vue'

const isDialogVisible = ref(false)
const selectedEventData = ref({ title: '', startDate: '', endDate: '' })
const showWeekends = ref(true)
const calendar = ref(null)
const dialogMode = ref('add')

const handleDateClick = (dateStr) => {
  selectedEventData.value.startDate = dateStr
  selectedEventData.value.endDate = new Date(
    new Date(dateStr).getTime() + 60 * 60 * 1000
  ).toISOString()
  dialogMode.value = 'add'
  isDialogVisible.value = true
}

const handleEditEvent = (event) => {
  selectedEventData.value = {
    id: event.id,
    title: event.title,
    startDate: event.start,
    endDate: event.end
  }
  dialogMode.value = 'edit'
  isDialogVisible.value = true
}

const addEvent = (event) => {
  if (calendar.value) {
    calendar.value.addEvent(event)
  } else {
    console.error('Calendar reference not found')
  }
}

const editEvent = (event) => {
  console.log('calendar.value:', calendar.value)
  if (calendar.value) {
    calendar.value.editEvent(event)
  } else {
    console.warn('Event not found')
  }
}

onMounted(() => {})
</script>
