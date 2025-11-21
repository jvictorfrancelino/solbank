package br.com.solbank.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI solbankOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SolBank")
                        .description("API do SolBank para cadastrar, atualizar, buscar (incluindo operações em lote) e excluir clientes, com filtros de pesquisa.")
                        .version("1.0.0"));
    }
}
