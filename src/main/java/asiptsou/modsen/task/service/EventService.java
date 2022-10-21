package asiptsou.modsen.task.service;

import asiptsou.modsen.task.model.dto.EventDto;
import asiptsou.modsen.task.model.EventFilter;

import java.util.List;

public interface EventService {

  EventDto getById(long id);

  EventDto save(EventDto eventDto);

  List<EventDto> getAll();

  List<EventDto> getListEventByFilter(EventFilter eventFilter);

  int deleteById(long id);

  void update(long id, EventDto eventDto);
}
