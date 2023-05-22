package me.andrewberger.graphql.example.datafetchers;

import graphql.schema.DataFetcher;

import java.util.concurrent.CompletableFuture;

import org.dataloader.DataLoader;

import me.andrewberger.graphql.annotations.DataFetchersFor;
import me.andrewberger.graphql.annotations.FieldDataFetcher;
import me.andrewberger.graphql.dataFetchers.DataFetchers;
import me.andrewberger.graphql.example.dto.Food;
import me.andrewberger.graphql.util.dataloader.DataLoaderUtil;

@DataFetchersFor(schemaType = "QueryType")
public class QueryDataFetchers implements DataFetchers {

    @FieldDataFetcher(fieldName = "food")
    public DataFetcher<CompletableFuture<Food>> foodDataFetcher() {
        return dataFetchingEnvironment -> {
            DataLoader<String, Food> foodDataLoader = DataLoaderUtil.dataLoader(dataFetchingEnvironment, String.class, Food.class);
            return foodDataLoader.load(dataFetchingEnvironment.getArgument("name"));
        };
    }

}
