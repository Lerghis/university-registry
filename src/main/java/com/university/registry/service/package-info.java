/**
 * Service layer: business logic and validation rules that sit between the
 * UI and the DAO layer. Services orchestrate one or more DAOs, enforce
 * domain rules (duplicate checks, semester validation, grade averaging),
 * and are the only layer the UI controllers should call directly.
 */
package com.university.registry.service;
