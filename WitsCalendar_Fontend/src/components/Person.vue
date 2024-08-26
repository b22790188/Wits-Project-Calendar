<template>
  <div class="person">
    <h2>一輛{{ car.brand }} 車，價值{{ car.price }},年份{{ car.year }}</h2>
    <button @click="changePirce">變更價格</button>
  </div>
</template>

<script setup name="Car">
// 數據
let car = { brand: 'Toyota', model: 'Corolla', year: 2005, price: 100000 }

function changePirce() {
  car.price += 1000
}
</script>
<style scoped>
.person {
  background-color: skyblue;
  box-shadow: 0 0 10px;
  border-radius: 10px;
  padding: 20px;
}
button {
  margin: 10px;
}
</style>
