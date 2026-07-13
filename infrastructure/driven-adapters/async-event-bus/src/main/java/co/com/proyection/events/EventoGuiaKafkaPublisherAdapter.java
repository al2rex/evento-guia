package co.com.proyection.events;

import co.com.proyection.model.events.EventoGuia;
import co.com.proyection.model.events.gateways.EventoGuiaPublisherGateway;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;
import tools.jackson.databind.ObjectMapper;

@Component
public class EventoGuiaKafkaPublisherAdapter implements EventoGuiaPublisherGateway {

    private final KafkaSender<String, String> kafkaSender;
    private final KafkaProducerProperties properties;
    private final ObjectMapper objectMapper;

    public EventoGuiaKafkaPublisherAdapter(KafkaSender<String, String> kafkaSender,
                                           KafkaProducerProperties properties,
                                           ObjectMapper objectMapper) {
        this.kafkaSender = kafkaSender;
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> publicar(EventoGuia evento) {
        return Mono.fromCallable(() -> objectMapper.writeValueAsString(evento))
                .map(payload -> new ProducerRecord<>(
                        properties.topicEventosGuia(),
                        evento.getNumeroGuia(),
                        payload))
                .map(record -> SenderRecord.create(record, evento.getEventoId()))
                .flatMap(senderRecord -> kafkaSender.send(Mono.just(senderRecord))
                        .next()
                        .doOnNext(result -> {
                            if (result.exception() != null) {
                                throw new IllegalStateException(
                                        "error publicando evento " + evento.getEventoId(),
                                        result.exception());
                            }
                        }))
                .then();
    }

}
