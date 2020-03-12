package nextg.telegrambot.service;

import nextg.telegrambot.domain.Annotation;
import nextg.telegrambot.exception.AnnotationNotFound;
import nextg.telegrambot.repository.AnnotationRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class AnnotationService {

    AnnotationRepository annotationRepository;

    public AnnotationService(AnnotationRepository annotationRepository) {
        this.annotationRepository = annotationRepository;
    }

    public List<Annotation> getAll() {
        List<Annotation> annotations = new ArrayList<>();
        annotationRepository.findAll().forEach(annotations::add);
        return annotations;
    }

    public Annotation getOne(Long id) {
        return annotationRepository.findById(id).orElseThrow(AnnotationNotFound::new);
    }
}
