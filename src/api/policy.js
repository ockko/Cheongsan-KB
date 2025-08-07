import axios from './index';

// 정책 목록 조회
export const getPolicies = async (params = {}) => {
  try {
    const response = await axios.get('/api/policies', { params });
    return response.data;
  } catch (error) {
    console.error('정책 목록 조회 실패:', error);
    throw error;
  }
};

// 정책 상세 정보 조회
export const getPolicyDetail = async (policyId) => {
  try {
    const response = await axios.get(`/api/policies/${policyId}`);
    return response.data;
  } catch (error) {
    console.error('정책 상세 정보 조회 실패:', error);

    // 임시 테스트 데이터 반환 (개발용)
    const mockData = {
      policyNumber: '39488',
      ministryName: '보건복지부',
      departmentName: '기초생활보장과',
      policyName: '긴급복지 주거지원',
      policyTags: ['주거'],
      policySummary:
        '생계곤란 등의 위기상황에 처하여 도움이 필요한 경우 일시적으로 신속하게 지원함으로써 위기상황에서 벗어날 수 있도록 지원합니다.',
      supportAge: null,
      supportTarget: ['저소득', '위기상황가구', '주거지원필요자'],
      supportType: '현금지급,현물지급',
      supportCycle: '월',
      isOnlineApplyAvailable: 'N',
      contactNumber: '129',
      detailPageUrl:
        'https://www.bokjiro.go.kr/ssis-tbu/twataa/wlfareInfo/moveTWAT52011M.do?wlfareInfoId=WLF00000917&wlfareInfoReldBztpCd=01',
      policyId: policyId,
    };

    return mockData;
  }
};

// 정책 카테고리별 조회
export const getPoliciesByCategory = async (categoryId) => {
  try {
    const response = await axios.get(`/api/policies/category/${categoryId}`);
    return response.data;
  } catch (error) {
    console.error('카테고리별 정책 조회 실패:', error);
    throw error;
  }
};

// 정책 검색
export const searchPolicies = async (keyword) => {
  try {
    const response = await axios.get('/api/policies/search', {
      params: { keyword },
    });
    return response.data;
  } catch (error) {
    console.error('정책 검색 실패:', error);
    throw error;
  }
};

// 맞춤 지원 정책 목록 조회
export const getCustomPolicies = async (token = null) => {
  try {
    const config = {};

    // 토큰이 제공된 경우 헤더에 추가
    if (token) {
      config.headers = {
        Authorization: `Bearer ${'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbnRlaHl1bjQ4ODAiLCJpYXQiOjE3NTQ1Mjc1NDUsImV4cCI6MTc1NDUzMTE0NX0.0n8giHYmBRvS1YMoASFQY52txjjKQHBRELjmlz_-dKU'}`,
      };
    }

    const response = await axios.get(
      'http://localhost:8080/cheongsan/policies/list',
      config
    );
    return response.data;
  } catch (error) {
    console.error('맞춤 지원 정책 목록 조회 실패:', error);

    // 개발용 Mock 데이터 반환
    console.log('Mock 데이터를 사용합니다.');
    return [
      {
        ministryName: '국토교통부',
        policySummary:
          '정기적 대중교통 이용을 지원하여 대중교통을 자주 이용하는 서민·청년층 등의 교통비 부담을 완화하고 대중교통 이용을 촉진합니다.',
        policyId: 'WLF00005440',
        policyName: '대중교통비 환급 지원(K-패스)',
        supportCycle: '월',
        tagList: ['생활지원', '다문화·탈북민', '저소득'],
        policyOnline: 'N',
        policyDate: '20240430',
      },
      {
        ministryName: '보건복지부',
        policySummary:
          '우울·불안 등 정서적 어려움으로 인해 심리상담이 필요한 국민에게 전문적인 심리상담 서비스를 제공합니다.',
        policyId: 'WLF00005567',
        policyName: '전국민 마음투자 지원사업',
        supportCycle: '년',
        tagList: ['정신건강'],
        policyOnline: 'Y',
        policyDate: '20240626',
      },
      {
        ministryName: '국토교통부',
        policySummary:
          '생활이 어려운 사람에게 주거급여를 실시하여 취약계층의 주거비 부담을 완화하고 양질의 주거 수준 향상을 도모합니다.',
        policyId: 'WLF00003201',
        policyName: '주거급여(맞춤형 급여)',
        supportCycle: '월',
        tagList: ['주거', '에너지', '저소득'],
        policyOnline: 'Y',
        policyDate: '20210903',
      },
      {
        ministryName: '여성가족부',
        policySummary:
          '저소득 한부모가족 및 조손가족이 가족의 기능을 유지하고 안정된 생활을 할 수 있도록 아동 양육비를 지원합니다.',
        policyId: 'WLF00001068',
        policyName: '한부모가족 아동양육비 지원',
        supportCycle: '월',
        tagList: ['생활지원', '보육', '교육', '한부모·조손'],
        policyOnline: 'Y',
        policyDate: '20210903',
      },
      {
        ministryName: '보건복지부',
        policySummary:
          '아동복지시설 및 가정위탁보호아동이 퇴소 또는 위탁종료 시 경제적 지원을 통해 안정적인 사회정착을 도모합니다.',
        policyId: 'WLF00005445',
        policyName: '자립준비청년(보호종료아동) 자립정착금 지원',
        supportCycle: '1회성',
        tagList: ['생활지원', '입양·위탁', '보호·돌봄'],
        policyOnline: 'N',
        policyDate: '20240521',
      },
      {
        ministryName: '보건복지부',
        policySummary:
          '근로빈곤층 청년의 생계수급자 등으로의 하락을 사전에 예방하고, 일하는 중간계층 청년이 사회에 안착할 수 있도록 자산형성을 지원합니다.',
        policyId: 'WLF00000060',
        policyName: '청년내일저축계좌',
        supportCycle: '월',
        tagList: ['서민금융', '저소득'],
        policyOnline: 'Y',
        policyDate: '20210903',
      },
      {
        ministryName: '국토교통부',
        policySummary:
          '월세부담이 큰 사회초년생 등의 주택월세자금 융자를 통해 주거안정을 지원합니다.',
        policyId: 'WLF00001063',
        policyName: '주거안정 월세대출',
        supportCycle: '월',
        tagList: ['주거', '서민금융'],
        policyOnline: 'N',
        policyDate: '20210903',
      },
      {
        ministryName: '교육부',
        policySummary:
          '누구나 경제적 여건에 관계없이 의지와 능력에 따라 대학교육의 기회를 가질 수 있도록 소득연계를 통한 대학 등록금을 차등 지원합니다.',
        policyId: 'WLF00003197',
        policyName: '국가장학금(Ⅰ, Ⅱ유형)',
        supportCycle: '1회성',
        tagList: ['교육', '다자녀'],
        policyOnline: 'N',
        policyDate: '20210903',
      },
      {
        ministryName: '통일부',
        policySummary:
          '북한이탈주민의 자립과 자활을 위해  취업과 창업을 돕고, 영농활동으로 정착할 수 있도록 지원합니다.',
        policyId: 'WLF00001168',
        policyName: '(북한이탈주민)자립자활지원',
        supportCycle: '수시',
        tagList: ['일자리', '다문화·탈북민'],
        policyOnline: 'N',
        policyDate: '20210903',
      },
      {
        ministryName: '국토교통부',
        policySummary:
          '도심 내 저소득층이 현 생활권에서 안정적으로 거주할 수 있도록 임대료가 저렴한 임대주택을 지원하여 주거안정을 도모합니다.',
        policyId: 'WLF00003269',
        policyName: '기존주택 전세임대주택 지원사업',
        supportCycle: '수시',
        tagList: ['주거', '다자녀', '장애인', '저소득', '한부모·조손'],
        policyOnline: 'N',
        policyDate: '20210903',
      },
    ];
  }
};
