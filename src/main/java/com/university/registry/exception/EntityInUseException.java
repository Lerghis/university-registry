package com.university.registry.exception;

public class EntityInUseException extends RegistryException
{
    public EntityInUseException(String entityType, String identifier)
    {
        super(entityType + " με αναγνωριστικό '" + identifier + "' δεν μπορεί να διαγραφεί: εξακολουθεί να χρησιμοποιείται (έχει ενεργές εγγραφές ή αναθέσεις).\n");
    }
}
