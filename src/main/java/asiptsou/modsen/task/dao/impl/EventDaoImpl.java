package asiptsou.modsen.task.dao.impl;

import asiptsou.modsen.task.dao.EventDao;
import asiptsou.modsen.task.model.Event;
import lombok.AllArgsConstructor;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
@AllArgsConstructor
public class EventDaoImpl implements EventDao {

  public static final String TOPIC = "topic";
  public static final String ORGANIZER = "organizer";
  public static final String DATE = "date";
  public static final String ASCENDING_SORTING = "asc";
  public static final String DESCENDING_SORTING = "asc";

  private final SessionFactory sessionFactory;

  @Override
  public Optional<Event> getById(long id) {
    return sessionFactory
        .getCurrentSession()
        .createQuery("from Event where id = :id", Event.class)
        .setParameter("id", id)
        .getResultList()
        .stream()
        .findFirst();
  }

  @Override
  public Event save(Event event) {
    sessionFactory.getCurrentSession().save(event);
    return event;
  }

  @Override
  public List<Event> getAll() {
    return sessionFactory
        .getCurrentSession()
        .createQuery("from Event", Event.class)
        .getResultList();
  }

  @Override
  public void update(Event event) {
    sessionFactory.getCurrentSession().merge(event);
  }

  @Override
  public int delete(long id) {
    return sessionFactory
        .getCurrentSession()
        .createQuery("delete from Event where id =:id")
        .setParameter("id", id)
        .executeUpdate();
  }

  @Override
  public List<Event> getAllByFilter(
      LocalDateTime from,
      LocalDateTime to,
      String organizer,
      String topic,
      boolean isSortTopic,
      boolean isSortDate,
      boolean isSortOrganizer,
      String topicSortDirection,
      String organizerSortDirection,
      String dateSortDirection) {

    CriteriaBuilder criteriaBuilder = sessionFactory.getCriteriaBuilder();
    CriteriaQuery<Event> criteriaQuery = criteriaBuilder.createQuery(Event.class);
    Root<Event> root = criteriaQuery.from(Event.class);

    Predicate dateFromTo = criteriaBuilder.between(root.get(DATE), from, to);
    Predicate organizerLike = criteriaBuilder.like(root.get(ORGANIZER), "%" + organizer + "%");
    Predicate topicLike = criteriaBuilder.like(root.get(TOPIC), "%" + topic + "%");

    criteriaQuery.select(root).where(criteriaBuilder.and(dateFromTo, organizerLike, topicLike));

    List<Order> orderList = new ArrayList<>();

    if ((isSortTopic) && (topicSortDirection.equals(ASCENDING_SORTING))) {
      orderList.add(criteriaBuilder.asc(root.get(TOPIC)));
    }
    if ((isSortTopic) && (topicSortDirection.equals(DESCENDING_SORTING))) {
      orderList.add(criteriaBuilder.desc(root.get(TOPIC)));
    }
    if ((isSortOrganizer) && (organizerSortDirection.equals(ASCENDING_SORTING))) {
      orderList.add(criteriaBuilder.asc(root.get(ORGANIZER)));
    }
    if ((isSortOrganizer) && (organizerSortDirection.equals(DESCENDING_SORTING))) {
      orderList.add(criteriaBuilder.desc(root.get(ORGANIZER)));
    }
    if ((isSortDate) && (dateSortDirection.equals(ASCENDING_SORTING))) {
      orderList.add(criteriaBuilder.asc(root.get(DATE)));
    }
    if ((isSortDate) && (dateSortDirection.equals(DESCENDING_SORTING))) {
      orderList.add(criteriaBuilder.desc(root.get(DATE)));
    }

    criteriaQuery.orderBy(orderList);

    Query<Event> query = sessionFactory.getCurrentSession().createQuery(criteriaQuery);

    return query.getResultList();
  }
}
