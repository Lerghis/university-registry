/**
 * Data Access Objects (DAOs). Each DAO is responsible for persistence of a
 * single entity type via JDBC/SQL against MySQL - and nothing else. DAOs do
 * not contain business rules (e.g. "can't enroll if the course is full");
 * that logic belongs in the {@code service} package, which calls the DAOs.
 */
package com.university.registry.dao;
