package co.com.proyection.events;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "eventos.kafka")
public record KafkaProducerProperties(
        String bootstrapServers,
        String topicEventosGuia
) {
}
