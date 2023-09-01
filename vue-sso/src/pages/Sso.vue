<script setup lang="ts">
import {computed, onMounted, reactive, ref, watch} from "vue";
import * as API from "@/api"
import * as TokenConstants from '@/constants/tokenConstants.ts'
import {useRoute, useRouter} from "vue-router";

const $router = useRouter();
const $route = useRoute();

const client = ref();

const params = reactive({
  redirectUri: $route.query.redirectUri,
  responseType: $route.query.responseType,
  clientId: $route.query.clientId,
  state: $route.query.state,
  scopes: $route.query.scopes as string,
  scopesArr: []
})

const loginForm = reactive({
  scopes: []
})


onMounted(() => {

  if (params.scopes) {
    params.scopesArr = params.scopes.split(" ")
  }

  // 获取授权页的基本信息
  API.getAuthorize(params.clientId).then(res => {
    client.value = res.data.client
    // 解析 scope
    let scopes
    // 1.1 如果 params.scope 非空，则过滤下返回的 scopes
    if (params.scopesArr.length > 0) {
      scopes = []
      for (const scope of res.data.scopes) {
        if (params.scopesArr.indexOf(scope.key) >= 0) {
          scopes.push(scope)
        }
      }
      // 1.2 如果 params.scope 为空，则使用返回的 scopes 设置它
    } else {
      scopes = res.data.scopes
      for (const scope of scopes) {
        params.scopesArr.push(scope.key)
      }
    }
    // 生成已选中的 checkedScopes
    for (const scope of scopes) {
      if (scope.value) {
        loginForm.scopes.push(scope.key)
      }
    }
  })

})


</script>

<template>
  <div class="container">
    <div class="login-wrapper">
      <div class="header">用户授权</div>
      <div class="form-wrapper">
        <p style="text-align: center">授权所有用户个人信息</p>
        <div class="btn">授权</div>
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
  height: 300px;
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
  line-height: 150px;
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
