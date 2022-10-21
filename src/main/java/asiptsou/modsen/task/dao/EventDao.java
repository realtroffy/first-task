package asiptsou.modsen.task.dao;

import asiptsou.modsen.task.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventDao {

  Optional<Event> getById(long id);

  Event save(Event event);

  List<Event> getAll();

  int delete(long id);

  void update(Event event);

  List<Event> getAllByFilter(
      LocalDateTime from,
      LocalDateTime to,
      String organizer,
      String topic,
      boolean isSortTopic,
      boolean isSortDate,
      boolean isSortOrganizer,
      String topicSortDirection,
      String organizerSortDirection,
      String dateSortDirection);
}
