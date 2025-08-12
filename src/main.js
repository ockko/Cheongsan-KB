
import "./assets/main.css";

import { createApp } from "vue";
import { createPinia } from "pinia";

import App from "./App.vue";
import router from "./router";

import { library } from "@fortawesome/fontawesome-svg-core";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import { faFaceSmile, faThumbsUp } from "@fortawesome/free-regular-svg-icons";
import { faBell } from "@fortawesome/free-solid-svg-icons";

library.add(faFaceSmile, faThumbsUp, faBell);

const app = createApp(App);

app.use(createPinia());
app.use(router);

app.component("FontAwesomeIcon", FontAwesomeIcon);

app.mount("#app");
