package com.leadnews.kafka.stream;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Arrays;

/**
 * @author lihaohui
 * @date 2023/8/30
 */
@Component
@Slf4j
public class KafkaStreamHelloListener {

    @Bean
    public KStream<String, String> kStream(StreamsBuilder builder) {
        // Serializers/deserializers (serde) for String and Long types
        final Serde<String> stringSerde = Serdes.String();
        final Serde<Long> longSerde = Serdes.Long();

// Construct a `KStream` from the input topic "streams-plaintext-input", where message values
// represent lines of text (for the sake of this example, we ignore whatever may be stored
// in the message keys).
        KStream<String, String> textLines = builder.stream(
                "streams-plaintext-input",
                Consumed.with(stringSerde, stringSerde)
        );

        KTable<String, Long> wordCounts = textLines
                // Split each text line, by whitespace, into words.
                .flatMapValues(value -> Arrays.asList(value.toLowerCase().split("\\W+")))

                // Group the text words as message keys
                .groupBy((key, value) -> value)

                // Count the occurrences of each word (message key).
                .count();

// Store the running counts as a changelog stream to the output topic.
        wordCounts.toStream()
                .map((key, value) -> {
                    System.out.println("key:" + key + ",value:" + value);
                    return new KeyValue<>(key.toString(), value);
                })
                .to("streams-wordcount-output", Produced.with(Serdes.String(), Serdes.Long()));

        return textLines;
    }
}
