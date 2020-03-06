package nextg.telegrambot.repository;

import nextg.telegrambot.domain.Update;
import org.springframework.data.repository.CrudRepository;

public interface UpdateRepository extends CrudRepository<Update, Long> {
}
