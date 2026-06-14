package com.julian.account_movement_service.integration;

import com.julian.account_movement_service.domain.model.ClienteSnapshot;
import com.julian.account_movement_service.domain.model.Cuenta;
import com.julian.account_movement_service.infrastructure.persistence.repository.ClienteSnapshotRepository;
import com.julian.account_movement_service.infrastructure.persistence.repository.CuentaRepository;
import com.julian.account_movement_service.infrastructure.persistence.repository.MovimientoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.HttpStatusCodeException;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MovimientoIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("account_db")
            .withUsername("test")
            .withPassword("test");

    @Container
    static final RabbitMQContainer rabbitMQ = new RabbitMQContainer("rabbitmq:3.13-management-alpine");

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("spring.rabbitmq.host", rabbitMQ::getHost);
        registry.add("spring.rabbitmq.port", rabbitMQ::getAmqpPort);
        registry.add("spring.rabbitmq.username", rabbitMQ::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbitMQ::getAdminPassword);

        registry.add("app.messaging.client-events.exchange", () -> "client.exchange");
        registry.add("app.messaging.client-events.created-queue", () -> "client.created.queue");
        registry.add("app.messaging.client-events.updated-queue", () -> "client.updated.queue");
        registry.add("app.messaging.client-events.created-routing-key", () -> "client.created");
        registry.add("app.messaging.client-events.updated-routing-key", () -> "client.updated");

        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registry.add("spring.flyway.enabled", () -> "true");
    }


    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClienteSnapshotRepository clientSnapshotRepository;

    @Autowired
    private CuentaRepository cuentaRepository;

    @Autowired
    private MovimientoRepository movimientoRepository;

    @LocalServerPort
    private int port;

    private final RestTemplate restTemplate = new RestTemplate();

    @BeforeEach
    void setUp() {
        movimientoRepository.deleteAll();
        cuentaRepository.deleteAll();
        clientSnapshotRepository.deleteAll();
    }

    @Test
    void shouldCreateMovementAndUpdateAccountBalance() throws Exception {
        createActiveClientSnapshot(1L);

        createAccount("478758", "AHORROS", new BigDecimal("2000.00"), 1L);

        Map<String, Object> movementRequest = new LinkedHashMap<>();
        movementRequest.put("numeroCuenta", "478758");
        movementRequest.put("fecha", "2022-02-10");
        movementRequest.put("valor", new BigDecimal("600.00"));

        ResponseEntity<String> response = postJson("/api/movimientos", movementRequest);

        assertThat(response.getStatusCode().value()).isEqualTo(201);

        JsonNode body = objectMapper.readTree(response.getBody());

        assertThat(body.get("numeroCuenta").asText()).isEqualTo("478758");
        assertThat(body.get("tipoMovimiento").asText()).isEqualTo("DEPOSITO");
        assertThat(body.get("valor").decimalValue().compareTo(new BigDecimal("600.00"))).isZero();
        assertThat(body.get("saldoDisponible").decimalValue().compareTo(new BigDecimal("2600.00"))).isZero();

        Cuenta cuenta = cuentaRepository.findByNumeroCuenta("478758")
                .orElseThrow();

        assertThat(cuenta.getSaldoDisponible().compareTo(new BigDecimal("2600.00"))).isZero();
        assertThat(movimientoRepository.findAll()).hasSize(1);
    }

    @Test
    void shouldReturnSaldoNoDisponibleWhenWithdrawalExceedsBalance() throws Exception {
        createActiveClientSnapshot(1L);

        createAccount("225487", "CORRIENTE", new BigDecimal("100.00"), 1L);

        Map<String, Object> movementRequest = new LinkedHashMap<>();
        movementRequest.put("numeroCuenta", "225487");
        movementRequest.put("fecha", "2022-02-10");
        movementRequest.put("valor", new BigDecimal("-200.00"));

        ResponseEntity<String> response = postJson("/api/movimientos", movementRequest);

        assertThat(response.getStatusCode().value()).isEqualTo(400);

        JsonNode body = objectMapper.readTree(response.getBody());

        assertThat(body.get("message").asText()).isEqualTo("Saldo no disponible");

        Cuenta cuenta = cuentaRepository.findByNumeroCuenta("225487")
                .orElseThrow();

        assertThat(cuenta.getSaldoDisponible().compareTo(new BigDecimal("100.00"))).isZero();
        assertThat(movimientoRepository.findAll()).isEmpty();
    }

    private void createActiveClientSnapshot(Long clienteId) {
        ClienteSnapshot snapshot = new ClienteSnapshot();
        snapshot.setClienteId(clienteId);
        snapshot.setNombre("Marianela Montalvo");
        snapshot.setIdentificacion("1717171717");
        snapshot.setEstado(true);

        clientSnapshotRepository.save(snapshot);
    }

    private void createAccount(
            String numeroCuenta,
            String tipoCuenta,
            BigDecimal saldoInicial,
            Long clienteId
    ) {
        Map<String, Object> accountRequest = new LinkedHashMap<>();
        accountRequest.put("numeroCuenta", numeroCuenta);
        accountRequest.put("tipoCuenta", tipoCuenta);
        accountRequest.put("saldoInicial", saldoInicial);
        accountRequest.put("estado", true);
        accountRequest.put("clienteId", clienteId);

        ResponseEntity<String> response = postJson("/api/cuentas", accountRequest);

        assertThat(response.getStatusCode().value()).isEqualTo(201);
    }

    private ResponseEntity<String> postJson(String path, Map<String, Object> body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        String url = "http://localhost:" + port + path;

        try {
            return restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    String.class
            );
        } catch (HttpStatusCodeException exception) {
            HttpHeaders responseHeaders = exception.getResponseHeaders() != null
                    ? exception.getResponseHeaders()
                    : new HttpHeaders();

            return ResponseEntity
                    .status(exception.getStatusCode())
                    .headers(responseHeaders)
                    .body(exception.getResponseBodyAsString());
        }
    }
}