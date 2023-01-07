package zerobase.weather.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                //BASIC ERROR CONTROLLER을 없애려면 ANY()가 아니라 BASEPACKAGE("패키지명")
                .apis(RequestHandlerSelectors.basePackage("zerobase.weather"))
                //모든 API를 부르려면 ANY()를 하면 되지만, ANT(특정 패턴)를 하게 되면 특정 경로만 보여줄 수 있다.
                .paths(PathSelectors.ant("/read/*"))
                .build().apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("날씨 일기 프로젝트 : ")
                .description("날씨 일기를 CRUD 할 수 있는 백엔드 API 입니다.")
                .version("2.0")
                .build();
    }
}
