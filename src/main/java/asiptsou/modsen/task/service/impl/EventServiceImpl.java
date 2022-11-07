package asiptsou.modsen.task.service.impl;

import asiptsou.modsen.task.converter.EventConverter;
import asiptsou.modsen.task.dao.EventDao;
import asiptsou.modsen.task.dto.EventDto;
import asiptsou.modsen.task.model.Event;
import asiptsou.modsen.task.model.EventFilter;
import asiptsou.modsen.task.service.EventService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.time.LocalDateTime.of;
import static java.util.Objects.isNull;

@Service
@AllArgsConstructor
public class EventServiceImpl implements EventService {

  private static final String NO_SUCH_EVENT_EXCEPTION_MESSAGE = "Event was not found by id = ";
  private static final LocalDateTime MIN_DATE = of(1990, 1, 1, 0, 0, 0);
  private static final LocalDateTime MAX_DATE = of(3000, 1, 1, 0, 0, 0);
  private static final String DATE_ERROR = "Date from {%s} can't be after the date to {%s}";

  private final EventDao eventDao;
  private final EventConverter eventConverter;

  @Override
  @Transactional(readOnly = true)
  public EventDto getById(long id) {
    Event event =
        eventDao
            .getById(id)
            .orElseThrow(() -> new NoSuchElementException(NO_SUCH_EVENT_EXCEPTION_MESSAGE + id));
    return eventConverter.toDto(event);
  }

  @Override
  @Transactional
  public EventDto save(EventDto eventDto) {
    Event event = eventDao.save(eventConverter.toEntity(eventDto));
    eventDto.setId(event.getId());
    return eventDto;
  }

  @Override
  @Transactional(readOnly = true)
  public List<EventDto> getAll() {
    return eventDao.getAll().stream().map(eventConverter::toDto).collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public List<EventDto> getListEventByFilter(EventFilter eventFilter) {
    LocalDateTime to = isNull(eventFilter.getToDate()) ? MAX_DATE : eventFilter.getToDate();
    LocalDateTime from = isNull(eventFilter.getFromDate()) ? MIN_DATE : eventFilter.getFromDate();
    checkFromIsBeforeTo(from, to);

    return eventDao
        .getAllByFilter(
            from,
            to,
            eventFilter.getOrganizer(),
            eventFilter.getTopic(),
            eventFilter.isSortTopic(),
            eventFilter.isSortDate(),
            eventFilter.isSortOrganizer(),
            eventFilter.getTopicSortDirection(),
            eventFilter.getOrganizerSortDirection(),
            eventFilter.getDateSortDirection())
        .stream()
        .map(eventConverter::toDto)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public int deleteById(long id) {
    return eventDao.delete(id);
  }

  @Override
  @Transactional
  public void update(long id, EventDto eventDto) {
    if (eventDao.getById(id).isEmpty()) {
      throw new NoSuchElementException(NO_SUCH_EVENT_EXCEPTION_MESSAGE + id);
    }
    Event event = eventConverter.toEntity(eventDto);
    event.setId(id);
    eventDao.update(event);
  }

  private void checkFromIsBeforeTo(LocalDateTime from, LocalDateTime to) {
    if (from.isAfter(to)) {
      throw new IllegalArgumentException(format(DATE_ERROR, from, to));
    }
  }
}
