<template>
  <div>   
      <div class="container fluid">
        <h1 class="mt-5 mb-5 text-center">Metering Dashboard</h1>
        <MeteringDashboardActiveUsers />
      </div>
  </div>    
</template>

<script>
import MeteringDashboardActiveUsers from '@/components/MeteringDashboardActiveUsers.vue';
export default {
  name: 'MeteringDashboard',
  props: [],
  components: {
    MeteringDashboardActiveUsers
  },
  data: function(){
    return {
    }
  }
}
</script>

<style>
</style>
