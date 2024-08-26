<template>
  <div id="app">
    <!-- Element Plus 按鈕，點擊切換週末的顯示狀態 -->
    <el-button type="primary" @click="toggleWeekends"
      >Toggle Weekends ({{ showWeekends ? 'On' : 'Off' }})
    </el-button>

    <!-- Calendar 組件，傳入 `showWeekends` 作為屬性，並綁定 `date-click` 事件 -->
    <Calendar ref="calendar" @date-click="handleDateClick" :weekends="showWeekends" />

    <!-- EventDialog 組件，使用 `v-model` 雙向綁定對話框的可見性，傳遞事件對象和添加事件的處理程序 -->
    <EventDialog v-model:visible="isDialogVisible" :event="newEvent" @add-event="addEvent" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import Calendar from './components/EventCalendar.vue'
import EventDialog from './components/EventDialog.vue'

const isDialogVisible = ref(false)
const newEvent = ref({ title: '', date: '' })
const showWeekends = ref(true)
const calendar = ref(null)

const handleDateClick = (dateStr) => {
  console.log('Date clicked in App:', dateStr)
  newEvent.value.date = dateStr
  isDialogVisible.value = true
}

const addEvent = (event) => {
  console.log('Event to add:', event)
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

onMounted(() => {
  console.log('App mounted')
})
</script>
