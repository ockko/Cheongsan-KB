# temp-vue

This template should help get you started developing with Vue 3 in Vite.

## Recommended IDE Setup

[VSCode](https://code.visualstudio.com/) + [Volar](https://marketplace.visualstudio.com/items?itemName=Vue.volar) (and disable Vetur).

## Customize configuration

See [Vite Configuration Reference](https://vite.dev/config/).

## Project Setup

```sh
npm install
```

### Compile and Hot-Reload for Development

```sh
npm run dev
```

### Compile and Minify for Production

```sh
npm run build
```

## 부처 로고 설정

### 지원하는 정부 부처 (20개)

`PolicySection` 컴포넌트는 다음 20개 정부 부처의 로고를 지원합니다:

| 부처명             | 영문 약자 | 로고 파일명  |
| ------------------ | --------- | ------------ |
| 고용노동부         | MOEL      | `moel.jpg`   |
| 보건복지부         | MOHW      | `mohw.jpg`   |
| 산업통상자원부     | MOTIE     | `motie.jpg`  |
| 여성가족부         | MOGEF     | `mogef.jpg`  |
| 교육부             | MOE       | `moe.jpg`    |
| 통일부             | MOU       | `mou.jpg`    |
| 문화체육관광부     | MOCST     | `mocst.jpg`  |
| 농림축산식품부     | MOAFRA    | `moafra.jpg` |
| 금융위원회         | FSC       | `fsc.jpg`    |
| 국가보훈부         | MOPVA     | `mopva.jpg`  |
| 행정안전부         | MOIS      | `mois.jpg`   |
| 과학기술정보통신부 | MOSI      | `mosi.jpg`   |
| 해양수산부         | MOOF      | `moof.jpg`   |
| 기획재정부         | MOEF      | `moef.jpg`   |
| 산림청             | KFS       | `kfs.jpg`    |
| 중소벤처기업부     | MOSS      | `moss.jpg`   |
| 질병관리청         | KDCPA     | `kdcpa.jpg`  |
| 환경부             | MOEN      | `moen.jpg`   |
| 국토교통부         | MOLIT     | `molit.jpg`  |

### 로고 파일 설정

1. `public/minister-logos/` 디렉토리에 각 부처의 로고 이미지를 업로드
2. 파일명은 위 표의 "로고 파일명" 컬럼과 정확히 일치해야 함
3. 지원 형식: PNG, JPG, SVG (권장: PNG, 40x40px)

### 사용 방법

```javascript
// PolicySection.vue에서 자동으로 로고 매핑
const getMinisterLogo = (logoText) => {
  // logoText에 따라 자동으로 해당 로고 파일을 반환
  return logoMapping[logoText] || '/images/default-logo.png';
};
```
