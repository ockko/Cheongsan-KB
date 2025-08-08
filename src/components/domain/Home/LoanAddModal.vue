<script>
import axios from 'axios';

export default {
  name: "LoanForm",
  server: {
    proxy: {
      '/dashboard': {
        target: 'http://localhost:8080', // 백엔드 서버 주소
        changeOrigin: true,
        rewrite: path => path.replace(/^\/dashboard/, '/dashboard')
      }
    }
  },
  data() {
    return {
      debtName: "",
      institutionName: "",
      resAccount: "", // 계좌번호 추가
      repaymentMethod: "",
      formData: {
        originalAmount: "",
        interestRate: "",
        loanYear: "",
        loanMonth: "",
        loanDay: "",
        totalRepaymentPeriod: "",
        remainingAmount: "",
        gracePeriod: ""
      },
      fields: [
        { label: "원금", model: "originalAmount", unit: "원" },
        { label: "이자율", model: "interestRate", unit: "%" },
        { label: "대출 시작일", model: "loanStartDate", unit: "" },
        { label: "총 상환 기간", model: "totalRepaymentPeriod", unit: "개월" },
        { label: "남은 상환액", model: "remainingAmount", unit: "원" },
        { label: "거치 기간", model: "gracePeriod", unit: "개월" }
      ]
    };
  },
  methods: {
    async submitForm() {
      try {
        const token = localStorage.getItem("accessToken"); // JWT 토큰 가져오기

        // 날짜 조합 (YYYY-MM-DD)
        const loanStartDate = `${this.formData.loanYear}-${String(this.formData.loanMonth).padStart(2, "0")}-${String(this.formData.loanDay).padStart(2, "0")}`;

        // API 요청 데이터 매핑
        const payload = {
          debtName: this.debtName,
          organizationName: this.institutionName,
          resAccount: this.resAccount,
          originalAmount: Number(this.formData.originalAmount),
          interestRate: Number(this.formData.interestRate),
          loanStartDate,
          totalMonths: Number(this.formData.totalRepaymentPeriod),
          currentBalance: Number(this.formData.remainingAmount),
          gracePeriodMonths: Number(this.formData.gracePeriod),
          repaymentMethod: this.repaymentMethod
        };

        const response = await axios.post(
          "/dashboard/loans",
          payload,
          {
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${token}`
            }
          }
        );

        if (response.data.message === "대출 상품 추가 성공") {
          alert("대출이 성공적으로 추가되었습니다.");
          // this.$router.push("/"); // 다음 페이지로 이동
        } else {
          alert(response.data.message);
        }

      } catch (error) {
        console.error(error);
        alert("대출 등록 중 오류가 발생했습니다.");
      }
    }
  }
};
</script>

<template>
  <div class="modal-overlay">
    <div class="modal-container">
      <form class="form">
        <!-- 대출명 -->
      <div class="form-row">
        <label class="title-label">대출명 :</label>
        <input
          v-model="debtName"
          type="text"
          class="text-input"
        />
      </div>

      <!-- 대출 기관명 -->
      <div class="form-row">
        <label class="sub-label">대출 기관명 :</label>
        <input
          v-model="institutionName"
          type="text"
          class="text-input"
        />
      </div>
      </form>
      
      <form class="form">
        <div>
          <div 
            class="form-row" 
            v-for="(field, index) in fields" 
            :key="index"
          >
            <div class="dot">
              <img src="/images/dot-icon.png" alt="dot">
            </div>
            <div class="form-label">{{ field.label }}</div>

            <div class="form-control" v-if="field.model !== 'loanStartDate'">
              <input
                v-model="formData[field.model]"
                type="number"
                class="text-input"
              />
              {{ field.unit }}
            </div>

            <!-- 대출 시작일 전용 UI -->
            <div class="form-control" v-else>
              <input
                v-model="formData.loanYear"
                type="text"
                class="year-input"
              /> 년
              <input
                v-model="formData.loanMonth"
                type="text"
                class="date-input"
              /> 월
              <input
                v-model="formData.loanDay"
                type="text"
                class="date-input"
              /> 일
            </div>
          </div>
      </div>

        <div class="form-row">
            <img src="/images/dot-icon.png" alt="dot">
            <div class="form-label">상환 방식</div>
            <div class="form-control">
                <select v-model="repaymentMethod" class="repayment-select">
                    <option>원금 균등 상환</option>
                    <option>원리금 균등 상환</option>
                    <option>만기 일시 상환</option>
                </select>
            </div>
        </div>

        <button type="submit" class="submit-btn" @click="submitForm">추가하기</button>
      </form>
    </div>
  </div>
</template>

<style scoped>
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal-container {
  background: #fff;
  padding: 2rem;
  border-radius: 12px;
  height: 80%;
  width: 90%;
  max-width: 340px;
  margin: 0 auto; 
  border: 2px solid #004b6e;
}
.title {
  font-size: 20px;
  font-weight: bold;
  color: #004b6e;
}

.institution {
  display: flex;
  align-items: center;
  font-weight: bold;
  font-size: 16px;
  color: #555;
}

.icon {
  margin-right: 8px;
}

.form {
  display: flex;
  flex-direction: column;
}
.form-row img {
    width: 10px;
    height: 10px;
}
.form-row {
  display: flex;
  align-items: center;
  margin: -6px 0;
}
.form-label {
  width: 60px;
  text-align: left;
  margin-left: 1rem;
  font-weight: bold;
  white-space: nowrap;
  margin-right: 1rem;
  font-size: 15px;
  flex-shrink: 0;
}
.title-label {
  width: 55px;
  text-align: left;
  margin-left: 1rem;
  font-weight: bold;
  white-space: nowrap;
  margin-right: 1rem;
  font-size: 20px;
  flex-shrink: 0;
}
.sub-label {
  width: 70px;
  text-align: left;
  margin-left: 1rem;
  font-weight: bold;
  white-space: nowrap;
  margin-right: 1rem;
  font-size: 16px;
  flex-shrink: 0;
}

.form-control {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 15px;
  flex: 1;
}

.text-input {
    margin-left: 6px;
    width: 60px;
    border: none;
    border-bottom: 1px solid #333;
    text-align: center;
    font-size: 15px;
    background: transparent;
}
label {
  width: fit-content;
  margin: 0 14px;
  font-size: 14px;
}

input,
select {
  border: none;
  border-bottom: 1px solid #999;
  padding: 4px;
  flex: 1;
  outline: none;
  font-size: 14px;
}

.year-input {
  width: 45px;
  text-align: right;
}
.date-input {
  width: 16px;
  text-align: right;
}

.submit-btn {
  background: white;
  color: #004b6e;
  padding: 8px 0;
  border-radius: 20px;
  margin-top: 30px;
  cursor: pointer;
}

.submit-btn:hover {
  background: #004b6e;
  color: white;
}
</style>
