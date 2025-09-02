<script setup>
import { ref, onMounted, computed } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/stores/auth";
import { useUiStore } from "@/stores/ui";
import request from "@/api/index";
import ConfirmDeleteModal from "@/components/domain/admin/DeleteModal.vue";
import styles from "@/assets/styles/pages/Admin.module.css";

const members = ref([]);
const showModal = ref(false);
const selectedMember = ref(null);
const router = useRouter();
const auth = useAuthStore();
const myId = computed(() => auth.state.user?.id ?? null);
const uiStore = useUiStore();
onMounted(async () => {
  try {
    const { data } = await request.get("/cheongsan/admin/users");
    console.log("admin users raw:", data);
    members.value = data
      .filter((user) => user.role !== "ADMIN")
      .filter((user) => user.id !== 266293);
  } catch (e) {
    console.error("사용자 목록 불러오기 실패", e);
    uiStore.openModal({
      title: "오류 발생",
      message: "사용자 목록을 불러오는 데 실패했습니다.",
      isError: true,
    });
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

const openDeleteModal = (index) => {
  selectedMember.value = members.value[index];
  showModal.value = !!selectedMember.value;
};

const confirmDelete = async () => {
  if (!selectedMember.value) return;
  const userId = selectedMember.value.id;

  try {
    await request.delete(`/cheongsan/admin/users/${userId}`);
    members.value = members.value.filter((m) => m.id !== userId);
    showModal.value = false;
    selectedMember.value = null;

    uiStore.openModal({
      title: "삭제 완료",
      message: "회원이 정상적으로 삭제되었습니다.",
      isError: false,
    });
  } catch (e) {
    console.error("사용자 삭제 실패:", e);
    uiStore.openModal({
      title: "삭제 실패",
      message: "사용자 삭제에 실패했습니다.",
      isError: true,
    });
  }
};

const cancelDelete = () => {
  showModal.value = false;
};

const displayMemberName = computed(
  () => selectedMember.value?.nickname || "회원"
);

const logout = async () => {
  try {
    if (auth.state.refreshToken) {
      await request.post("/cheongsan/auth/logout", {
        refreshToken: auth.state.refreshToken,
      });
    }
  } catch (_) {
  } finally {
    auth.logout();
    router.replace("/login");
  }
};
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
              <th :class="styles.thName">ID</th>
              <th :class="styles.thDate">가입일</th>
              <th :class="styles.thAction"></th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(member, index) in members" :key="member.id">
              <td :class="styles.tdName">{{ member.userId }}</td>
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
