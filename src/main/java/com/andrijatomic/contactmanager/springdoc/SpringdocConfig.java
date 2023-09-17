package com.andrijatomic.contactmanager.springdoc;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

@OpenAPIDefinition
@Configuration
public class SpringdocConfig {
  @Bean
  public OpenAPI baseOpenApi() {

    ApiResponse appUserNotFoundRequestApi = new ApiResponse().content(
        new Content().addMediaType(MediaType.APPLICATION_JSON_VALUE,
            new io.swagger.v3.oas.models.media.MediaType().addExamples("default",
                new Example().value("{\"code\" : 404, \"Status\" : \"Not found\","
                    + " \"Message\" :\"App user not found.\"}")))
    ).description("Couldn't find user with the provided TSID");

    ApiResponse contactNotFoundRequestApi = new ApiResponse().content(
        new Content().addMediaType(MediaType.APPLICATION_JSON_VALUE,
            new io.swagger.v3.oas.models.media.MediaType().addExamples("default",
                new Example().value("{\"code\" : 404, \"Status\" : \"Not found\","
                    + " \"Message\" :\"Contact not found.\"}")))
    ).description("Couldn't find contact because the user doesn't own it");

    ApiResponse badRequestApi = new ApiResponse().content(
        new Content().addMediaType(MediaType.APPLICATION_JSON_VALUE,
            new io.swagger.v3.oas.models.media.MediaType().addExamples("default",
                new Example().value("{\"code\" : 400, \"Status\" : \"Bad request\","
                    + " \"Message\" :\"Invalid request body or parameters\"}")))
    ).description("Bad request");

    ApiResponse internalServerErrorResponseApi = new ApiResponse().content(
        new Content().addMediaType(MediaType.APPLICATION_JSON_VALUE,
            new io.swagger.v3.oas.models.media.MediaType().addExamples("default",
                new Example().value("{\"code\" : 500, \"Status\" : \"Internal server error\","
                    + " \"Message\" :\"Could not process the request\"}")))
    ).description("Internal server error");

    Components components = new Components();
    components.addResponses("appUserNotFound", appUserNotFoundRequestApi);
    components.addResponses("contactNotFound", contactNotFoundRequestApi);
    components.addResponses("badRequest", badRequestApi);
    components.addResponses("internalServerError", internalServerErrorResponseApi);

    return new OpenAPI().components(components)
        .info(new Info().title("Contact manager REST API")
        .version("1.0.0")
        .description("Doc for managing users and contacts"));
  }

  @Bean
  public GroupedOpenApi contactApi() {
    String[] paths = {"/contact/**"};
    return GroupedOpenApi.builder()
        .group("Contact resource")
        .displayName("Contact resource")
        .pathsToMatch(paths)
        .build();
  }
  @Bean
  public GroupedOpenApi userApi() {
    String[] paths = {"/user/**"};
    return GroupedOpenApi.builder()
        .group("User resource")
        .displayName("User resource")
        .pathsToMatch(paths)
        .build();
  }

  @Bean
  public GroupedOpenApi verificationApi() {
    String[] paths = {"/verification/**"};
    return GroupedOpenApi.builder()
        .group("Verification resource")
        .displayName("Verification resource")
        .pathsToMatch(paths)
        .build();
  }

  @Bean
  public GroupedOpenApi contactTypeApi() {
    String[] paths = {"/contact-type/**"};
    return GroupedOpenApi.builder()
        .group("Contact type resource")
        .displayName("Contact type resource")
        .pathsToMatch(paths)
        .build();
  }

  @Bean
  public GroupedOpenApi authenticationApi() {
    String[] paths = {"/login"};
    return GroupedOpenApi.builder()
        .group("Authentication resource")
        .displayName("Authentication resource")
        .pathsToMatch(paths)
        .build();
  }
}
