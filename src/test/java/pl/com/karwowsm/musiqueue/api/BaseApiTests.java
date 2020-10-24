package pl.com.karwowsm.musiqueue.api;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.ActiveProfiles;
import pl.com.karwowsm.musiqueue.persistence.model.UserAccount;
import pl.com.karwowsm.musiqueue.persistence.repository.RoomMemberRepository;
import pl.com.karwowsm.musiqueue.persistence.repository.RoomRepository;
import pl.com.karwowsm.musiqueue.persistence.repository.RoomTrackRepository;
import pl.com.karwowsm.musiqueue.persistence.repository.TrackRepository;
import pl.com.karwowsm.musiqueue.persistence.repository.UserAccountRepository;
import pl.com.karwowsm.musiqueue.security.JWTProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseApiTests {

    @Autowired
    protected UserAccountRepository userAccountRepository;

    @Autowired
    protected RoomRepository roomRepository;

    @Autowired
    protected RoomMemberRepository roomMemberRepository;

    @Autowired
    protected TrackRepository trackRepository;

    @Autowired
    protected RoomTrackRepository roomTrackRepository;

    protected UserAccount userAccount1;

    protected UserAccount userAccount2;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JWTProvider jwtProvider;

    private JWTProvider.Token token1;

    private JWTProvider.Token token2;

    @BeforeEach
    void init() {
        roomRepository.deleteAll();
        roomTrackRepository.deleteAll();
        trackRepository.deleteAll();
        userAccountRepository.deleteAll();
        userAccount1 = userAccountRepository.saveAndFlush(UserAccount.builder()
            .username("test1")
            .password("test1")
            .email("test1@test1")
            .build());
        userAccount2 = userAccountRepository.saveAndFlush(UserAccount.builder()
            .username("test2")
            .password("test2")
            .email("test2@test2")
            .build());
        token1 = jwtProvider.generateToken(userAccount1.getId().toString());
        token2 = jwtProvider.generateToken(userAccount2.getId().toString());
        restTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    }

    protected <T> ResponseEntity<T> send(HttpMethod httpMethod, String path, Object body, Class<T> responseType) {
        return restTemplate.exchange(path, httpMethod, buildHttpEntity(body), responseType);
    }

    protected <T> ResponseEntity<T> send(HttpMethod httpMethod, String path, Object body, ParameterizedTypeReference<T> responseType) {
        return restTemplate.exchange(path, httpMethod, buildHttpEntity(body), responseType);
    }

    protected <T> ResponseEntity<T> send(HttpMethod httpMethod, String path, HttpEntity<?> httpEntity, Class<T> responseType) {
        return restTemplate.exchange(path, httpMethod, httpEntity, responseType);
    }

    protected <T> CompletableFuture<List<ResponseEntity<T>>> sendAsync(HttpMethod httpMethod, String path, int n, Class<T> responseType) {
        return sendAsync(httpMethod, path, IntStream.range(0, n).mapToObj(it -> null), responseType);
    }

    protected <T> CompletableFuture<List<ResponseEntity<T>>> sendAsync(HttpMethod httpMethod, String path, Stream<Object> bodies, Class<T> responseType) {
        List<CompletableFuture<ResponseEntity<T>>> futuresList = bodies
            .map(request -> CompletableFuture.supplyAsync(() -> send(httpMethod, path, request, responseType)))
            .collect(Collectors.toList());

        CompletableFuture<Void> future = CompletableFuture.allOf(futuresList.toArray(new CompletableFuture[0]));

        return future.thenApply(v -> futuresList.stream().map(CompletableFuture::join).collect(Collectors.toList()));
    }

    protected <T> HttpEntity<T> buildHttpEntity(T body) {
        return new HttpEntity<>(body, buildHttpHeaders());
    }

    protected HttpHeaders buildHttpHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        JWTProvider.Token token = (int) (Math.random() * 2) == 0
            ? token1
            : token2;
        httpHeaders.add("Authorization", token.getToken_type() + " " + token.getAccess_token());

        return httpHeaders;
    }
}
