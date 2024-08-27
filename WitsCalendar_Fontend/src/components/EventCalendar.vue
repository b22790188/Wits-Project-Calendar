<template>
  <div style="height: 100vh; width: 100%">
    <FullCalendar ref="fullCalendar" :options="calendarOptions" />

    <div
      v-if="isPopoverVisible"
      class="custom-popover"
      :style="{ left: `${popoverX}px`, top: `${popoverY}px` }"
    >
      <div v-if="!isDeleteOptionVisible" @click="showAddEventDialog">新增事件</div>
      <div v-if="isDeleteOptionVisible" @click="deleteSelectedEvent">刪除事件</div>
      <div v-if="isDeleteOptionVisible" @click="showEditEventDialog">修改事件</div>
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
import axios from 'axios'
import { format } from 'date-fns'

const emit = defineEmits(['date-click'])
const props = defineProps(['weekends'])
const fullCalendar = ref(null)
const isPopoverVisible = ref(false)
const isDeleteOptionVisible = ref(false)
const popoverX = ref(0)
const popoverY = ref(0)
const selectedDate = ref('')
const selectedEvent = ref(null)
const events = ref([])

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
    selectedDate.value = info.dateStr
    emit('date-click', info.dateStr)
  },
  eventDidMount: (info) => {
    info.el.addEventListener('contextmenu', (e) => {
      e.preventDefault()
      handleContextMenuForEvent(e, info.event)
    })
  },
  eventRemove: async function (info) {
    const googleEventId = info.event.extendedProps.extendsProps?.googleEventId

    if (googleEventId) {
      try {
        await axios.delete('http://localhost:8080/events', {
          params: { eventId: googleEventId }
        })
        console.log('Event successfully deleted from Google Calendar.')
      } catch (error) {
        console.error('Failed to delete event from Google Calendar:', error)
        info.revert()
      }
    } else {
      console.warn('Google Event ID is not available.')
    }
  },
  eventChange: async function (info) {
    const googleEventId = info.event.extendedProps.extendsProps?.googleEventId

    const newStart = info.event.start ? info.event.start.toISOString() : null

    const newEnd = newStart
      ? new Date(info.event.start.getTime() + 60 * 60 * 1000).toISOString()
      : null

    if (googleEventId) {
      try {
        const requestData = {
          newSummary: info.event.title,
          newStart: newStart,
          newEnd: newEnd
        }

        await axios.put(`http://localhost:8080/events/${googleEventId}`, requestData, {
          headers: {
            'Content-Type': 'application/json'
          }
        })

        console.log('Event successfully updated in Google Calendar.')
      } catch (error) {
        console.error('Failed to update event in Google Calendar:', error)
        info.revert()
      }
    } else {
      console.warn('Google Event ID is not available.')
    }
  },
  events: events.value
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
  document.addEventListener('contextmenu', (e) => {
    if (!e.target.closest('.fc-event')) {
      handleContextMenu(e)
    }
  })
  document.addEventListener('click', handleDocumentClick)
})

onBeforeUnmount(() => {
  document.removeEventListener('contextmenu', handleContextMenu)
  document.removeEventListener('click', handleDocumentClick)
})

watch(events, (newEvents) => {
  if (fullCalendar.value) {
    const api = fullCalendar.value.getApi()
    api.removeAllEvents()
    api.addEventSource(newEvents)
  }
})

onMounted(async () => {
  try {
    const response = await axios.get('http://localhost:8080/events')
    let fetchedevents = []

    for (const event of response.data) {
      fetchedevents.push(formattedEvent(event))
    }

    events.value = fetchedevents
  } catch (error) {
    console.error('Failed to fetch events:', error)
  }
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
  isDeleteOptionVisible.value = false
  selectedDate.value = null
  const hitEl = document.elementFromPoint(event.clientX, event.clientY)

  if (hitEl && (hitEl.classList.contains('fc-daygrid-day') || hitEl.closest('.fc-daygrid-day'))) {
    const dateEl = hitEl.classList.contains('fc-daygrid-day')
      ? hitEl
      : hitEl.closest('.fc-daygrid-day')
    const dateStr = dateEl.getAttribute('data-date')
    if (dateStr) {
      selectedDate.value = dateStr
    } else {
      isPopoverVisible.value = false
      console.warn('無法從點擊位置獲取日期')
    }
  } else {
    isPopoverVisible.value = false
    console.warn('無法從點擊位置獲取日期')
  }
}

const handleContextMenuForEvent = (e, event) => {
  e.preventDefault()

  popoverX.value = e.clientX
  popoverY.value = e.clientY
  isPopoverVisible.value = true

  if (event) {
    isDeleteOptionVisible.value = true
    selectedEvent.value = event
  } else {
    isDeleteOptionVisible.value = false
    selectedEvent.value = null
  }
}

const deleteSelectedEvent = () => {
  if (selectedEvent.value) {
    const confirmDelete = confirm('確定要刪除此事件嗎？')
    if (confirmDelete) {
      selectedEvent.value.remove() // 刪除事件
      isDeleteOptionVisible.value = false
      selectedEvent.value = null
    }
  }
}

const showAddEventDialog = () => {
  isPopoverVisible.value = false
  emit('date-click', selectedDate.value)
}

const showEditEventDialog = () => {
  isPopoverVisible.value = false
  emit('edit-event', selectedEvent.value)
}

const addEvent = (event) => {
  if (fullCalendar.value) {
    const api = fullCalendar.value.getApi()
    api.addEvent(event)
  }
}

const editEvent = (updatedEvent) => {
  if (fullCalendar.value) {
    const api = fullCalendar.value.getApi()
    const existingEvent = api.getEventById(updatedEvent.id)

    if (existingEvent) {
      existingEvent.setProp('title', updatedEvent.title)
      existingEvent.setStart(updatedEvent.date)
    } else {
      console.warn('Event not found')
    }
  }
}

// format event data to match FullCalendar API
const formattedEvent = (event) => {
  const startDate = formattedDate(event.start.date.value)
  const summary = event.summary
  const googleEventId = event.id

  return { title: summary, date: startDate, extendsProps: { googleEventId } }
}

// format date to match FullCalendar API
const formattedDate = (timestamp) => {
  const date = new Date(timestamp)
  return format(date, 'yyyy-MM-dd')
}

defineExpose({ addEvent, editEvent })
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
