package asiptsou.modsen.task.converter;

import asiptsou.modsen.task.dto.EventDto;
import asiptsou.modsen.task.model.Event;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EventConverter {

  Event toEntity(EventDto eventDto);

  EventDto toDto(Event event);
}
