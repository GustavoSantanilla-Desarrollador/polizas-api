package com.example.polizas;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Levanta el contexto Spring completo (incluye DataInitializer, filtro de seguridad,
 * JPA/H2 y Swagger) para detectar errores de configuracion que un test unitario de
 * dominio no puede ver.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class PolizasApiApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
        // Si el contexto no levanta (bean faltante, config invalida), este test falla.
    }

    @Test
    void sinApiKeyRetorna401() throws Exception {
        mockMvc.perform(get("/polizas"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void conApiKeyListaLasPolizasDeEjemplo() throws Exception {
        mockMvc.perform(get("/polizas").header("x-api-key", "123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].numero").value("POL-0001"));
    }

    @Test
    void swaggerUiAccesibleSinApiKey() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());
    }
}
