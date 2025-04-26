package eu.xfsc.train.tcr.server.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The OpenApi Spring config.
 */
@Configuration
@RequiredArgsConstructor
public class OpenApiConfig {
	
  private final Optional<BuildProperties> buildProperties;

  /**
   * The OpenApi Info bean config.
   */
  @Bean
  public OpenAPI openApiInfo() {
    String version;

    if (buildProperties.isPresent()) {
      version = buildProperties.get().getVersion();
    } else {
      version = "Development Build";
    }

    return new OpenAPI().info(new Info().version(version).title("XFSC TRAIN Project")
    		.description("This is the REST API of the XFSC TRAIN Trusted Content Resolver.")
    		.license(new License().name("Apache 2.0").url("http://www.apache.org/licenses/LICENSE-2.0")));
    }
  
}
