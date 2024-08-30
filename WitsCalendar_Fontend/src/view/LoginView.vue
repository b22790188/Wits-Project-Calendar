<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { googleSdkLoaded } from 'vue3-google-login'

const data = ref()
const router = useRouter()

const login = () => {
  googleSdkLoaded((google) => {
    google.accounts.oauth2
      .initTokenClient({
        client_id: '402282197504-i03vqslflc0nbv4ttobahhm36c20qlma.apps.googleusercontent.com',
        scope: 'email profile openid https://www.googleapis.com/auth/calendar',
        callback: (res) => {
          localStorage.setItem('authToken', res.access_token)
          router.push('/calendar')
        }
      })
      .requestAccessToken()
  })
}
</script>

<template>
  <div>
    <button type="button" @click="login">Google login</button>
    <p>{{ data }}</p>
  </div>
</template>

<style scoped>
p {
  margin-top: 12px;
  word-break: break-all;
}
</style>
