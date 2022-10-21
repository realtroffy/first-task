package asiptsou.modsen.task.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EventFilter {

  private String topic;
  private boolean isSortTopic;
  private String topicSortDirection;
  private String organizer;
  private boolean isSortOrganizer;
  private String organizerSortDirection;
  private LocalDateTime fromDate;
  private LocalDateTime toDate;
  private boolean isSortDate;
  private String dateSortDirection;
}
