package co.com.proyection.events;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;

import java.util.Map;

@Configuration
@EnableConfigurationProperties(KafkaProducerProperties.class)
public class KafkaProducerConfig {
    @Bean
    public KafkaSender<String, String> kafkaSender(KafkaProducerProperties properties) {
        Map<String, Object> configuracion = Map.ofEntries(
                Map.entry(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.bootstrapServers()),
                Map.entry(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class),
                Map.entry(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class),
                // Con ACKS=all + reintentos se prioriza no perder eventos por
                // encima de la latencia, acorde al requisito de "no perder eventos".
                Map.entry(ProducerConfig.ACKS_CONFIG, "all"),
                Map.entry(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true),
                Map.entry(ProducerConfig.RETRIES_CONFIG, 5)
        );
        SenderOptions<String, String> senderOptions = SenderOptions.create(configuracion);
        return KafkaSender.create(senderOptions);
    }

}
