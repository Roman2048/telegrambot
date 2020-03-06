package nextg.telegrambot.repository;

import nextg.telegrambot.domain.Annotation;
import org.springframework.data.repository.CrudRepository;

public interface AnnotationRepository extends CrudRepository<Annotation, Long> {
}
