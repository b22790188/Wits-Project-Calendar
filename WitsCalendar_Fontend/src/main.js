import { createApp } from 'vue'
import App from './App.vue'
import ElementPlus from 'element-plus'
import vue3GoogleLogin from 'vue3-google-login'
import 'element-plus/dist/index.css'
import router from './router'

const app = createApp(App)

app.use(ElementPlus)

app.use(router)

app.use(vue3GoogleLogin, {
  clientId: '833567069700-ngh0neuifrrq1tdeul96maf7sr59rhh3.apps.googleusercontent.com'
})

app.mount('#app')
