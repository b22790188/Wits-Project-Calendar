<template>
    <div>
      <el-button type="primary" @click="toggleWeekends">
        Toggle Weekends ({{ showWeekends ? 'On' : 'Off' }})
      </el-button>
  
      <Calendar ref="calendar" @date-click="handleDateClick" :weekends="showWeekends" />
      <EventDialog v-model:visible="isDialogVisible" :event="newEvent" @add-event="addEvent" />
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue'
  import Calendar from '../components/EventCalendar.vue'
  import EventDialog from '../components/EventDialog.vue'
  
  const isDialogVisible = ref(false)
  const newEvent = ref({ title: '', date: '' })
  const showWeekends = ref(true)
  const calendar = ref(null)
  
  const handleDateClick = (dateStr) => {
    newEvent.value.date = dateStr
    isDialogVisible.value = true
  }
  
  const addEvent = (event) => {
    if (calendar.value) {
      calendar.value.addEvent(event)
    } else {
      console.error('Calendar reference not found')
    }
  }
  
  const toggleWeekends = () => {
    showWeekends.value = !showWeekends.value
    console.log('Toggled weekends:', showWeekends.value)
  }
  </script>
  