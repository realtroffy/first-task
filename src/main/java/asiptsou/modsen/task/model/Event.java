package asiptsou.modsen.task.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "EVENT")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Event implements Serializable {

  private static final long serialVersionUID = 7143289884507285773L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "event_id_generator")
  @SequenceGenerator(
      name = "event_id_generator",
      sequenceName = "event_id_sequence",
      allocationSize = 3)
  @Column(name = "ID")
  private Long id;

  @Column(name = "TOPIC")
  private String topic;

  @Column(name = "DESCRIPTION")
  private String description;

  @Column(name = "ORGANIZER")
  private String organizer;

  @Column(name = "DATE")
  private LocalDateTime date;

  @Column(name = "LOCATION")
  private String location;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (o == null || getClass() != o.getClass()) return false;

    Event event = (Event) o;

    return new EqualsBuilder()
        .append(topic, event.topic)
        .append(description, event.description)
        .append(organizer, event.organizer)
        .append(date, event.date)
        .append(location, event.location)
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
