<script setup>
import { ref, onMounted, computed } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/stores/auth";
import instance from "@/api/index";
import ConfirmDeleteModal from "@/components/domain/admin/DeleteModal.vue";
import styles from "@/assets/styles/pages/Admin.module.css";

const members = ref([]);
const showModal = ref(false);
const selectedMember = ref(null);
const router = useRouter();
const auth = useAuthStore();
const myId = computed(() => auth.state.user?.id ?? null);

onMounted(async () => {
  try {
    const { data } = await instance.get("/cheongsan/admin/users");
    console.log("admin users raw:", data);
    members.value = data.filter((user) => user.role !== "ADMIN");
  } catch (e) {
    console.error("사용자 목록 불러오기 실패", e);
    alert("사용자 목록을 불러오는 데 실패했습니다.");
  }
});

const formatDate = (iso) => {
  if (!iso) return "-";
  const d = new Date(iso);
  const y = d.getFullYear();
  const m = String(d.getMonth() + 1).padStart(2, "0");
  const day = String(d.getDate()).padStart(2, "0");
  return `${y}.${m}.${day}`;
};

function openDeleteModal(index) {
  selectedMember.value = members.value[index];
  showModal.value = !!selectedMember.value;
}

async function confirmDelete() {
  if (!selectedMember.value) return;
  const userId = selectedMember.value.id;
  try {
    await instance.delete(`/cheongsan/admin/users/${userId}`);
    members.value = members.value.filter((m) => m.id !== userId);
    showModal.value = false;
    selectedMember.value = null;
    alert("회원이 정상적으로 삭제되었습니다.");
  } catch (e) {
    console.error("사용자 삭제 실패:", e);
    alert("사용자 삭제에 실패했습니다.");
  }
}

function cancelDelete() {
  showModal.value = false;
}

const displayMemberName = computed(() =>
  selectedMember.value && selectedMember.value.nickname
    ? selectedMember.value.nickname
    : "회원"
);

async function logout() {
  try {
    if (auth.state.refreshToken) {
      await instance.post("/cheongsan/auth/logout", {
        refreshToken: auth.state.refreshToken,
      });
    }
  } catch (_) {
  } finally {
    auth.logout();
    router.replace("/login");
  }
}
</script>

<template>
  <div :class="styles.page">
    <div :class="styles.container">
      <div :class="styles.header">
        <div :class="styles.headerSpacer"></div>
        <h3 :class="styles.title">회원 관리</h3>
        <button :class="styles.logoutBtn" @click="logout" aria-label="로그아웃">
          <i class="fas fa-sign-out-alt"></i>
        </button>
      </div>

      <div :class="styles.tableContainer">
        <table :class="styles.memberTable">
          <thead>
            <tr>
              <th :class="styles.thName">이름</th>
              <th :class="styles.thDate">가입일</th>
              <th :class="styles.thAction"></th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(member, index) in members" :key="member.id">
              <td :class="styles.tdName">{{ member.nickname }}</td>
              <td :class="styles.tdDate">{{ formatDate(member.createdAt) }}</td>
              <td :class="styles.tdAction">
                <button
                  :class="styles.deleteBtn"
                  :disabled="member.id === myId"
                  :title="
                    member.id === myId
                      ? '본인 계정은 삭제할 수 없습니다.'
                      : '삭제'
                  "
                  @click="openDeleteModal(index)"
                >
                  삭제
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <ConfirmDeleteModal
      :showModal="showModal"
      :memberName="displayMemberName"
      @confirm="confirmDelete"
      @cancel="cancelDelete"
    />
  </div>
</template>
