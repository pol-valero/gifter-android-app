package com.example.giftr.persistence;

import java.util.List;

public interface GeneralDAO<classType, idType, uniqueType> {

    /**
     * Methods that saves a specific class instance into persistence.
     *
     * @param className	The new class instance to save.
     * @return	Unique ID attributed when the instance was inserted into persistence.
     */
    idType add(classType className);

    /**
     * Method that deletes a specific class instance from persistence.
     *
     * @param classID	The instance to be deleted.
     */
    void delete(idType classID);

    /**
     * Method that reads the information in persistence, returning a specific class instance selected by an ID.
     *
     * @param classID	The ID of the instance to read.
     * @return	The instance read in persistence.
     */
    classType getOne(idType classID);

    /**
     * Method that reads the information in persistence, returning the ID of a specific class instance.
     *
     * @param uniqueField	The field used to read the instance in persistence (must be unique).
     * @return	The instance read in persistence.
     */
    idType getID(uniqueType uniqueField);

    /**
     * Method that reads the information in persistence, returning all persisted class instances.
     *
     * @return	A list containing all stored instances.
     */
    List<classType> getAll();
}