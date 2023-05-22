package me.andrewberger.graphql.example;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.idl.SchemaDirectiveWiringEnvironment;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import com.apollographql.federation.graphqljava.tracing.HTTPRequestHeaders;
import me.andrewberger.graphql.directives.auth.DefaultAuthDirectiveConfig;

@Component
public class AuthConfig extends DefaultAuthDirectiveConfig {

    @NotNull
    @Override
    public Object dataFetcherAuthWrapper(@NotNull DataFetcher originalDataFetcher, @NotNull DataFetchingEnvironment wrapperDataFetchingEnvironment,
                                         @NotNull SchemaDirectiveWiringEnvironment environment) throws Exception {
        HTTPRequestHeaders context = wrapperDataFetchingEnvironment.getContext();
        String auth = context.getHTTPRequestHeader("auth");
        if ("admin".equals(auth)){
            return originalDataFetcher.get(wrapperDataFetchingEnvironment);
        } else {
            throw new Exception("Not Authorized");
        }
    }
}
