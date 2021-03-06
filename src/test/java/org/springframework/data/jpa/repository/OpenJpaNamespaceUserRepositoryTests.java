/*
 * Copyright 2008-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.jpa.repository;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Parameter;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.data.jpa.domain.sample.User;
import org.springframework.data.jpa.repository.sample.UserRepository;
import org.springframework.test.context.ContextConfiguration;

/**
 * Testcase to run {@link UserRepository} integration tests on top of OpenJPA.
 * 
 * @author Oliver Gierke
 */
@ContextConfiguration(value = "classpath:openjpa.xml", inheritLocations = true)
public class OpenJpaNamespaceUserRepositoryTests extends NamespaceUserRepositoryTests {

	@PersistenceContext
	EntityManager em;

	/**
	 * Ignored until https://issues.apache.org/jira/browse/OPENJPA-2018 gets fixed.
	 */
	@Override
	@Ignore
	public void findsAllByGivenIds() {

	}

	/**
	 * Ignored until https://issues.apache.org/jira/browse/OPENJPA-2018 gets fixed.
	 */
	@Override
	public void handlesIterableOfIdsCorrectly() {
	}

	@Test
	public void checkQueryValidationWithOpenJpa() {

		try {
			em.createQuery("something absurd");
			fail("Creating query did not validate it");
		} catch (Exception e) {
			// expected
		}

		try {
			em.createNamedQuery("not available");
			fail("Creating invalid named query did not validate it");
		} catch (Exception e) {
			// expected
		}
	}

	/**
	 * Test case for https://issues.apache.org/jira/browse/OPENJPA-2018
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	@Ignore
	public void queryUsingIn() {

		flushTestUsers();

		CriteriaBuilder builder = em.getCriteriaBuilder();

		CriteriaQuery<User> criteriaQuery = builder.createQuery(User.class);
		Root<User> root = criteriaQuery.from(User.class);
		criteriaQuery.where(root.<Integer> get("id").in(builder.parameter(Collection.class)));

		TypedQuery<User> query = em.createQuery(criteriaQuery);
		for (Parameter parameter : query.getParameters()) {
			query.setParameter(parameter, Arrays.asList(1, 2));
		}

		List<User> resultList = query.getResultList();
		assertThat(resultList.size(), is(2));
	}
}
