<template>
  <div>   
      <div class="container fluid">
        <h1 class="mt-5 mb-5 text-center">Metering Dashboard</h1>
        <MeteringDashboardActiveUsers />
        <MeteringDashboardDatabase />

      </div>
  </div>    
</template>

<script>
import MeteringDashboardActiveUsers from '@/components/MeteringDashboardActiveUsers.vue';
import MeteringDashboardDatabase from '@/components/MeteringDashboardDatabase.vue';
export default {
  name: 'MeteringDashboard',
  props: [],
  components: {
    MeteringDashboardActiveUsers,
    MeteringDashboardDatabase,
  },
  data: function(){
    return {
    }
  }
}
</script>

<style>
</style>