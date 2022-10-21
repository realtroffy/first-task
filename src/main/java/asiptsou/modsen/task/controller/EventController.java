package asiptsou.modsen.task.controller;

import asiptsou.modsen.task.model.EventFilter;
import asiptsou.modsen.task.model.dto.EventDto;
import asiptsou.modsen.task.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/events")
@AllArgsConstructor
public class EventController {

  private final EventService eventService;

  @Operation(summary = "Get event by its id")
  @GetMapping("/{id}")
  public ResponseEntity<EventDto> getById(
      @Parameter(description = "Id of event to be searched") @PathVariable long id) {
    return ResponseEntity.ok(eventService.getById(id));
  }

  @Operation(summary = "Save event. For date use pattern: dd-MM-yyyy HH:mm")
  @PostMapping()
  public ResponseEntity<EventDto> save(@RequestBody @Valid EventDto eventDto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(eventService.save(eventDto));
  }

  @Operation(summary = "Get all events")
  @GetMapping()
  public ResponseEntity<List<EventDto>> getAll() {
    return ResponseEntity.ok(eventService.getAll());
  }

  @Operation(summary = "Get all events by filter. You can choose filter and sorting parameters")
  @GetMapping("/filter")
  public ResponseEntity<List<EventDto>> getEventByFilter(
      @Parameter(
              description =
                  "Part of topic's name for searching. Example 'ar' could return topics 'paragraph' or 'arcade'")
          @RequestParam(required = false, defaultValue = "")
          String topic,
      @Parameter(
              description =
                  "Choose 'true' if you want for sorting by topic. After that you must choose direction for sorting(mandatory)")
          @RequestParam(required = false)
          boolean isSortTopic,
      @Parameter(
              description = "Choose 'asc' for ascending sorting or 'desc' for descending sorting")
          @RequestParam(required = false, defaultValue = "")
          String topicSortDirection,
      @Parameter(
              description =
                  "Part of organizer's name for searching. Example 'ar' could return organizers 'paragraph' or 'arcade'")
          @RequestParam(required = false, defaultValue = "")
          String organizer,
      @Parameter(
              description =
                  "Choose 'true' if you want for sorting by organizer. After that you must choose direction for sorting(mandatory)")
          @RequestParam(required = false)
          boolean isSortOrganizer,
      @Parameter(
              description = "Choose 'asc' for ascending sorting or 'desc' for descending sorting")
          @RequestParam(required = false, defaultValue = "")
          String organizerSortDirection,
      @Parameter(
              description =
                  "First date for search events. Use pattern 'dd-MM-yyyy HH:mm'. Default value '01-02-1990 00:00'")
          @RequestParam(required = false)
          @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
          LocalDateTime fromDate,
      @Parameter(
              description =
                  "Last date for search events. Use pattern 'dd-MM-yyyy HH:mm'. Default value '01-02-3000 00:00'")
          @RequestParam(required = false)
          @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
          LocalDateTime toDate,
      @Parameter(
              description =
                  "Choose 'true' if you want for sorting by date. After that you must choose direction for sorting(mandatory)")
          @RequestParam(required = false)
          boolean isSortDate,
      @Parameter(
              description = "Choose 'asc' for ascending sorting or 'desc' for descending sorting")
          @RequestParam(required = false, defaultValue = "")
          String dateSortDirection) {

    EventFilter eventFilter =
        EventFilter.builder()
            .topic(topic)
            .isSortTopic(isSortTopic)
            .topicSortDirection(topicSortDirection)
            .organizer(organizer)
            .isSortOrganizer(isSortOrganizer)
            .organizerSortDirection(organizerSortDirection)
            .fromDate(fromDate)
            .toDate(toDate)
            .isSortDate(isSortDate)
            .dateSortDirection(dateSortDirection)
            .build();

    List<EventDto> eventList = eventService.getListEventByFilter(eventFilter);

    return eventList.isEmpty() ? noContent().build() : ok(eventList);
  }

  @Operation(summary = "Delete event by id")
  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteById(
      @Parameter(description = "Id of event to be deleted") @PathVariable long id) {
    int deletedRow = eventService.deleteById(id);
    return deletedRow > 0
        ? ok().body("Event was successfully deleted with id = " + id)
        : ok().body("Event was not found by such id = " + id);
  }

  @Operation(summary = "Update event by id and new body")
  @PutMapping("/{id}")
  public ResponseEntity<Void> update(
      @Parameter(description = "Id of event to be updated") @PathVariable("id") long id,
      @Valid @RequestBody EventDto eventDto) {
    eventService.update(id, eventDto);
    return ResponseEntity.noContent().build();
  }
}
