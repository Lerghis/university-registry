package com.university.registry.exception;

public class EntityInUseException extends RegistryException
{
    public EntityInUseException(String entityType, String identifier)
    {
        super(entityType + " with identifier '" + identifier + "' cannot be deleted: it is still referenced by other records.\n");
    }
}
