package com.careplus.map.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.*;
import org.springframework.context.annotation.*;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MAP – API")
                        .description("""
                            **Recursos disponíveis:**
                            - Gerenciamento de Usuários
                            - Gerenciamento de Missões Preventivas
                            - Avatar e Gamificação (conclusão de missões, estatísticas, ranking)
                            """)
                        .version("1.0.0"));
    }
}
