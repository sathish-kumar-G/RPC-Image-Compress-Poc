package net.breezeware.rpccompressimagepoc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "net.breezeware" })
//@PropertySources({
//        @PropertySource(value = { "file:/usr/local/rpc/rpc-service/conf/application.properties" },
//                ignoreResourceNotFound = true),
//        @PropertySource(value = { "classpath:dynamo-auth.properties", "classpath:dynamo-aws-sns.properties",
//                "classpath:dynamo-aws-ses.properties", "classpath:dynamo-aws-s3.properties",
//                "classpath:application.properties" }) })
//@EnableJpaRepositories(basePackages = { "com.revolutionpicturecars" })
@EntityScan(basePackages = { "net.breezeware" })
public class RpcCompressImagePocApplication {

    public static void main(String[] args) {
        SpringApplication.run(RpcCompressImagePocApplication.class, args);
    }

}
