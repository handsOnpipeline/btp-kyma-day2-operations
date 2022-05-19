<template>
  <div id="app">
    <!-- Loading the navbar on the top of every page -->
    <fiori-nav-bar />

    <!-- Inserting router with all props and events needed in the child components -->
    <router-view></router-view>

  </div>
</template>

<script>
import FioriNavBar from '@/components/FioriNavBar.vue';
export default {
  name: 'App',
  data: function(){
    return {
    }
  },
  components: {
    FioriNavBar,
  }
}
</script>
  
<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}
a{
  text-decoration: none !important;
}
</style>
