package asiptsou.modsen.task.service.impl;

import asiptsou.modsen.task.converter.EventConverter;
import asiptsou.modsen.task.dao.EventDao;
import asiptsou.modsen.task.model.Event;
import asiptsou.modsen.task.dto.EventDto;
import asiptsou.modsen.task.model.EventFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

  public static final int MODIFIED_ROW_AFTER_DELETING = 1;
  public static final LocalDateTime FROM_DATE =
      LocalDateTime.parse("15-06-2025 22:49", DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
  public static final LocalDateTime TO_DATE =
      LocalDateTime.parse("15-06-2032 22:49", DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));

  private EventDto eventDto;
  private Event event;
  private EventFilter eventFilter;
  @Mock private EventDao eventDao;
  @Mock private EventConverter converter;

  @InjectMocks private EventServiceImpl eventService;

  @BeforeEach
  void setUp() {
    eventDto = new EventDto();
    eventDto.setId(1L);
    event = new Event();
    event.setId(1L);
    eventFilter =
        EventFilter.builder()
            .fromDate(FROM_DATE)
            .toDate(TO_DATE)
            .topic("topic")
            .organizer("organizer")
            .isSortOrganizer(true)
            .isSortDate(true)
            .isSortTopic(true)
            .topicSortDirection("asc")
            .dateSortDirection("asc")
            .organizerSortDirection("asc")
            .build();
  }

  @Test
  void testGetByIdWhenExist() {
    when(eventDao.getById(anyLong())).thenReturn(Optional.of(new Event()));
    when(converter.toDto(new Event())).thenReturn(new EventDto());

    eventService.getById(anyLong());

    verify(eventDao).getById(anyLong());
    verify(converter).toDto(new Event());
  }

  @Test
  void testSave_ok() {
    when(eventDao.save(new Event())).thenReturn(new Event());
    when(converter.toEntity(new EventDto())).thenReturn(new Event());

    eventService.save(new EventDto());

    verify(eventDao).save(new Event());
    verify(converter).toEntity(new EventDto());
  }

  @Test
  void testGetAll_ok() {
    when(eventDao.getAll()).thenReturn(List.of(new Event()));
    when(converter.toDto(new Event())).thenReturn(new EventDto());

    eventService.getAll();

    verify(eventDao).getAll();
    verify(converter).toDto(new Event());
  }

  @Test
  void testDeleteById_ok() {
    when(eventDao.delete(anyLong())).thenReturn(MODIFIED_ROW_AFTER_DELETING);

    eventService.deleteById(anyLong());

    verify(eventDao).delete(anyLong());
  }

  @Test
  void testUpdate_ok() {
    when(eventDao.getById(anyLong())).thenReturn(Optional.of(event));
    doNothing().when(eventDao).update(event);
    when(converter.toEntity(eventDto)).thenReturn(event);

    eventService.update(anyLong(), eventDto);

    verify(eventDao).update(event);
    verify(converter).toEntity(eventDto);
    verify(eventDao).getById(anyLong());
  }

  @Test
  void testGetListEventByFilter_ok() {
    when(eventDao.getAllByFilter(
            FROM_DATE, TO_DATE, "organizer", "topic", true, true, true, "asc", "asc", "asc"))
        .thenReturn(List.of(event));
    when(converter.toDto(event)).thenReturn(eventDto);

    eventService.getListEventByFilter(eventFilter);

    verify(eventDao)
        .getAllByFilter(
            FROM_DATE, TO_DATE, "organizer", "topic", true, true, true, "asc", "asc", "asc");
    verify(converter).toDto(event);
  }

  @Test()
  void testGetListEventByFilterWhenDateFromAfterDateTo() {
    eventFilter.setFromDate(TO_DATE);
    eventFilter.setToDate(FROM_DATE);

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class, () -> eventService.getListEventByFilter(eventFilter));

    assertEquals(
        "Date from {2032-06-15T22:49} can't be after the date to {2025-06-15T22:49}",
        exception.getMessage());

    verify(eventDao, never())
        .getAllByFilter(
            TO_DATE, FROM_DATE, "organizer", "topic", true, true, true, "asc", "asc", "asc");
    verify(converter, never()).toDto(event);
  }
}
