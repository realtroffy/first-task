package asiptsou.modsen.task.model.dto;

import asiptsou.modsen.task.service.LocalDateTimeDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {

  private Long id;

  @NotBlank(message = "Can't be null")
  @Size(min = 5, max = 50, message = "Topic length should be between 5 and 50 characters")
  private String topic;

  @NotBlank(message = "Can't be null")
  @Size(min = 5, max = 50, message = "Description length should be between 5 and 50 characters")
  private String description;

  @NotBlank(message = "Can't be null")
  @Size(min = 5, max = 50, message = "Organizer length should be between 5 and 50 characters")
  private String organizer;

  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @NotNull(message = "Date is mandatory field. Please use next pattern for date dd-MM-yyyy HH:mm")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")
  private LocalDateTime date;

  @NotBlank(message = "Can't be null")
  @Size(min = 5, max = 50, message = "Location length should be between 5 and 50 characters")
  private String location;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (o == null || getClass() != o.getClass()) return false;

    EventDto eventDto = (EventDto) o;

    return new EqualsBuilder()
        .append(topic, eventDto.topic)
        .append(description, eventDto.description)
        .append(organizer, eventDto.organizer)
        .append(date, eventDto.date)
        .append(location, eventDto.location)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(topic)
        .append(description)
        .append(organizer)
        .append(date)
        .append(location)
        .toHashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("id", id)
        .append("topic", topic)
        .append("description", description)
        .append("organizer", organizer)
        .append("date", date)
        .append("location", location)
        .toString();
  }
}
