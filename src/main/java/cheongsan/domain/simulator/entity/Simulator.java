package cheongsan.domain.simulator.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Simulator {
    private String finPrdNm;      // 상품명
    private String hdlInst;       // 기관명
    private String irt;           // 상품 금리
    private String lnLmt;         // 최대 한도
    private String rltSite;       // 사이트 URL
    private String dsrCheck;      // DSR 적용 여부('Y' or 'N')
}
