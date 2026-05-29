package com.readcovery.service;

import com.readcovery.domain.AnalysisResult;
import com.readcovery.domain.Quote;
import com.readcovery.domain.User;
import com.readcovery.dto.analysis.AnalysisResponse;
import com.readcovery.dto.analysis.openai.AnalysisJsonResult;
import com.readcovery.dto.analysis.openai.OpenAiChatRequest;
import com.readcovery.dto.analysis.openai.OpenAiChatResponse;
import com.readcovery.repository.AnalysisResultRepository;
import com.readcovery.repository.QuoteRepository;
import com.readcovery.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalysisService {

    private final WebClient openAiWebClient;
    private final QuoteRepository quoteRepository;
    private final UserRepository userRepository;
    private final AnalysisResultRepository analysisResultRepository;
    private final ObjectMapper objectMapper;

    @Value("${openai.model}")
    private String openAiModel;

    private static final int MIN_QUOTES_FOR_ANALYSIS = 3;
    private static final int CACHE_VALID_DAYS = 7;

    @Transactional
    public AnalysisResponse analyze(Long userId) {
        // 1. 사용자의 모든 인용구 수집
        List<Quote> quotes = quoteRepository.findByMyBook_UserId(userId);

        if (quotes.size() < MIN_QUOTES_FOR_ANALYSIS) {
            throw new IllegalStateException(
                    "분석을 위해 최소 " + MIN_QUOTES_FOR_ANALYSIS + "개의 인용구가 필요합니다. " +
                            "현재: " + quotes.size() + "개"
            );
        }

        // 2. 캐시 확인
        Optional<AnalysisResult> cached = analysisResultRepository.findTopByUser_IdOrderByCreatedAtDesc(userId);

        if (cached.isPresent() && !cached.get().shouldRegenerate(quotes.size())) {
            log.info("캐시 hit: userId={}", userId);
            return AnalysisResponse.from(cached.get(), true);
        }

        // 3. 캐시 없으면 새로 분석
        log.info("새 분석 시작: userId={}, 인용구={}개", userId, quotes.size());
        AnalysisJsonResult aiResult = callOpenAi(quotes);

        // 4. 결과 저장
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        AnalysisResult saved = analysisResultRepository.save(
                AnalysisResult.builder()
                        .user(user)
                        .summary(aiResult.getSummary())
                        .keywords(String.join(", ", aiResult.getKeywords()))
                        .personality(aiResult.getPersonality())
                        .quoteCountAtAnalysis(quotes.size())
                        .expiresAt(LocalDateTime.now().plusDays(CACHE_VALID_DAYS))
                        .build()
        );

        analysisResultRepository.flush();

        return AnalysisResponse.from(saved, false);
    }

    private AnalysisJsonResult callOpenAi(List<Quote> quotes) {
        // 인용구를 하나의 텍스트로 합치기
        String quotesText = quotes.stream()
                .map(q -> "- " + q.getContent())
                .collect(Collectors.joining("\n"));

        // 프롬프트 구성
        String systemPrompt = """
                당신은 독서 분석가입니다.
                사용자가 책에서 모은 인용구들을 보고, 그 사람의 관심사와 가치관을 분석해주세요.
                
                응답은 반드시 다음 JSON 형식으로만 작성하세요. 다른 텍스트는 포함하지 마세요.
                {
                  "summary": "사용자의 독서 성향을 한 줄로 요약 (50자 이내)",
                  "keywords": ["키워드1", "키워드2", "키워드3"],
                  "personality": "이 사용자의 가치관과 관심사를 3-4문장으로 분석"
                }
                """;

        String userPrompt = "다음은 사용자가 책에서 모은 인용구들입니다:\n\n" + quotesText;

        // 요청 객체 생성
        OpenAiChatRequest request = OpenAiChatRequest.builder()
                .model(openAiModel)
                .messages(List.of(
                        OpenAiChatRequest.Message.builder()
                                .role("system")
                                .content(systemPrompt)
                                .build(),
                        OpenAiChatRequest.Message.builder()
                                .role("user")
                                .content(userPrompt)
                                .build()
                ))
                .responseFormat(
                        OpenAiChatRequest.ResponseFormat.builder()
                                .type("json_object")
                                .build()
                )
                .temperature(0.7)
                .build();

        try {
            log.info("OpenAI 요청 JSON: {}", objectMapper.writeValueAsString(request));
        } catch (Exception e) {
            log.error("로그 직렬화 실패", e);
        }

        // OpenAi 호출
        OpenAiChatResponse response = openAiWebClient.post()
                .uri("/v1/chat/completions")
                .bodyValue(request)
                .retrieve()
                .onStatus(
                        status -> status.isError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    log.error("OpenAI 에러 응담: {}", errorBody);
                                    return Mono.error(
                                            new IllegalStateException("OpenAI 호출 실패: " + errorBody)
                                    );
                                })
                )
                .bodyToMono(OpenAiChatResponse.class)
                .block();

        // 응답 파싱
        if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
            throw new IllegalStateException("OpenAI 응답이 비어있습니다.");
        }

        String content = response.getChoices().get(0).getMessage().getContent();
        log.info("OpenAI 응답 수신: {}", content);

        try {
            return objectMapper.readValue(content, AnalysisJsonResult.class);
        } catch (JacksonException e) {
            log.error("OpenAI 응답 JSON 파싱 실패: {}", content, e);
            throw new IllegalStateException("AI 분석 결과를 처리할 수 없습니다.");
        }
    }
}
