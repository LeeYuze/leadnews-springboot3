<script setup lang="ts">
import {computed, reactive} from "vue";
import * as API from "@/api"
import * as TokenConstants from '@/constants/tokenConstants.ts'
import {useRoute, useRouter} from "vue-router";

const $router = useRouter();
const $route = useRoute();

const redirect = computed(() => $route.query.redirect ? decodeURIComponent($route.query.redirect as string) : undefined);


const state = reactive({
  username: "admin",
  password: "admin"
})

const login = () => {
  // console.log(state)
  API.login(state).then(res => {
    const {code, data} = res;
    if (code === 0) {
      const {accessToken, refreshToken} = data;
      // storage accessToken
      localStorage.setItem(TokenConstants.ACCESSTOKEN_KEY, accessToken)
      // storage refreshToken
      localStorage.setItem(TokenConstants.REFRESHTOKEN_KEY, refreshToken)
      // $router.push({path: redirect.value || "/"})
      location.href = redirect.value
    }
  })
}
</script>

<template>
  <div class="container">
    <div class="login-wrapper">
      <div class="header">Login</div>
      <div class="form-wrapper">
        <input type="text" name="username" placeholder="username" class="input-item" :value="state.username">
        <input type="password" name="password" placeholder="password" class="input-item" :value="state.password">
        <div class="btn" @click="login">Login</div>
      </div>
      <div class="msg" style="display: none">
        Don't have account?
        <a href="#">Sign up</a>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>

.container {
  height: 100%;
  background-image: linear-gradient(to right, #fbc2eb, #a6c1ee);
}

.login-wrapper {
  background-color: #fff;
  width: 358px;
  height: 500px;
  border-radius: 15px;
  padding: 0 50px;
  position: relative;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
}

.header {
  font-size: 38px;
  font-weight: bold;
  text-align: center;
  line-height: 200px;
}

.input-item {
  display: block;
  width: 100%;
  margin-bottom: 20px;
  border: 0;
  padding: 10px;
  border-bottom: 1px solid rgb(128, 125, 125);
  font-size: 15px;
  outline: none;
}

.input-item:placeholder {
  text-transform: uppercase;
}

.btn {
  user-select: none;
  cursor: pointer;
  text-align: center;
  padding: 10px;
  width: 100%;
  margin-top: 40px;
  background-image: linear-gradient(to right, #a6c1ee, #fbc2eb);
  color: #fff;

  &:hover {
    background-image: linear-gradient(to right, rgba(#a6c1ee, 0.8), rgba(#fbc2eb, 0.8));
  }

  &:active {
    background-image: linear-gradient(to right, rgba(#a6c1ee, 0.6), rgba(#fbc2eb, 0.6));
  }

}

.msg {
  text-align: center;
  line-height: 88px;
}

a {
  text-decoration-line: none;
  color: #abc1ee;
}
</style>
