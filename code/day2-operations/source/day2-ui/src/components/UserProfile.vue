<template>
  <div>
    <h1 class="mt-5 mb-5 text-center">User Profile</h1>
    <!-- Manage coordinator details section -->
    <div class="container">
      <div class="fd-row">
        <fd-field-set>
          <fd-form-item>
            <fd-form-label>User name</fd-form-label>
            <fd-input v-model="userName" required />
          </fd-form-item>
          <fd-form-item>
            <fd-form-label>User email</fd-form-label>
            <fd-input v-model="userInfo.email" required />
          </fd-form-item>
        </fd-field-set>
      </div>
    </div>

  </div>
</template>


<script>
export default {
  name: "UserProfile",
  data: function() {
    return {
      userInfo: "",
      userAlreadyinDb: ""
    };
  },
  computed: {
    // Merging user lastname and firstname
    userName: function() {
      let name = this.userInfo.firstname + ' ' + this.userInfo.lastname;
      return name;
    }
  },
  methods: {
    // Get userinfo from Approuter
    async getUserInfo(){
      if(!this.userInfo){
        // Calling API to extract user info from the JWT token
        const response = await fetch('/userinfo');
        this.userInfo = await response.json();
        console.log("[DEBUG] Loaded user info: ", this.userInfo);
      }
    }
  },
  mounted: function() {
    // Loading user info when component is mounted
    this.getUserInfo();
  }
};
</script>
<style>
</style>