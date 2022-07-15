package dk.kmd.cnap.examples.spring.boot.rest.kafka.json.service.config;

import dk.kmd.cnap.examples.spring.boot.rest.kafka.json.service.model.message.GreetingCreateRequest;
import dk.kmd.cnap.examples.spring.boot.rest.kafka.json.service.model.message.GreetingCreateResponse;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;


@Configuration
@EnableKafka
public class KafkaConfig implements KafkaTopicName {

  /**  port are the addresses of the
   * Kafka brokers in a "bootstrap" Kafka cluster   */
  @Value("${spring.kafka.bootstrap.servers}")
  private String kafkaBootstrapServers;

  /** Replicas are the number of copies for the partitions in the topic */
  @Value("${spring.kafka.replicas:1}")
  private Integer replicas;

  /** a partition is the smallest storage unit that holds a subset of records owned by a topic  */
  @Value("${spring.kafka.partitions:1}")
  private Integer partitions;

  // Shared Kafka properties

  @Bean
  public Map kafkaProperties() {
    return Map.of(
        BOOTSTRAP_SERVERS_CONFIG,
        kafkaBootstrapServers,
        JsonDeserializer.TRUSTED_PACKAGES, "*"
    );
  }

  // Kafka topics
  @Bean
  public KafkaAdmin kafkaAdmin() {
    Map<String, Object> configs = new HashMap<>();
    configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
    return new KafkaAdmin(configs);
  }

  /** This method creates public.command.kmd.cnap.examples.spring.boot.rest.kafka.json.greeting.create.request topic with
   * specified number of partitions and replicas.*/
  @Bean
  public NewTopic topicCreateMessageRequest() {
    return TopicBuilder.name(TOPIC_GREETING_CREATE_REQUEST)
        .partitions(partitions)
        .replicas(replicas)
        .build();
  }

  /** This method creates public.command.kmd.cnap.examples.spring.boot.rest.kafka.json.greeting.create.response topic with
   * specified number of partitions and replicas.*/
  @Bean
  public NewTopic topicCreateMessageResponse() {
    return TopicBuilder.name(TOPIC_GREETING_CREATE_RESPONSE)
        .partitions(partitions)
        .replicas(replicas)
        .build();
  }

  // Kafka factories for listeners

  @Bean("greetingCreateRequestKafkaListenerContainerFactory")
  public ConcurrentKafkaListenerContainerFactory<String, GreetingCreateRequest>
  greetingCreateRequestKafkaListenerContainerFactory(final Map kafkaProperties) {
    ConcurrentKafkaListenerContainerFactory<String, GreetingCreateRequest>
        concurrentContainerFactory = new ConcurrentKafkaListenerContainerFactory<>();
    concurrentContainerFactory.setConcurrency(partitions);
    DefaultKafkaConsumerFactory consumerFactory = new DefaultKafkaConsumerFactory(kafkaProperties, new StringDeserializer(), new JsonSerde<GreetingCreateRequest>().deserializer());
    concurrentContainerFactory.setConsumerFactory(consumerFactory);
    return concurrentContainerFactory;
  }

  @Bean("greetingCreateResponseKafkaListenerContainerFactory")
  public ConcurrentKafkaListenerContainerFactory<String, GreetingCreateResponse>
  greetingCreateResponseKafkaListenerContainerFactory(final Map kafkaProperties) {
    ConcurrentKafkaListenerContainerFactory<String, GreetingCreateResponse>
        concurrentContainerFactory = new ConcurrentKafkaListenerContainerFactory<>();
    concurrentContainerFactory.setConcurrency(partitions);
    DefaultKafkaConsumerFactory consumerFactory = new DefaultKafkaConsumerFactory(kafkaProperties, new StringDeserializer(), new JsonSerde<GreetingCreateResponse>().deserializer());
    concurrentContainerFactory.setConsumerFactory(consumerFactory);
    return concurrentContainerFactory;
  }

  // Kafka producers

  @Bean
  public KafkaProducer<String, GreetingCreateRequest> greetingCreateRequestKafkaProducer(
      final Map kafkaProperties) {
    return new KafkaProducer(kafkaProperties, new StringSerializer(), new JsonSerde<GreetingCreateRequest>().serializer());
  }

  @Bean
  public KafkaProducer<String, GreetingCreateResponse> greetingCreateResponseKafkaProducer(
      final Map kafkaProperties) {
    return new KafkaProducer(kafkaProperties, new StringSerializer(), new JsonSerde<GreetingCreateResponse>().serializer());
  }
}
