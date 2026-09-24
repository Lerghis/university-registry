package com.university.registry.exception;

public class NoGradesRecordedException extends RegistryException
{
    public NoGradesRecordedException(String entityTYpe, String identifier)
    {
        super (entityTYpe + " με το αναγνωριστικό " + identifier + " δεν έχει καταχωρημένους βαθμούς ακόμη.\n");
    }
}
