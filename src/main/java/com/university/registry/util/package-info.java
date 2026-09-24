/**
 * Cross-cutting utilities shared across layers, such as the database
 * connection factory (HikariCP data source setup). Kept intentionally
 * small - if a class here starts accumulating business logic, it belongs
 * in {@code service} instead.
 */
package com.university.registry.util;
