package asiptsou.modsen.task.controller;

import asiptsou.modsen.task.exception.GlobalDefaultExceptionHandler;
import asiptsou.modsen.task.model.dto.EventDto;
import asiptsou.modsen.task.model.EventFilter;
import asiptsou.modsen.task.service.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {

  public static final String EVENT_URL = "/api/events";
  public static final String ID_VARIABLE = "/{id}";
  public static final String FILTER = "/filter";
  public static final Long EXIST_ID = 99L;
  public static final Long NOT_EXIST_ID = -2L;
  public static final int MODIFIED_ROW_AFTER_DELETING = 1;
  public static final int UNMODIFIED_ROW_AFTER_DELETING = 0;
  public static final String NOT_VALID_FIELD = "top";

  private MockMvc mvc;
  private EventDto eventDto;
  private EventFilter eventFilter;
  private ObjectMapper objectMapper;

  @Mock private EventService eventService;
  @InjectMocks private EventController eventController;

  @BeforeEach
  void setUp() {
    mvc =
        MockMvcBuilders.standaloneSetup(eventController)
            .setControllerAdvice(new GlobalDefaultExceptionHandler())
            .build();

    eventDto =
        EventDto.builder()
            .id(1L)
            .topic("topic")
            .description("description")
            .organizer("organizer")
            .date(
                LocalDateTime.parse(
                    "15-06-2025 22:49", DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")))
            .location("location")
            .build();

    eventFilter =
        EventFilter.builder()
            .topic("topic")
            .organizer("organizer")
            .topicSortDirection("asc")
            .organizerSortDirection("asc")
            .dateSortDirection("asc")
            .build();

    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
  }

  @Test
  void testGetByIdWhenExist() throws Exception {
    when(eventService.getById(anyLong())).thenReturn(eventDto);

    mvc.perform(get(EVENT_URL + ID_VARIABLE, anyLong()))
        .andDo(print())
        .andExpect(jsonPath("$.topic").value("topic"))
        .andExpect(jsonPath("$.date").value("15-06-2025 22:49"))
        .andExpect(status().isOk());

    verify(eventService).getById(anyLong());
  }

  @Test
  void testGetByIdWhenNotExist() throws Exception {
    when(eventService.getById(anyLong())).thenThrow(NoSuchElementException.class);

    mvc.perform(get(EVENT_URL + ID_VARIABLE, anyLong()))
        .andDo(print())
        .andExpect(status().isNotFound());

    verify(eventService).getById(anyLong());
  }

  @Test
  void testSaveWhenDtoValid() throws Exception {
    when(eventService.save(eventDto)).thenReturn(eventDto);

    mvc.perform(
            post(EVENT_URL)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventDto)))
        .andDo(print())
        .andExpect(status().isCreated());

    verify(eventService).save(eventDto);
  }

  @Test
  void testSaveWhenDtoNotValid() throws Exception {
    eventDto.setTopic(NOT_VALID_FIELD);

    mvc.perform(
            post(EVENT_URL)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventDto)))
        .andDo(print())
        .andExpect(status().isBadRequest());

    verify(eventService, never()).save(eventDto);
  }

  @Test
  void testGetAll_ok() throws Exception {
    when(eventService.getAll()).thenReturn(List.of(eventDto));

    mvc.perform(get(EVENT_URL)).andDo(print()).andExpect(status().isOk());

    verify(eventService).getAll();
  }

  @Test
  void testDeleteByIdWhenIdExist() throws Exception {
    when(eventService.deleteById(anyLong())).thenReturn(MODIFIED_ROW_AFTER_DELETING);

    String expected =
        mvc.perform(delete(EVENT_URL + ID_VARIABLE, EXIST_ID))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    String actual = "Event was successfully deleted with id = " + EXIST_ID;

    assertEquals(expected, actual);

    verify(eventService).deleteById(anyLong());
  }

  @Test
  void testDeleteByIdWhenIdNotExist() throws Exception {
    when(eventService.deleteById(anyLong())).thenReturn(UNMODIFIED_ROW_AFTER_DELETING);

    String expected =
        mvc.perform(delete(EVENT_URL + ID_VARIABLE, NOT_EXIST_ID))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    String actual = "Event was not found by such id = " + NOT_EXIST_ID;

    assertEquals(expected, actual);

    verify(eventService).deleteById(anyLong());
  }

  @Test
  void testUpdate_ok() throws Exception {
    doNothing().when(eventService).update(anyLong(), eq(eventDto));

    mvc.perform(
            put(EVENT_URL + ID_VARIABLE, anyLong(), eq(eventDto))
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventDto)))
        .andDo(print())
        .andExpect(status().isNoContent());

    verify(eventService).update(anyLong(), eq(eventDto));
  }

  @Test
  void testGetEventByFilter_ok() throws Exception {
    when(eventService.getListEventByFilter(eventFilter)).thenReturn(List.of(eventDto));

    mvc.perform(
            get(EVENT_URL + FILTER)
                .param("topic", "topic")
                .param("organizer", "organizer")
                .param("topicSortDirection", "asc")
                .param("organizerSortDirection", "asc")
                .param("dateSortDirection", "asc"))
        .andDo(print())
        .andExpect(status().isOk());

    verify(eventService).getListEventByFilter(eventFilter);
  }
}
