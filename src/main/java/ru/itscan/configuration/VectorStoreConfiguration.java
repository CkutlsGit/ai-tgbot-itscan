package ru.itscan.configuration;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.itscan.configuration.property.RagProperties;

import javax.sql.DataSource;

@Configuration
public class VectorStoreConfiguration {

    @Bean
    public PgVectorStore vectorStore(
            @Qualifier("vectorDataSource")DataSource dataSource,
            EmbeddingModel embeddingModel,
            RagProperties properties
            )
    {
        return PgVectorStore.builder(new JdbcTemplate(dataSource), embeddingModel)
                .initializeSchema(true)
                .vectorTableName("vector_store")
                .dimensions(properties.embedDimensions())
                .distanceType(PgVectorStore.PgDistanceType.COSINE_DISTANCE)
                .indexType(PgVectorStore.PgIndexType.HNSW)
                .build();
    }

    @Bean
    public TokenTextSplitter tokenTextSplitter(RagProperties properties) {
        return TokenTextSplitter.builder()
                .withChunkSize(properties.chunkSize())
                .withMinChunkSizeChars(properties.minChunkSize())
                .withMinChunkLengthToEmbed(properties.minChunkSizeLengthEmbed())
                .withMaxNumChunks(properties.maxChunkSize())
                .withKeepSeparator(properties.keepSeparator())
                .build();
    }
}
