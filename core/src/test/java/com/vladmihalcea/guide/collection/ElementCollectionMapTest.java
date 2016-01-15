package com.vladmihalcea.guide.collection;

import com.vladmihalcea.book.hpjp.util.AbstractTest;
import org.junit.Test;

import javax.persistence.*;
import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * <code>UnidirectionalBag</code> - Unidirectional Bag Test
 *
 * @author Vlad Mihalcea
 */
public class ElementCollectionMapTest extends AbstractTest {

    @Override
    protected Class<?>[] entities() {
        return new Class<?>[]{
                Person.class,
                Phone.class,
        };
    }

    @Test
    public void testLifecycle() {
        doInJPA(entityManager -> {
            Person person = new Person(1L);
            person.getPhoneRegister().put(new Phone(PhoneType.LAND_LINE, "028-234-9876"), new Date());
            person.getPhoneRegister().put(new Phone(PhoneType.MOBILE, "072-122-9876"), new Date());
            entityManager.persist(person);
        });
        doInJPA(entityManager -> {
            Person person = entityManager.find(Person.class, 1L);
            Map<Phone, Date> phones = person.getPhoneRegister();
            assertEquals(2, phones.size());
        });
    }

    @Entity(name = "Person")
    public static class Person {

        @Id
        private Long id;

        public Person() {
        }

        public Person(Long id) {
            this.id = id;
        }

        @Temporal(TemporalType.TIMESTAMP)
        @ElementCollection
        @CollectionTable(name="phone_register")
        @Column(name="since")
        @MapKeyJoinColumn(name = "phone_id", referencedColumnName="id")
        private Map<Phone, Date> phoneRegister = new HashMap<>();

        public Map<Phone, Date> getPhoneRegister() {
            return phoneRegister;
        }
    }

    public enum PhoneType {
        LAND_LINE,
        MOBILE
    }

    @Embeddable
    public static class Phone  {

        private PhoneType type;

        private String number;

        public Phone() {
        }

        public Phone(PhoneType type, String number) {
            this.type = type;
            this.number = number;
        }

        public PhoneType getType() {
            return type;
        }

        public String getNumber() {
            return number;
        }
    }
}