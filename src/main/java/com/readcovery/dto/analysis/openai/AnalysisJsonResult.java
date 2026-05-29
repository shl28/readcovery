package com.readcovery.dto.analysis.openai;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class AnalysisJsonResult {

    private String summary;
    private List<String> keywords;
    private String personality;
}
