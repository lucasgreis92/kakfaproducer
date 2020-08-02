package br.com.lgrapplications.kafka.kafkaproducer.configurations;

import com.google.common.base.Predicates;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.*;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SecurityConfigurationBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class SwaggerConfiguration {

    @Value("${keycloak.auth-server-url}")
    private String AUTH_SERVER;

    @Value("${keycloak.credentials.secret}")
    private String CLIENT_SECRET;

    @Value("${keycloak.resource}")
    private String CLIENT_ID;

    @Value("${keycloak.realm}")
    private String REALM;

    @Value("${keycloak.resource}")
    private String RESOURCE;

    @Value("${swagger.base-package}")
    private String BASEPACKAGE;


    @Bean
    public SecurityConfiguration securityConfiguration() {

        Map<String, Object> additionalQueryStringParams=new HashMap<>();
        additionalQueryStringParams.put("nonce","123456");

        return SecurityConfigurationBuilder.builder()
                .clientId(CLIENT_ID).realm(REALM).appName(RESOURCE)
                .clientSecret(CLIENT_SECRET)
                .additionalQueryStringParams(additionalQueryStringParams)
                .build();
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage(BASEPACKAGE))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo())
                // .ignoredParameterTypes(Page.class)
                .securitySchemes(buildSecurityScheme()).securityContexts(buildSecurityContext());

    }

    private ApiInfo apiInfo() {
        ApiInfo apiInfo = new ApiInfoBuilder()
                .title ("API Database WS")
                .description ("Essa é a API responsável por comunicar com o banco de dados e disponibilizar os dados via REST.")
                /*           .license("Apache License Version 2.0")
                           .licenseUrl("https://www.apache.org/licenses/LICENSE-2.0")
                           .termsOfServiceUrl("/service.html")*/
                .version("1.0.0")
                .contact(new Contact("Lucas Gabriel Reis","http://www.spintec.com.br/", "lucas.reis@spintec.com.br"))
                .build();

        return apiInfo;
    }

    private List<SecurityContext> buildSecurityContext() {
        List<SecurityReference> securityReferences = new ArrayList<>();

        securityReferences.add(SecurityReference.builder().reference("oauth2").scopes(scopes().toArray(new AuthorizationScope[]{})).build());

        SecurityContext context = SecurityContext.builder().forPaths(Predicates.alwaysTrue()).securityReferences(securityReferences).build();

        List<SecurityContext> ret = new ArrayList<>();
        ret.add(context);
        return ret;
    }

    private List<? extends SecurityScheme> buildSecurityScheme() {
        List<SecurityScheme> lst = new ArrayList<>();
        // lst.add(new ApiKey("api_key", "X-API-KEY", "header"));

        LoginEndpoint login = new LoginEndpointBuilder().url(AUTH_SERVER+"/realms/"+REALM+"/protocol/openid-connect/auth").build();

        List<GrantType> gTypes = new ArrayList<>();
        gTypes.add(new ImplicitGrant(login, "acces_token"));

        lst.add(new OAuth("oauth2",  scopes(), gTypes));
        return lst;
    }

    private List<AuthorizationScope> scopes() {
        List<AuthorizationScope> scopes = new ArrayList<>();
        for (String scopeItem : new String[]{"openid=openid"}) {
            String scope[] = scopeItem.split("=");
            if (scope.length == 2) {
                scopes.add(new AuthorizationScopeBuilder().scope(scope[0]).description(scope[1]).build());
            } else {
                System.out.println("Scope "+scopeItem+" is not valid (format is scope=description) ");
            }
        }

        return scopes;
    }
}
