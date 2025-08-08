<script setup>
import styles from '@/assets/styles/pages/InitialSetup/InitialSetup2.module.css'
import ProgressHeader from '@/components/domain/InitialSetup/ProgressHeader.vue'
</script>

<template>
    <div :class="styles.page">
        <ProgressHeader :current="2" :total="3" />
        <div :class="styles.container">
            <div :class="styles.titleBox">
                <h2>마이데이터<br/>연동 중</h2>
                <p>000님의 자산내역을 불러오는 중입니다. <br/> 잠시만 기다려주세요.</p>
            </div>
            <img src="/images/logo-blue.png" alt="로고" width="200" height="200" />
        </div>
    </div>
</template>