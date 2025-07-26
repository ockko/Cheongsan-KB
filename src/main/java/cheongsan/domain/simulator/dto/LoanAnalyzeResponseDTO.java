package cheongsan.domain.simulator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanAnalyzeResponseDTO {
    private List<AnalysisDTO> analysis = new ArrayList<>();
    private List<ImpactDTO> impactGraphData = new ArrayList<>();
    private List<RecommendationDTO> recommendations = new ArrayList<>();
}
