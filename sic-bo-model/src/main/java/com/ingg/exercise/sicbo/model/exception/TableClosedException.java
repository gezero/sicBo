package com.ingg.exercise.sicbo.model.exception;

/**
 * <p>
 * A bet cannot currently be accepted by the {@link com.ingg.exercise.sicbo.model.Table}, as the table is not open.
 * </p>
 *
 * @author iKernel Team
 * @author Inspired Gaming Group
 */
public class TableClosedException extends SicBoException {

    public TableClosedException() {
        super();
    }

}
