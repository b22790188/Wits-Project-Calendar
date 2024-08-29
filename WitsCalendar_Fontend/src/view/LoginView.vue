<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router';
import { googleTokenLogin } from 'vue3-google-login'

const data = ref()
const router = useRouter()

const login = () => {
    googleTokenLogin()
        .then((res) => {
          localStorage.setItem('authToken', res.access_token)
          router.push('/calendar')
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
